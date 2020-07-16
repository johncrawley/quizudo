package com.jacsstuff.quizudo.creator.express;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.dialog.DialogLoader;
import com.jacsstuff.quizudo.list.ListActionExecutor;
import com.jacsstuff.quizudo.list.ListAdapterHelper;

public class GeneratorQuestionSetActivity extends AppCompatActivity implements ListActionExecutor {

    private String questionSetName;
    private int questionSetId;
    private ListView list;
    private Context context;
    private ListAdapterHelper listAdapterHelper;
    private QuestionGeneratorDbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_question_set);
        context = GeneratorQuestionSetActivity.this;
        dbManager = new QuestionGeneratorDbManager(context);
        getIntentData();
        setupList();
    }

    private void setupList(){
        list = findViewById(R.id.list1);
        TextView textView = new TextView(context);
        textView.setText(getString(R.string.question_generator_set_answer_list_title));
        list.addHeaderView(textView);
        ListAdapterHelper listAdapterHelper = new ListAdapterHelper(context, list, this);


    }

    private void getIntentData(){
        Intent intent = getIntent();
        String questionSetName = intent.getStringExtra("1");
        int questionSetId = intent.getIntExtra("2",0);

    }


    @Override
    public void onClick(String item){

    }


    @Override
    public void onLongClick(String item){

    }


    @Override
    public void onTextEntered(String text){
        if(text == null){
            return;
        }
        String name = text.trim();
        dbManager.addQuestionGenerator(name);
        listAdapterHelper.addToList(name);
    }

}
