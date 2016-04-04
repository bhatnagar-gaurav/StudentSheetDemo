package com.example.test.studentdemo.network;


import java.io.IOException;

/* Created by gaurav_bhatnagar on 4/4/2016.
        * This Class is for creating the customized Exception so that
        * it could also be shown to the end user.
        */
public class IOExceptionWithContent extends IOException {

    private static final long serialVersionUID = -4085558180781960925L;
    public String mContent;
    public IOExceptionWithContent(final String message, final String content) {
        super(message);
        mContent = content;
    }
    public String toString() {
        String str = super.toString();
        if(null != mContent) {
            str = str + "   Content: " + mContent;
        }
        return str;
    }

}
