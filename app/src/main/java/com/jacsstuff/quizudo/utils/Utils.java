package com.jacsstuff.quizudo.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Utils {


    public static void bringBackActivity(Context context, Class activity){
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }


    public static boolean isFreeVersion(Context context){
        Log.i("utils", "package name = " + context.getPackageName());
        return context.getPackageName().equals("com.jacsstuff.quizudo.free");
    }
}
