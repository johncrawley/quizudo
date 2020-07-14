package com.jacsstuff.quizudo.creator.express;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;

public class GeneratorQuestionSetActivity extends AppCompatActivity {

    private String questionSetName;
    private int questionSetId;
    private ListView list;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_question_set);
        context = GeneratorQuestionSetActivity.this;
        getIntentData();
        setupList();
    }

    private void setupList(){
        list = findViewById(R.id.list1);
        TextView textView = new TextView(context);
        textView.setText(getString(R.string.question_generator_set_answer_list_title));
        list.addHeaderView(textView);


    }

    private void getIntentData(){
        Intent intent = getIntent();
        String questionSetName = intent.getStringExtra("1");
        int questionSetId = intent.getIntExtra("2",0);

    }
}
