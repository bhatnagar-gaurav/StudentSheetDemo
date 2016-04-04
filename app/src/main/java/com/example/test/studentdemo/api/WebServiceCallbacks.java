package com.example.test.studentdemo.api;

/**
 * Created by gaurav_bhatnagar on 3/7/2016.
 */
public interface WebServiceCallbacks {
    /**
     * Callback interface through which the fragment can report the task's
     * progress and results back to the Activity.
     */

    void onPreExecute();
    void onCancelled();
    void onPostExecute(Object result);

}
