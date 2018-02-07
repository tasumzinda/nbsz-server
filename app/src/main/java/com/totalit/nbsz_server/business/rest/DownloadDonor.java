package com.totalit.nbsz_server.business.rest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.totalit.nbsz_server.business.domain.Donor;
import com.totalit.nbsz_server.business.util.AppUtil;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tasu on 7/18/17.
 */
public class DownloadDonor extends IntentService {

    public static final String NOTIFICATION = "zw.org.nbsz";
    private Context context = this;
    public static final String RESULT = "result";

    public DownloadDonor(){
        super("DownloadDonor");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        int result = Activity.RESULT_OK;
        try{
                loadDonors(AppUtil.run(AppUtil.getDonorUrl(context), context));
        }catch (IOException ex){
            ex.printStackTrace();
            result = Activity.RESULT_CANCELED;
        }
        publishResults(result);
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    public String loadDonors(String data){
        String msg = "Donor synced";
        try{
            JSONArray array = new JSONArray(data);
            ArrayList<Donor> list = Donor.fromJSON(array);
            for(Donor item : list){
                Donor duplicate = Donor.findByDonorNumber(item.donorNumber);
                if(duplicate == null){
                    item.save();
                    Log.d("Donor", item.firstName + " " + item.surname + " " + item.donorNumber);
                }
            }
        }catch (JSONException ex){
            ex.printStackTrace();
            msg = "Donor sync failed";
        }
        return msg;
    }
}
