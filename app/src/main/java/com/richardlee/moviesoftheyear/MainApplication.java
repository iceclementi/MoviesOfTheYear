package com.richardlee.moviesoftheyear;

import android.app.Application;
import android.content.Context;
//import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MainApplication extends Application {

    public static RefWatcher getRefWatcher(Context context) {
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();


        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        // Normal app init code..
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //MultiDex.install(this);
    }
}