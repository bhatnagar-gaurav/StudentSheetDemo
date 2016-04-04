package com.example.test.studentdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class Student extends BasicResponse implements Parcelable {
    public String fName;
    public String lName;
    public String rollNo;
    public ArrayList<Mark> marks;
    public String profilePic;

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public ArrayList<Mark> getMarks() {
        return marks;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public Student(String fName, String lName, ArrayList<Mark> marks, String rollNo, String profilePic) {

        this.fName = fName;
        this.lName = lName;
        this.marks = marks;
        this.rollNo = rollNo;
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fName);
        dest.writeString(this.lName);
        dest.writeString(this.rollNo);
        dest.writeTypedList(marks);
        dest.writeString(this.profilePic);
    }

    protected Student(Parcel in) {
        this.fName = in.readString();
        this.lName = in.readString();
        this.rollNo = in.readString();
        this.marks = in.createTypedArrayList(Mark.CREATOR);
        this.profilePic = in.readString();
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
