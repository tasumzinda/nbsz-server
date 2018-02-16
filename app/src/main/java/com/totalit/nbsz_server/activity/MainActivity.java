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
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.totalit.nbsz_server.R;
import com.totalit.nbsz_server.business.domain.*;
import com.totalit.nbsz_server.business.domain.util.UUIDGen;
import com.totalit.nbsz_server.business.util.AppUtil;
import com.totalit.nbsz_server.business.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    String result;

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
        setSupportActionBar(createToolBar("NBSZ"));
        for(Donor m : Donor.getAll()){
            Log.d("Donor", AppUtil.createGson().toJson(m));
        }
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
                        JSONObject obj = null;
                        try{
                            obj = new JSONObject(stringData);
                            String requestType = obj.getString("requestType");
                            if(requestType.equals("POST_DONOR")){
                                String id = saveDonorData(obj);
                                result = id;
                            }
                            if(requestType.equals("POST_STATISTICS")){
                                String id = saveDonationStats(obj);
                                result = id;
                            }
                            if(requestType.equals("POST_DONATION")){
                                result = saveDonation(obj);
                            }
                            if(requestType.equals("POST_OFFER")){
                                result = saveOffer(obj);
                            }
                            if(requestType.equals("donorNumber")){
                                String donorNumber = obj.getString("donorNumber");
                                Donor item = Donor.findByDonorNumber(donorNumber);
                                if(item != null){
                                    result = AppUtil.createGson().toJson(item);
                                }else{
                                    result = "Not found";
                                }
                            }
                            if(requestType.equals("idNumber")){
                                String idNumber = obj.getString("idNumber");
                                Donor item = Donor.findByNationalId(idNumber);
                                if(item != null){
                                    result = AppUtil.createGson().toJson(item);
                                }else{
                                    result = "Not found";
                                }
                            }
                            if(requestType.equals("nameDob")){
                                String firstName = obj.getString("firstName");
                                String surname = obj.getString("surname");
                                String dob = obj.getString("dob");
                                List<Donor> donors = new ArrayList<>();
                                if( ! firstName.isEmpty()){
                                    donors = Donor.findByFirstNameAndLastNameAndDateOfBirth(firstName.toUpperCase(), surname.toUpperCase(), dob);
                                }else{
                                    donors = Donor.findByLastNameAndDateOfBirth(surname.toUpperCase(), dob);
                                }
                                if(donors.size() > 0){
                                    List<Donor> toSend = new ArrayList<>();
                                    for(Donor m : donors){
                                        m.genderValue = m.gender.getName();
                                        toSend.add(m);
                                    }
                                    result = AppUtil.createGson().toJson(toSend);
                                }else{
                                    result = "Not found";
                                }
                            }
                            if(requestType.equals("todayDonations")){
                                String date = obj.getString("date");
                                List<Donor> list = Donor.findTodayDonations(date);
                                if(list != null){
                                    List<Donor> toSend = new ArrayList<>();
                                    for(Donor m : list){
                                        m.genderValue = m.gender.getName();
                                        toSend.add(m);
                                    }
                                    result = AppUtil.createGson().toJson(toSend);
                                    Log.d("Fetched Donors", result);
                                }else{
                                    result = "Not Found";
                                }
                            }
                            if(requestType.equals("donations")){
                                String localId = obj.getString("localId");
                                Donor donor = Donor.findByLocalId(localId);
                                List<Donation> list = Donation.findByDonor(donor);
                                if(list != null){
                                    List<Donation> toSend = new ArrayList<>();
                                    for(Donation m : list){
                                        m.donationDate = DateUtil.getStringFromDate(m.date);
                                        toSend.add(m);
                                    }
                                    result = AppUtil.createGson().toJson(toSend);
                                    Log.d("Fetched donations", result);
                                }else{
                                    result = "Not found";
                                }
                            }
                            if(requestType.equals("offers")){
                                String localId = obj.getString("localId");
                                Donor donor = Donor.findByLocalId(localId);
                                List<Offer> list = Offer.findByDonor(donor);
                                if(list != null){
                                    List<Offer> toSend = new ArrayList<>();
                                    for(Offer offer : list){
                                        offer.offer = DateUtil.getStringFromDate(offer.offerDate);
                                        if(offer.deferDate != null){
                                            offer.defer = DateUtil.getStringFromDate(offer.deferDate);
                                        }
                                        List<Incentive> incentives = Incentive.findByOffer(offer);
                                        offer.incentives = incentives;
                                        toSend.add(offer);
                                    }
                                    result = AppUtil.createGson().toJson(toSend);
                                    Log.d("Fetched offers", result);
                                }else{
                                    result = "Not found";
                                }
                            }
                            if(requestType.equals("donationStats")){
                                String localId = obj.getString("localId");
                                Donor donor = Donor.findByLocalId(localId);
                                List<DonationStats> list = DonationStats.findByDonor(donor);
                                if(list != null){
                                    result = AppUtil.createGson().toJson(list);
                                    Log.d("donationStats offers", result);
                                }else{
                                    result = "Not found";
                                }
                            }
                        }catch (JSONException ex){
                            ex.printStackTrace();
                            textViewIpAccess.setText(ex.getMessage());
                        }
                        output.println(result);
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

    public String saveDonorData(JSONObject object){
        JSONArray array = null;
        Donor item = Donor.fromJSON(object);
        if(item.localId != null){
            Donor res = Donor.findByLocalId(item.localId);
            item.save();
            for(Donation m : Donation.findByDonor(res)){
                m.person = item;
                m.save();
            }
            for(DonationStats m : DonationStats.findByDonor(res)){
                m.person = item;
                m.save();
            }
            for(Offer m : Offer.findByDonor(res)){
                m.person = item;
                m.save();
            }
            res.delete();
            try{
                if( ! object.isNull("specialNotes")){
                    array = object.getJSONArray("specialNotes");
                    if(array != null){
                        ArrayList<SpecialNotes> specialNotes = SpecialNotes.fromJSON(array);
                        for(SpecialNotes note : specialNotes){
                            DonorSpecialNotesContract contract = new DonorSpecialNotesContract();
                            contract.donor = item;
                            contract.specialNotes = note;
                            contract.save();
                        }
                    }
                }

            }catch (JSONException ex){
                ex.printStackTrace();
            }

        }else{
            item.localId = UUIDGen.generateUUID();
            item.save();
        }
        return item.localId;
    }

    public String saveDonationStats(JSONObject object){
        DonationStats item = DonationStats.fromJSON(object);
        Log.d("Object", object.toString());
        Log.d("item", AppUtil.createGson().toJson(item));
        if(item.localId != null){
            DonationStats res = DonationStats.findByLocalId(item.localId);
            Log.d("Res", AppUtil.createGson().toJson(res));
            res.delete();
            Log.d("Deleted res", AppUtil.createGson().toJson(res));
            //item = res;
            item.person = Donor.findByLocalId(item.person.localId);
            item.save();
        }else{
            item.localId = UUIDGen.generateUUID();
            item.person = Donor.findByLocalId(item.person.localId);
            item.save();
        }
        return item.localId;
    }

    public String saveDonation(JSONObject object){
        Donation item = Donation.fromJSON(object);
        item.person = Donor.findByLocalId(item.person.localId);
        item.save();
        return "OK";
    }

    public String saveOffer(JSONObject object){
        Offer item = Offer.fromJSON(object);
        ArrayList<Incentive> incentives = (ArrayList<Incentive>) item.incentives;
        item.person = Donor.findByLocalId(item.person.localId);
        item.save();
        for(Incentive incentive : incentives){
            OfferIncentiveContract contract = new OfferIncentiveContract();
            contract.offer = item;
            contract.incentive = Incentive.findById(incentive.serverId);
            contract.save();
        }
        return "OK";
    }
}
