package com.example.test.studentdemo.userinterface;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.StudentDemoApplication;
import com.example.test.studentdemo.model.Student;
import com.example.test.studentdemo.model.StudentsList;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class StudentListAdapter extends BaseAdapter {
    private static final String TAG = StudentListAdapter.class.getSimpleName();
    StudentDemoApplication mApplication;
    Picasso picassoInstance;

    public StudentListAdapter(StudentDemoApplication mApplication){
        this.mApplication = mApplication;
        picassoInstance = this.mApplication.getPicassoInstance(mApplication);

    }

    public static abstract class Row {
    }

    public static final class Section extends Row {
        public final String text;
        public Section(String text) {
            this.text = text;
        }
    }

    public static final class Item extends Row {
        public final Student item;
        public Item(Student item) {
            this.item = item;
        }
    }

    private List rows;

    public void setRows(List rows) {
        this.rows = rows;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Object getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        this.mApplication = StudentDemoApplication.getInstance();
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (getItemViewType(position)== 0){
            if (view == null){
                view = inflater.inflate(R.layout.item_students,
                        parent, false);
                view.setMinimumHeight(105);
            }
            Item item = (Item) getItem(position);
            TextView studentName = (TextView) view
                    .findViewById(R.id.textView_student_name);
            if (item.item.getlName() != null && item.item.getfName() != null){
                studentName.setText(item.item.getlName().concat(" " + item.item.getfName()));
            }
            else if (item.item.getlName() == null){
                if (item.item.getfName() != null){
                    studentName.setText(item.item.getfName());
                }
                else{
                    studentName.setText("");
                }
            }
            TextView student_roll_no = (TextView) view
                    .findViewById(R.id.textView_student_roll_no);
            final ImageView profilePicView = (ImageView) view.findViewById(R.id.studentProfilePic);
            if (item.item.getRollNo() != null){
                student_roll_no.setText(item.item.getRollNo());
            }
            else{
                student_roll_no.setText("");
            }

            // Lazy Loading the thumbnails For each Row including the caching mechanism.
            picassoInstance.with(this.mApplication)
                    .load(item.item.getProfilePic())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .error(R.mipmap.placeholder)
                    .placeholder(R.mipmap.placeholder)
                    .fit()
                    .into(profilePicView, new Callback() {
                        Item item = (Item) StudentListAdapter.this.getItem(position);
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            picassoInstance.with(mApplication)
                                    .load(item.item.getProfilePic())
                                    .error(R.mipmap.placeholder)
                                    .placeholder(R.mipmap.placeholder)
                                    .fit()
                                    .into(profilePicView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            Log.v(TAG, "Could not fetch image");
                                        }
                                    });
                        }
                    });
        }
        else { // Section
            if (view == null) {
                view = inflater.inflate(
                        R.layout.selection_students, parent, false);
            }
            Section section = (Section) getItem(position);
            TextView textView = (TextView) view
                    .findViewById(R.id.textView_selection);
            textView.setText(section.text);
        }
        return view;
    }

    /*private class StudentFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {

        }

    }*/
}
