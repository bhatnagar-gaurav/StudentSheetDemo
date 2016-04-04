package com.example.test.studentdemo.userinterface;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.StudentDemoApplication;
import com.example.test.studentdemo.model.Mark;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class StudentDetailsFragment extends ListFragment implements View.OnClickListener {

    private String studentFullName;
    private StudentDemoApplication mApplication;
    private StudentDetailsAdapter studentDetailsAdapter;


    public StudentDetailsFragment() {
        mApplication = StudentDemoApplication.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_student_marks,container,false);
        TextView studentFullNameTextView = (TextView) contentView.findViewById(R.id.text_view_student_detail_name);
        Button menuButton = (Button) contentView.findViewById(R.id.btn_student_details);
        menuButton.setOnClickListener(this);
        if (studentFullName != null){
            studentFullNameTextView.setText(studentFullName);
        }
        if (this.studentDetailsAdapter != null){
            setListAdapter(this.studentDetailsAdapter);
        }
        return contentView;
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentFullName = getArguments().getString(getString(R.string.tag_student_full_name));
        ArrayList<Mark> studentMarks = getArguments().getParcelableArrayList(getString(R.string.tag_marks));
        if(this.mApplication != null){
            if(studentMarks != null){
                this.studentDetailsAdapter = new StudentDetailsAdapter(this.mApplication, studentMarks);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_student_details:{
                getActivity().getFragmentManager().popBackStackImmediate();
            }
                break;
            default:
                break;

        }
    }
}
