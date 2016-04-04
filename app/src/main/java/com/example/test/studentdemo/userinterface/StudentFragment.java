package com.example.test.studentdemo.userinterface;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.StudentDemoApplication;
import com.example.test.studentdemo.api.WebServiceCallbacks;
import com.example.test.studentdemo.api.WebServiceFragment;
import com.example.test.studentdemo.model.Student;
import com.example.test.studentdemo.model.StudentsList;
import com.example.test.studentdemo.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class StudentFragment extends ListFragment implements WebServiceCallbacks,AdapterView.OnItemClickListener {

    private static final String TAG = StudentFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.
    private static final String KEY_DATA_SET = "DATA_SET";
    private static final String TAG_WEB_SERVICE_FRAGMENT = "web_service_fragment";
    private StudentListAdapter adapter;
    private ArrayList<Student> studentList;
    private StudentDemoApplication mApplication;
    private ProgressDialog progressDialog;
    private WebServiceFragment mWebServiceFragment;
    private EditText mSearchEditStudentText;
    private boolean searchStudentFunctionality = false;

    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    private View contentView;
    private ArrayList<Student> searchStudentList;
    int searchTextLength;

    public StudentFragment(){
        mApplication = StudentDemoApplication.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.mApplication != null){
            adapter = new StudentListAdapter(this.mApplication);
        }
        else{
            this.mApplication = StudentDemoApplication.getInstance();
            adapter = new StudentListAdapter(this.mApplication);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        mWebServiceFragment = (WebServiceFragment) fragmentManager.findFragmentByTag(TAG_WEB_SERVICE_FRAGMENT);

        // If the Fragment is non-null, then it is being retained
        // over a configuration change.
        if (mWebServiceFragment == null) {
            mWebServiceFragment = new WebServiceFragment();
            mWebServiceFragment.setTargetFragment(this,0);
            fragmentManager.beginTransaction().add(mWebServiceFragment, TAG_WEB_SERVICE_FRAGMENT).commit();
        }
        if (null == savedInstanceState){
            mWebServiceFragment.start();
        }
        // Restore saved state.
        else{
            this.studentList = savedInstanceState.getParcelableArrayList(KEY_DATA_SET);
            if (this.studentList != null){
                this.populateListView(this.studentList);
                // Clearing the Search Edit Text
                if (!this.mSearchEditStudentText.getText().toString().equalsIgnoreCase("")){
                    this.mSearchEditStudentText.setText("");
                }
                // Enabling the search Functionality
                searchStudentList = new ArrayList<Student>(this.studentList.size());
                enableSearchFunctionality();
            }
            else{
                Toast.makeText(mApplication,"WebService getting attached to the new recreated fragment",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
            Log.i(TAG, "onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);
        if (this.studentList != null){
            outState.putParcelableArrayList(KEY_DATA_SET,studentList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_student, container, false);
        mSearchEditStudentText = (EditText) contentView
                .findViewById(R.id.text_edit_search_students);
        String searchByItems = getResources().getString(R.string.search_student_name);
        mSearchEditStudentText.setHint(searchByItems);
        mSearchEditStudentText.setSingleLine(true);
        mSearchEditStudentText.setText("");
        if (alphabet.size() > 0){
            this.initialize();
        }
        return contentView;
    }



    @Override
    public void onPreExecute() {
        // show spinner
       // progressDialog = ProgressDialog.show(mApplication,"","Loading Images");
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(Object response) {
        // hide spinner
       /* if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }*/
        // In case of Null response.
        if (response == null){
            Utils.showErrorDialog(mApplication, getString(R.string.error_unexpected));
        }
        else{
            StudentsList studentsList = (StudentsList)response;
            if (studentsList.returncode == 1){
                if (studentsList.studentsList != null){
                    if (studentsList.studentsList.size() != 0){
                        // Initializing the Data-set
                        this.studentList = studentsList.studentsList;
                        populateListView(this.studentList);
                        // Enabling the search Functionality for the List View
                        searchStudentList = new ArrayList<Student>(this.studentList.size());
                        enableSearchFunctionality();

                    }
                    else{
                        // The scenario when there are no students in the Array List.
                        Toast.makeText(getActivity(), "There are no contacts",
                                Toast.LENGTH_LONG).show();
                        updateList();
                    }
                }
                else{
                    Utils.showErrorDialog(mApplication,getString(R.string.error_unexpected));
                }
            }
            else{
                Utils.showErrorDialog(mApplication, studentsList.message);
            }
        }
    }

    //Helper Method for Populating the List View with the given results. .
    public void populateListView(ArrayList<Student> studentsList){
        // Sorting the arraylist via student Last name
        Collections.sort(studentsList, new CustomStudentComparator());
        List rows = new ArrayList();
        int start = 0;
        int end;
        String previousLetter = null;
        Object[] tmpIndexItem;
        Pattern numberPattern = Pattern.compile("[0-9]");
        for (Student student : studentsList) {
            String firstLetter = student.getlName().substring(0, 1);
            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }
            // If we've changed to a new letter, add the previous letter
            // to the
            // alphabet scroller
            firstLetter = firstLetter.toUpperCase();
            if (previousLetter != null
                    && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);
                start = end + 1;
            }
            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new StudentListAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }
            // Add the contact to the list
            rows.add(new StudentListAdapter.Item(student));
            previousLetter = firstLetter;
        }
        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }
        adapter.setRows(rows);
        setListAdapter(adapter);
        this.getListView().setOnItemClickListener(this);
        updateList();
    }

    // Helper Method For Enabling the search Functionality. Not Thoroughly optimized but a working solution for now.
    private void enableSearchFunctionality(){
        mSearchEditStudentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Retrieving the Text in the Search Edit Text
                String searchString = mSearchEditStudentText.getText().toString();
                searchTextLength = searchString.length();
                if (searchTextLength >= 3){
                    // Clearing the search Data set
                    searchStudentList.clear();
                    for (int searchCounter =0;searchCounter<studentList.size();searchCounter++){
                        String studentLastName = studentList.get(searchCounter).getlName();
                        if (searchTextLength <= studentLastName.length()){
                            //Comparing the Search String in EditText with Student Last Names in the ArrayList
                            if(searchString.trim().equalsIgnoreCase(studentLastName.substring(0,searchTextLength)))
                                searchStudentList.add(studentList.get(searchCounter));
                        }
                    }
                    // populating the listView with search results
                    if (searchStudentList.size() > 0){
                        if (alphabet.size() > 0){
                            initialize();
                        }
                        populateListView(searchStudentList);
                    }
                    Utils.saveBoolValueForName(Utils.SETTING_IS_SEARCH_FRAGMENT,true);

                }
                // Coming Back to the original List View.
                else if (searchTextLength == 0){
                    Utils.saveBoolValueForName(Utils.SETTING_IS_SEARCH_FRAGMENT,false);
                    clearSearchFunctionality();
                }
            }
        });
    }

    // From the ItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
        if (adapter != null){
            if (adapter.getItemViewType(position)==0){
                StudentListAdapter.Item item = (StudentListAdapter.Item) adapter.getItem(position);
                StudentDetailsFragment studentMarksFragment = new StudentDetailsFragment();
                Bundle dataBundle = new Bundle();
                dataBundle.putParcelableArrayList(getString(R.string.tag_marks),item.item.getMarks());
                dataBundle.putString(getString(R.string.tag_student_full_name), item.item.getlName().concat(" "+item.item.getfName()));
                studentMarksFragment.setArguments(dataBundle);
                FragmentTransaction transaction = getActivity().getFragmentManager()
                        .beginTransaction();
                transaction.replace(android.R.id.content, studentMarksFragment);
                transaction.addToBackStack("studentSubjectDetails");
                transaction.commit();
            }
        }
    }

    // Helper Method for Clearing the Search Functionality
    public void clearSearchFunctionality(){
        if (!Utils.getBoolForName(Utils.SETTING_IS_SEARCH_FRAGMENT,false)){
            if (!this.mSearchEditStudentText.getText().toString().equalsIgnoreCase("")){
                this.mSearchEditStudentText.setText("");
            }
            if (alphabet.size() > 0){
                this.initialize();
            }
            if (this.studentList != null){
                populateListView(this.studentList);
            }
        }

    }



    /* Classes and Methods for sorting the student Data set by Name. */

    class SideIndexGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // we know already coordinates of first touch
            // we know as well a scroll distance
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            // when the user scrolls within our side index
            // we can show for every position in it a proper
            // item in the country list
            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) contentView
                .findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;
        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);
        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            //noinspection SuspiciousMethodCalls as this method-Call is required for the logic to run seamlessly.
            int subitemPosition = sections.get(indexItem[0]);
            getListView().setSelection(subitemPosition);
        }
    }

    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) contentView
                .findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }
        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }
        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();
            tmpTV = new TextView(contentView.getContext());
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            tmpTV.setTextColor(Color.BLUE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }
        sideIndexHeight = sideIndex.getHeight();
        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();
                // and can display a proper item it country list
                displayListItem();
                return false;
            }
        });
    }


    private void initialize (){
        setListAdapter(null);
        int length = alphabet.size();
        if (length > 0){
            for (int i=(length-1) ; i >= 0 ;i--){
                alphabet.remove(i);
            }
        }
    }
}
