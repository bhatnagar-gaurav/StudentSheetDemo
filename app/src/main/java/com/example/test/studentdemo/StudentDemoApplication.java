package com.example.test.studentdemo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class StudentDemoApplication extends Application {
    private static final String TAG = "StudentDemoApplication";
    private static StudentDemoApplication application = null;
    private static Picasso picassoInstance = null;

    public StudentDemoApplication(){

    }
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    /**
     *   Incorporating Image Caching for Picasso Library
     * @param context application Context
     */
    private Picasso customizePicassoCache (Context context) {

        Downloader downloader   = new OkHttpDownloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(downloader);
        picassoInstance = builder.build();
        picassoInstance.setLoggingEnabled(true);
        picassoInstance.setIndicatorsEnabled(true);
        //Also using singleton approach for Picasso instance.
        Picasso.setSingletonInstance(picassoInstance);
        return picassoInstance;
    }
    /**
     * Retrieve Singleton Picasso Instance
     * @param context application Context
     * @return Picasso instance
     */
    public Picasso getPicassoInstance (Context context) {

        if (picassoInstance == null) {
            picassoInstance = customizePicassoCache(context);
        }

        return picassoInstance;
    }

    public static StudentDemoApplication getInstance() {
        return application;
    }

    /**
     * Gets application version.
     * @return string represents application version.
     */
    public String getVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf(TAG, e);
        }
        return (info != null ? info.versionName : "");
    }

}
