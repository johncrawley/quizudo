package com.jacsstuff.quizudo.configure;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

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
    protected void onDestroy(){
        if(questionPackManager != null) {
            questionPackManager.closeConnections();
        }
        super.onDestroy();
    }
    protected void onResume(){
        super.onResume();
        loadQuestions();
    }

    private void setupViews(){

        Button configureQuizButton = findViewById(R.id.configureQuizButton);
        ListView listView = findViewById(R.id.listView);
        TextView noItemsTextView = findViewById(R.id.no_question_packs_found_text);
        questionPackList = new QuestionPackList(context, this, listView, configureQuizButton, noItemsTextView);
        questionPackList.setButtonOnClick(new View.OnClickListener(){
            public void onClick(View view){
               startQuizLoaderTask();
            }
        });
        questionPackList.initializeList(questionPackManager.getQuestionPackOverviews());
    }

    private void startQuizLoaderTask(){
        new QuizDbLoader(this, questionPackManager, questionPackList).execute("");
    }


    void onFinishedDbInit(){
        loadingDialog.dismiss();
        setupViews();
        isDbInitFinished = true;
    }

    private void loadQuestions() {
        if (isDbInitFinished) {
            isDbInitFinished = false;
            loadingDialog.show();
            questionPackManager = new QuestionPackDBManager(context);
            DBInitialiser initialiser = new DBInitialiser(this, context, questionPackManager);
            initialiser.execute("onResume()");
        }
    }


    private static class DBInitialiser extends AsyncTask<String, String, Integer>{

        private final WeakReference<CreateQuizActivity> createQuizActivityWeakReference;
        private final WeakReference<QuestionPackManager> questionPackManagerWeakReference;
        private final WeakReference<Context> contextWeakReference;

        DBInitialiser(CreateQuizActivity createQuizActivity, Context context, QuestionPackManager questionPackManager){
            createQuizActivityWeakReference = new WeakReference<>(createQuizActivity);
            questionPackManagerWeakReference = new WeakReference<>(questionPackManager);
            contextWeakReference = new WeakReference<>(context);
        }

        public Integer doInBackground(String... params){
            DefaultQuestionLoader defaultQuestionLoader = new DefaultQuestionLoader(contextWeakReference.get());
            defaultQuestionLoader.loadQuestions();
            questionPackManagerWeakReference.get().init();
            return 1;
        }

        public void onPostExecute(Integer value){
           createQuizActivityWeakReference.get().onFinishedDbInit();
        }

    }

    private static class QuizDbLoader extends AsyncTask<String, String, Integer> {

        private final QuestionsSingleton questionsSingleton = QuestionsSingleton.getInstance();
        private final WeakReference<QuestionPackManager> questionPackManagerWeakReference;
        private final WeakReference<QuestionPackList> questionPackListWeakReference;
        private final WeakReference<CreateQuizActivity> createQuizActivityWeakReference;


        QuizDbLoader(CreateQuizActivity createQuizActivity, QuestionPackManager questionPackManager, QuestionPackList questionPackList){
            createQuizActivityWeakReference = new WeakReference<>(createQuizActivity);
            questionPackManagerWeakReference = new WeakReference<>(questionPackManager);
            questionPackListWeakReference = new WeakReference<>(questionPackList);
        }

        public Integer doInBackground(String... params){
            questionsSingleton.reset();
            Set<Integer> selectedIds = questionPackListWeakReference.get().getSelectedIds();
            List<Integer> questionIds = questionPackManagerWeakReference.get().getQuestionIds(selectedIds);
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
            createQuizActivityWeakReference.get().loadConfigureQuizActivity();
        }
    }

    public void loadConfigureQuizActivity(){

        Intent intent = new Intent(this, ConfigureQuizActivity.class);
        startActivity(intent);
    }


}