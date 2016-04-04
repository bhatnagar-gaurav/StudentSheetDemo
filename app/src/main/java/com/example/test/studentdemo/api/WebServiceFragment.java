package com.example.test.studentdemo.api;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.model.Mark;
import com.example.test.studentdemo.model.Student;
import com.example.test.studentdemo.model.StudentsList;
import com.example.test.studentdemo.network.IOExceptionWithContent;
import com.example.test.studentdemo.network.NetworkUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class WebServiceFragment extends Fragment {
    private static final String TAG = WebServiceFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private WebServiceCallbacks parentCallbacks;
    private WebServiceTask webServiceTask;
    private boolean taskRunning;

    public WebServiceFragment() {
        super();
    }

    /**
     * Hold a reference to the target fragment so we can report the web service task's
     * progress and results. This specific operation is done in this method since it is
     * guaranteed to be the first method called after any type of config change
     * occurs (It must be taken into account that, the StudentFragment will be recreated after each configuration
     * change, so we will need to obtain a reference to the new instance).
     */
    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach(Activity)");
        super.onAttach(activity);
        if (!(getTargetFragment() instanceof WebServiceCallbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }
        // Hold a reference to the parent Activity so we can report back the task's
        // current progress and results.
        parentCallbacks = (WebServiceCallbacks) getTargetFragment();
    }


    /**
     * This method is called once when the Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Note that this method is not called when the Fragment is being
     * retained across Activity instances. It will only be called when its
     * parent Activity is being destroyed for good (such as when the user clicks
     * the back button, etc.).
     */
    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy()");
        super.onDestroy();
        cancel();
    }

    /***The Functions of Web Service Fragment *****/
    /**
     * Start the background task.
     */
    public void start() {
        if (!taskRunning) {
            webServiceTask = new WebServiceTask();
            webServiceTask.execute();
            taskRunning = true;
        }
    }

    /**
     * Cancel the background task.
     */
    public void cancel() {
        if (taskRunning) {
            webServiceTask.cancel(false);
            webServiceTask = null;
            taskRunning = false;
        }
    }

    /**
     * Returns the current state of the background task.
     */
    public boolean isRunning() {
        return taskRunning;
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        parentCallbacks = null;
    }



    /***** BACKGROUND WEBSERVICE TASK *****/

    /**
     * A Background  task that performs work and sends across progress
     * updates along with results back to the Activity.
     */
    private class WebServiceTask extends AsyncTask<Void, Void, StudentsList> {

        @Override
        protected void onPreExecute() {
            // Proxy the call to the Activity.
            if (parentCallbacks != null){
                parentCallbacks.onPreExecute();
                taskRunning = true;
            }
        }

        /**
         * We can-not call any activity objects from the method given below
         */
        @Override
        protected StudentsList doInBackground(Void... ignore) {
            StudentsList studentsList= new StudentsList();
            JSONArray studentsResponse;
            ArrayList<Mark> studentMarks;
            // HTTP Get
            try {
                String response;
                List<NameValuePair> headers = new ArrayList<NameValuePair>();
                headers.add(new BasicNameValuePair("Content-Type", "application/json"));
                response = NetworkUtils.get(Constants.URL_LOADING_STUDENTS, headers, null);
                // parse the json
                if (response != null)
                {
                    /*Initialize arrayList if null*/
                    studentsList.studentsList = new ArrayList<Student>();
                    studentsResponse = new JSONArray(response);
                    for (int i = 0; i < studentsResponse.length(); i++) {
                        JSONObject studentJsonObject = studentsResponse.optJSONObject(i);
                        JSONArray marksList = studentJsonObject.optJSONArray(getString(R.string.tag_marks));
                        if (marksList.length()!=0){
                            studentMarks = new ArrayList<Mark>(marksList.length());
                            for (int subjectCounter = 0;subjectCounter < marksList.length();subjectCounter++){
                                JSONObject studentMarkJsonObject = marksList.optJSONObject(subjectCounter);
                                Mark subjectMarks = new Mark(studentMarkJsonObject.optString(getString(R.string.tag_subject_name)),studentMarkJsonObject.optInt(getString(R.string.tag_marks)));
                                studentMarks.add(subjectMarks);
                            }
                        }
                        else{
                            studentMarks = new ArrayList<Mark>();
                        }
                        Student student = new Student(studentJsonObject.optString(getString(R.string.tag_student_first_name)),studentJsonObject.optString(getString(R.string.tag_student_last_name)),studentMarks,
                                studentJsonObject.optString(getString(R.string.tag_roll_no)),studentJsonObject.optString(getString(R.string.tag_profile_pic)));
                        studentsList.studentsList.add(student);
                    }
                    studentsList.returncode = 1;
                    studentsList.action = Constants.ACTION_GET_STUDENTS;
                    studentsList.message = "Successful";
                }
                else {
                    studentsList.returncode = -1;
                    studentsList.message = "Invalid Response.Please try again.";
                }
            }
            // In case of
            catch(JSONException jsonException){
                Log.w(TAG, jsonException);
                studentsList.returncode = -1;
                if (jsonException.getMessage()!= null){
                    studentsList.message = jsonException.getMessage();
                }
                else if (jsonException.getLocalizedMessage()!= null){
                    studentsList.message = jsonException.getLocalizedMessage();
                }
                else{
                    studentsList.message = jsonException.getCause().toString();
                }
            }
            catch(IOExceptionWithContent ioExceptionWithContent){
                Log.w(TAG,ioExceptionWithContent);
                studentsList.returncode = -1;
                studentsList.message = ioExceptionWithContent.mContent;
            }
            catch (Exception e) {
                Log.w(TAG, e);
                studentsList.returncode = -1;
                studentsList.message = e.getMessage();
            }

            return studentsList;
        }

        @Override
        protected void onCancelled() {
            // Proxy the call to the Activity.
            if (parentCallbacks != null) {
                parentCallbacks.onCancelled();
                taskRunning = false;
            }
        }

        @Override
        protected void onPostExecute(StudentsList result) {
            // Proxy the call to the Activity.
            if (parentCallbacks != null){
                parentCallbacks.onPostExecute(result);
                taskRunning = false;
            }
        }
    }

}
