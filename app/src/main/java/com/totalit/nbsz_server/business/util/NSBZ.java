package com.totalit.nbsz_server.business.util;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.app.Application;

/**
 * Created by tasu on 5/4/17.
 */
public class NSBZ extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Configuration configuration = new Configuration.Builder(this).create();
        ActiveAndroid.initialize(configuration);
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    /*@Override
    protected void attachBaseContext(Context context){
        super.attachBaseContext(context);
        MultiDex.install(context);
    }*/
}
