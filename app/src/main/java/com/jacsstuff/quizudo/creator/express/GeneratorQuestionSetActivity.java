package com.jacsstuff.quizudo.creator.express;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jacsstuff.quizudo.R;

public class GeneratorQuestionSetActivity extends AppCompatActivity {

    private String questionSetName;
    private int questionSetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_question_set);
        getIntentData();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        String questionSetName = intent.getStringExtra("1");
        int questionSetId = intent.getIntExtra("2",0);

    }
}
