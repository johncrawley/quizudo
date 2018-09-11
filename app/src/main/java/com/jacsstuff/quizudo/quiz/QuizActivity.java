package com.jacsstuff.quizudo.quiz;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.R;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener, QuizView{

    private ListView answerList;
    private TextView questionText;
    private TextView questionCounterText;
    private Button nextQuestionButton;
    private Context context;
    private QuizController quizController;

    protected void onResume(){
        super.onResume();
        quizController.notifyQuizResumed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        context = QuizActivity.this;
        ToolbarBuilder.setupToolbar(this);
        setupViews();
        quizController = new QuizController(this, context);
        setupListClickListener();
    }


    private void setupListClickListener(){
        answerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                quizController.answerItemClick(position);
            }
        });
    }


    private void setupViews(){
        questionText =          findViewById(R.id.question_text);
        answerList =            findViewById(R.id.listView);
        nextQuestionButton =    findViewById(R.id.next_question_button);
        questionCounterText =   findViewById(R.id.question_counter);

        nextQuestionButton.setOnClickListener(this);
    }


    public void onClick(View view){
        if(view.getId() == R.id.next_question_button){
            quizController.nextButton();
        }
    }

    public void setToolbarTitle(String title){

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
        }
    }

    @Override
    public void displayFinishButton() {

        this.nextQuestionButton.setText(getResources().getString(R.string.next_question_button_finish));
    }

    public void hideNextButton(){
        this.nextQuestionButton.setVisibility(View.INVISIBLE);
    }


    public void setAnswerChoices(String[] currentAnswerChoices) {
        answerList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, currentAnswerChoices));
        answerList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        answerList.setSelector(R.color.selectedListItem);
    }


    public void setQuestion(String questionText){
        this.questionText.setText(questionText);
    }

    public void setQuestionCounter(String counterValue){
        this.questionCounterText.setText(counterValue);
    }

    public Context getContext(){
        return this.context;
    }


}