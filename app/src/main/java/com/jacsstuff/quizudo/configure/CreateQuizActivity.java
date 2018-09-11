package com.jacsstuff.quizudo.configure;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.utils.LoadingDialog;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.results.QuestionResultsSingleton;
import com.jacsstuff.quizudo.quiz.QuizSingleton;
import com.jacsstuff.quizudo.list.QuestionPackList;
import com.jacsstuff.quizudo.db.QuestionPackDBManager;
import com.jacsstuff.quizudo.model.QuestionsSingleton;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.db.QuestionPackManager;

import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private QuestionPackManager questionPackManager;
    private QuestionPackList questionPackList;
    private  Context context;
    private LoadingDialog loadingDialog;
    private boolean isDbInitFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        context = CreateQuizActivity.this;

        ToolbarBuilder.setupToolbar(this);
        loadingDialog = new LoadingDialog(context, R.string.loading_questions);
        // N.B Questions are loaded in the onResume() method
    }

    @Override
    protected void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
    }
    @Override
    protected void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
    }



    @Override
    protected void onDestroy(){
        if(questionPackManager != null) {
            questionPackManager.closeConnections();
        }
        super.onDestroy();
    }
    protected void onResume(){
        super.onResume();
        loadQuestions("onResume()");
    }

    private void setupViews(){

        Button configureQuizButton = findViewById(R.id.configureQuizButton);
        ListView listView = findViewById(R.id.listView);
        TextView noItemsTextView = findViewById(R.id.no_question_packs_found_text);
        questionPackList = new QuestionPackList(context, this, listView, configureQuizButton, noItemsTextView);
        questionPackList.setButtonOnClick(new View.OnClickListener(){
            public void onClick(View view){
                new QuizDbLoader().execute("");
            }
        });
        questionPackList.initializeList(questionPackManager.getQuestionPackDetails());

    }

    private void loadQuestions(String callerId) {
        if (isDbInitFinished) {
            isDbInitFinished = false;
            loadingDialog.show();
            DBInitialiser initialiser = new DBInitialiser();
            initialiser.execute(callerId);
        }
    }


    private class DBInitialiser extends AsyncTask<String, String, Integer>{

        public Integer doInBackground(String... params){
            //String callerId = params[0];

            DefaultQuestionLoader defaultQuestionLoader = new DefaultQuestionLoader(context);
            defaultQuestionLoader.loadQuestions();
            questionPackManager = new QuestionPackDBManager(context);

            return 1;
        }

        public void onPostExecute(Integer value){
            loadingDialog.dismiss();
            setupViews();
            isDbInitFinished = true;
        }

    }

    private class QuizDbLoader extends AsyncTask<String, String, Integer> {


        QuestionsSingleton questionsSingleton = QuestionsSingleton.getInstance();
        public Integer doInBackground(String... params){

            questionsSingleton.reset();

            List<Integer> questionIds = questionPackManager.getQuestionIds(questionPackList.getSelectedIds());
            questionsSingleton.addNewQuestionIds(questionIds);
            QuizSingleton quizSingleton = QuizSingleton.getInstance();
            quizSingleton.killRunningQuiz(); // If we're starting a new quiz, we dont want to give the opportunity of resuming a previous quiz.
                                            // Why not? We'd have to create another structure to hold the previous quiz questions, and swap them over.
                                            // Also what if somebody decides to delete or update the questions of the running quiz?
                                            // As it stands, you cannot delete questions from the db and go back to the 'configure quiz' screen
                                            // without passing this activity, and basically wiping any old unfinished quiz away.

            QuestionResultsSingleton questionResultsSingleton = QuestionResultsSingleton.getInstance();
            questionResultsSingleton.resetResults(); // as well as removing the exisitng questions, we have to remove the previous results.

            if(questionsSingleton.isEmpty()) {
                return 0;
            }
            return 1;
        }

        public void onPostExecute(Integer value){
            loadConfigureQuizActivity();
        }
    }

    public void loadConfigureQuizActivity(){

        Intent intent = new Intent(this, ConfigureQuizActivity.class);
        startActivity(intent);
    }


}