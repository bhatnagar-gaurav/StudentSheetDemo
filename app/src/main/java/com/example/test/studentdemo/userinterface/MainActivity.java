package com.example.test.studentdemo.userinterface;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.test.studentdemo.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = true; // Set this to false to disable logs.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, new StudentFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //-------- CALLBACK METHODS FROM ACTIVITY --------//

    @Override
    protected void onStart() {
        if (DEBUG) Log.i(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (DEBUG) Log.i(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (DEBUG) Log.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (DEBUG) Log.i(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }

}
