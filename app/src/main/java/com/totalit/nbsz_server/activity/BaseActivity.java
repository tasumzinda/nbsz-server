package com.totalit.nbsz_server.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import com.android.volley.*;
import com.totalit.nbsz_server.R;
import com.totalit.nbsz_server.business.domain.*;
import com.totalit.nbsz_server.business.domain.util.Gender;
import com.totalit.nbsz_server.business.util.AppUtil;
import com.totalit.nbsz_server.business.util.DateUtil;
import com.totalit.nbsz_server.business.rest.DownloadDonor;
import com.totalit.nbsz_server.business.rest.PushPullService;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    public Context context = this;
    public Menu menu;
    public Toolbar toolbar;
    ProgressDialog progressDialog;


    public Toolbar createToolBar(String title) {
        toolbar = (Toolbar) findViewById(com.totalit.nbsz_server.R.id.toolbar);
        toolbar.setTitle(title);
        return toolbar;
    }

    public void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public void updateLabel(Date date, EditText editText) {
        editText.setText(DateUtil.getStringFromDate(date));
    }

    public boolean validate(EditText[] fields) {
        boolean isValid = true;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getText().toString().isEmpty()) {
                fields[i].setError(getResources().getString(com.totalit.nbsz_server.R.string.required_field_error));
                isValid = false;
            } else {
                fields[i].setError(null);
            }
        }
        return isValid;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(com.totalit.nbsz_server.R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent = null;

        if (android.R.id.home == id) {
            onBackPressed();
        }

        /*if (id == R.id.action_home) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }*/
        if (id == com.totalit.nbsz_server.R.id.action_refresh) {
            syncAppData();
            return true;
        }
        /*if (id == R.id.action_list) {
            intent = new Intent(context, DonorListActivity.class);
            startActivity(intent);
            finish();
        }*/
        if (id == com.totalit.nbsz_server.R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item_down = menu.findItem(com.totalit.nbsz_server.R.id.action_refresh);
        item_down.setVisible(true);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(PushPullService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    public void onExit() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("no", null).show();
    }


    public boolean checkDateFormat(String text) {
        if (text == null || !text.matches("([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})"))
            return false;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false);
        try {
            df.parse(text);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    public boolean validateStrings(String name) {
        if (name.trim().matches("^([a-zA-Z])+")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateNationalId(String nationalId) {
        if (nationalId.trim().matches("([0-9]{2})-([0-9]{6,7})([A-Z])([0-9]{2})")) {
            return true;
        } else {
            return false;
        }
    }

    public void syncAppData() {
        if (AppUtil.isNetworkAvailable(context)) {
            progressDialog = ProgressDialog.show(this, "Please wait", "Syncing with Server...", true);
            progressDialog.setCancelable(false);
            Intent intent = new Intent(this, PushPullService.class);
            startService(intent);
        } else {
            AppUtil.createShortNotification(this, "No Internet, Check Connectivity!");
        }
    }

    public void downloadDonors() {
        if (AppUtil.isNetworkAvailable(context)) {
            progressDialog = ProgressDialog.show(this, "Please wait", "Downloading donor data...", true);
            progressDialog.setCancelable(false);
            Intent intent = new Intent(this, DownloadDonor.class);
            startService(intent);
        } else {
            AppUtil.createShortNotification(this, "No Internet, Check Connectivity!");
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            updateView();
            if (bundle != null) {
                int resultCode = bundle.getInt(PushPullService.RESULT);
                if (resultCode == RESULT_OK) {
                    createNotificationDataSync("Sync Success", "Application Data Updated");
                    AppUtil.createShortNotification(context, "Application Data Updated");
                } else {
                    createNotificationDataSync("Sync Fail", "Incomplete Application Data");
                    AppUtil.createShortNotification(context, "Incomplete Application Data");
                }
            }
        }
    };

    public void createNotificationDataSync(String title, String msg) {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(msg).setSmallIcon(R.mipmap.logo)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateView() {

    }

    public void logout() {
        AppUtil.removePreferences(this);
        User user = User.getLoggedIn();
        user.logged_in = 0;
        user.save();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void handleVolleyError(VolleyError error){
        if(error instanceof TimeoutError || error instanceof NoConnectionError){
            AppUtil.createLongNotification(getApplicationContext(), "Connection to server has failed");
        }else if(error instanceof AuthFailureError){
            AppUtil.createLongNotification(getApplicationContext(),"Authentication from server failed");
        }else if(error instanceof NetworkError){
            AppUtil.createLongNotification(getApplicationContext(), "Network error");
        }else if(error instanceof ServerError){
            AppUtil.createLongNotification(getApplicationContext(), "An internal error occurred at the server");
        }else if(error instanceof ParseError){
            AppUtil.createLongNotification(getApplicationContext(), "Error. Failed to parse the network response");
        }
    }

    public static Donor fromJSON(JSONObject object){
        Donor item = new Donor();
        try{
            item.firstName = object.getString("firstName").toUpperCase().trim();
            item.surname = object.getString("surname").toUpperCase().trim();
            if( ! object.isNull("idNumber")){
                item.idNumber = object.getString("idNumber");
            }
            if( ! object.isNull("numberOfDonations")){
                item.numberOfDonations = object.getInt("numberOfDonations");
            }

            if( ! object.isNull("gender")){
                if(object.getString("gender").equals("M") || object.getString("gender").equals("F")){
                    item.gender = Gender.valueOf(object.getString("gender"));
                }
            }

            if( ! object.isNull("dob")){
                item.dateOfBirth = DateUtil.getDateFromString(object.getString("dob"));
                item.dob = DateUtil.formatDate(item.dateOfBirth);
            }

            if( ! object.isNull("deferDate")){
                item.deferDate = DateUtil.getStringFromDate(DateUtil.getDateFromString(object.getString("deferDate")));
            }
            if( ! object.isNull("entry")){
                item.entryDate = DateUtil.getDateFromString(object.getString("entry"));
                item.entry = DateUtil.getStringFromDate(item.entryDate);
            }
            if( ! object.isNull("deferNotes")){
                item.deferNotes = object.getString("deferNotes");
            }
            if( ! object.isNull("deferPeriod")){
                item.deferPeriod = object.getInt("deferPeriod");
            }



            if( ! object.isNull("profession")){
                JSONObject profession = object.getJSONObject("profession");
                item.profession = Profession.findById(profession.getLong("id"));
            }
            if( ! object.isNull("maritalStatus")){
                JSONObject maritalStatus = object.getJSONObject("maritalStatus");
                item.maritalStatus = MaritalStatus.findById(maritalStatus.getLong("id"));
            }
            if(! object.isNull("city")){
                JSONObject city = object.getJSONObject("city");
                item.city = Centre.findById(city.getLong("id"));
            }
            item.residentialAddress = object.getString("residentialAddress");
            item.homeTelephone = object.getString("homeTelephone");
            item.workTelephone = object.getString("workTelephone");
            item.cellphoneNumber = object.getString("cellphoneNumber");
            item.email = object.getString("email");
            if( ! object.isNull("counsellor")){
                JSONObject counsellor = object.getJSONObject("counsellor");
                Counsellor c = new Counsellor();
                c.name = counsellor.getString("name");
                c.address = counsellor.getString("address");
                c.phoneNumber = counsellor.getString("phoneNumber");
                if( ! counsellor.isNull("code")){
                    c.code = counsellor.getString("code");
                }

                if( ! counsellor.isNull("id")){
                    c.serverId = counsellor.getLong("id");
                    Counsellor duplicate = Counsellor.findById(c.serverId);
                    if(duplicate == null){
                        c.save();
                    }
                    Log.d("Saved counsellor", c.name);
                }

                item.counsellor = c;
            }
            if( ! object.isNull("deferredReason")){
                JSONObject deferredReason = object.getJSONObject("deferredReason");
                item.deferredReason = DeferredReason.findById(deferredReason.getLong("id"));
            }
            if( ! object.isNull("collectSite")){
                JSONObject collectSite = object.getJSONObject("collectSite");
                item.collectSite = CollectSite.findById(collectSite.getLong("id"));
            }
            if( ! object.isNull("donationType")){
                JSONObject donationType = object.getJSONObject("donationType");
                item.donationType = DonationType.findById(donationType.getLong("id"));
            }
            if( ! object.isNull("id")){
                item.server_id = object.getLong("id");
            }
            if( ! object.isNull("donorNumber")){
                item.donorNumber = object.getString("donorNumber");
            }
            item.entryTime = object.getString("timeEntry");
            if( ! object.isNull("bledBy")){
                JSONObject bledBy = object.getJSONObject("bledBy");
                User user = User.findById(bledBy.getLong("id"));
                item.bledBy = user;
            }
            if( ! object.isNull("accepted")){
                item.accepted = object.getString("accepted");
            }

        }catch (JSONException ex){
            ex.printStackTrace();
            return null;
        }
        return item;
    }
}