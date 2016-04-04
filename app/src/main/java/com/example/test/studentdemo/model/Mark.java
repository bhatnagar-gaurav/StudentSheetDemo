package com.example.test.studentdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
public class Mark implements Parcelable {
    public String subjectName;
    public int marks;

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subjectName);
        dest.writeInt(this.marks);
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getSubjectMarks() {
        return marks;
    }

    public Mark(String subjectName,int marks) {
        this.subjectName = subjectName;
        this.marks = marks;
    }

    // Methods for Implementing Parcelable Interface.
    protected Mark(Parcel in) {
        this.subjectName = in.readString();
        this.marks = in.readInt();
    }

    // Methods for Implementing Parcelable Interface
    public static final Parcelable.Creator<Mark> CREATOR = new Parcelable.Creator<Mark>() {
        @Override
        public Mark createFromParcel(Parcel source) {
            return new Mark(source);
        }

        @Override
        public Mark[] newArray(int size) {
            return new Mark[size];
        }
    };
}
