package com.example.test.studentdemo.userinterface;

import com.example.test.studentdemo.model.Student;

import java.util.Comparator;

/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */

public class CustomStudentComparator implements Comparator<Student> {

    @Override
    public int compare(Student left, Student right) {
        return left.getlName().compareToIgnoreCase(right.getlName());
    }

}
