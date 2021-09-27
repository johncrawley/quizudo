package com.jacsstuff.quizudo.utils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.jacsstuff.quizudo.R;

public class ToolbarBuilder {

    public static void setupToolbar(AppCompatActivity activity){

        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar == null){
            return;
        }
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );

    }

    public static void setupToolbarWithTitle(AppCompatActivity activity, String title){
        setupToolbar(activity);
        setTitle(activity, title);

    }

    private static void setTitle(AppCompatActivity activity, String title){

        if(title == null){
            Log.i("AnswerListActivity", "title from intent is null!");
            return;
        }
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar != null){
            activity.setTitle(title);
        }
    }
}
