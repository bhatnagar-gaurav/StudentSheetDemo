package com.example.test.studentdemo.userinterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.StudentDemoApplication;
import com.example.test.studentdemo.model.Mark;

import java.util.ArrayList;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class StudentDetailsAdapter extends BaseAdapter {
    private StudentDemoApplication mApplication;
    private ArrayList<Mark> mItems;
    private LayoutInflater mInflater;


    public StudentDetailsAdapter(StudentDemoApplication mApplication, ArrayList<Mark> mItems) {
        this.mApplication = mApplication;
        this.mItems = mItems;
        mInflater = LayoutInflater.from(mApplication);

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View studentMarksRow = convertView;
        StudentMarkInfoHolder holder;
        if (studentMarksRow == null){
            studentMarksRow = mInflater.inflate(R.layout.item_student_marks,parent,false);
            holder = new StudentMarkInfoHolder();
            holder.studentSubjectName = (TextView) studentMarksRow.findViewById(R.id.textView_student_subject);
            holder.studentMarks = (TextView) studentMarksRow.findViewById(R.id.textView_student_subject_marks);
            studentMarksRow.setTag(holder);
        }
        else{
            holder=(StudentMarkInfoHolder)studentMarksRow.getTag();
        }

        Mark mark = mItems.get(position);
        holder.studentSubjectName.setText(mark.getSubjectName());
        holder.studentMarks.setText(""+mark.getSubjectMarks());
        return studentMarksRow;
    }



    static class StudentMarkInfoHolder
    {
        TextView studentSubjectName;
        TextView studentMarks;
    }
}
