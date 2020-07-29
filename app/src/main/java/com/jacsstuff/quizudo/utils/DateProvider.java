package com.jacsstuff.quizudo.utils;

import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateProvider {

    public static String getNow() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        return dateFormat.format(cal.getTime());
    }
}


