package com.totalit.nbsz_server.activity;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.totalit.nbsz_server.R;
import com.totalit.nbsz_server.business.domain.Donor;
import com.totalit.nbsz_server.business.util.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends BaseActivity {

    private static final int DEFAULT_PORT = 9002;

    // INSTANCE OF ANDROID WEB SERVER
    private ServerSocket androidWebServer;
    private BroadcastReceiver broadcastReceiverNetworkState;
    private static boolean isStarted = false;

    // VIEW
    private CoordinatorLayout coordinatorLayout;
    private EditText editTextPort;
    private FloatingActionButton floatingActionButtonOnOff;
    private View textViewMessage;
    private TextView textViewIpAccess;
    private boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String download = intent.getStringExtra("download");
        if(download != null && ! download.isEmpty()){
            downloadDonors();
        }

        // INIT VIEW
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        textViewMessage = findViewById(R.id.textViewMessage);
        textViewIpAccess = (TextView) findViewById(R.id.textViewIpAccess);
        setIpAccess();
        floatingActionButtonOnOff = (FloatingActionButton) findViewById(R.id.floatingActionButtonOnOff);
        floatingActionButtonOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectedInWifi()) {
                    if (!isStarted && startAndroidWebServer()) {
                        isStarted = true;
                        textViewMessage.setVisibility(View.VISIBLE);
                        floatingActionButtonOnOff.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorGreen));
                        editTextPort.setEnabled(false);
                    } else if (stopAndroidWebServer()) {
                        isStarted = false;
                        textViewMessage.setVisibility(View.INVISIBLE);
                        floatingActionButtonOnOff.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorRed));
                        editTextPort.setEnabled(true);
                    }
                } else {
                    Snackbar.make(coordinatorLayout, getString(R.string.wifi_message), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // INIT BROADCAST RECEIVER TO LISTEN NETWORK STATE CHANGED
        initBroadcastReceiverNetworkStateChanged();
    }

    //region Start And Stop AndroidWebServer
    private boolean startAndroidWebServer() {
        if (!isStarted) {
            int port = getPortFromEditText();
            try {
                if (port == 0) {
                    throw new Exception();
                }
                androidWebServer = new ServerSocket(port);
                startServerSocket();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(coordinatorLayout, "The PORT " + port + " doesn't work, please change it between 1000 and 9999.", Snackbar.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private boolean stopAndroidWebServer() {
        if (isStarted && androidWebServer != null) {
            //androidWebServer.stop();
            return true;
        }
        return false;
    }
    //endregion

    //region Private utils Method
    private void setIpAccess() {
        textViewIpAccess.setText(getIpAccess());
    }

    private void initBroadcastReceiverNetworkStateChanged() {
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        broadcastReceiverNetworkState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setIpAccess();
            }
        };
        super.registerReceiver(broadcastReceiverNetworkState, filters);
    }

    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":";
    }

    private int getPortFromEditText() {
        String valueEditText = editTextPort.getText().toString();
        return (valueEditText.length() > 0) ? Integer.parseInt(valueEditText) : DEFAULT_PORT;
    }

    public boolean isConnectedInWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }
    //endregion

    public boolean onKeyDown(int keyCode, KeyEvent evt) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isStarted) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.dialog_exit_message)
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                        .show();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAndroidWebServer();
        isStarted = false;
        if (broadcastReceiverNetworkState != null) {
            unregisterReceiver(broadcastReceiverNetworkState);
        }
    }

    private void startServerSocket() {
        Thread thread = new Thread(new Runnable() {
            private String stringData = null;
            @Override
            public void run() {
                try {
                    while (!end) {
                        //Server is waiting for client here, if needed
                        Socket s = androidWebServer.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());
                        stringData = input.readLine();
                        Log.d("Data", stringData);
                        JSONObject obj = null;
                        try{
                            obj = new JSONObject(stringData);
                            String requestType = obj.getString("requestType");
                            if(requestType.equals("POST_DONOR")){
                                saveDonorData(obj);
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                            textViewIpAccess.setText(ex.getMessage());
                        }
                        output.println("FROM SERVER - " + stringData.toUpperCase());
                        output.flush();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //updateUI(stringData);
                        if (stringData.equalsIgnoreCase("STOP")) {
                            end = true;
                            output.close();
                            s.close();
                            break;
                        }

                        output.close();
                        s.close();
                    }
                    androidWebServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    textViewIpAccess.setText(e.getMessage());
                }
            }

        });
        thread.start();
    }

    public void saveDonorData(JSONObject object){
        Log.d("JSON", object.toString());
        Donor item = fromJSON(object);
        Log.d("Donor", AppUtil.createGson().toJson(item));
        item.save();
    }
}
