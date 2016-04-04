package com.example.test.studentdemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.StudentDemoApplication;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 * This File is for Different Helper Methods used in the application.
 */
public class Utils {
    static public final int NOT_EXISTS = -1;
    static private final String SHARED_PREFERENCES = "SETTINGS";
    public static final String SETTING_IS_SEARCH_FRAGMENT = "SEARCH_STUDENT_FRAGMENT";

    public static void saveBoolValueForName(final String name, boolean value) {
        SharedPreferences settings = StudentDemoApplication.getInstance().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public static boolean getBoolForName(final String name, boolean defaultValue) {
        SharedPreferences settings = StudentDemoApplication.getInstance().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return settings.getBoolean(name, defaultValue);
    }

    public static void showErrorDialog(Context context, String message) {

        if (context != null){
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.error_dialog_title))
                    .setMessage(message).setNegativeButton(context.getString(R.string.close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) { }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            StudentDemoApplication mApplication = StudentDemoApplication.getInstance();
            new AlertDialog.Builder(mApplication)
                    .setTitle(mApplication.getString(R.string.error_dialog_title))
                    .setMessage(message).setNegativeButton(mApplication.getString(R.string.close),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) { }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
