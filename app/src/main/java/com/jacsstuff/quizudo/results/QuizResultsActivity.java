package com.jacsstuff.quizudo.results;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.quiz.QuizActivity;
import com.jacsstuff.quizudo.quiz.QuizSingleton;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.List;

public class QuizResultsActivity extends AppCompatActivity {

    private TextView quizResultsMessage;
    private TextView quizResultsStatistic;
    private ListView quizResultsList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        context = QuizResultsActivity.this;
        ToolbarBuilder.setupToolbarWithTitle(QuizResultsActivity.this, getResources().getString(R.string.quiz_completed_heading));
        quizResultsMessage      = findViewById(R.id.quiz_results_message);
        quizResultsStatistic    = findViewById(R.id.quiz_results_statistic);
        quizResultsList         = findViewById(R.id.quiz_results_list);
        processResults();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_results_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home_menu_item) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Utils.bringBackActivity(QuizResultsActivity.this, MainActivity.class);
        }
        else if (id == R.id.retry_menu_item) {
            retryQuiz();
        }
        return super.onOptionsItemSelected(item);
    }


    public void processResults(){
        QuestionResultsSingleton resultsSingleton = QuestionResultsSingleton.getInstance();
        List<QuestionResult> results = resultsSingleton.getResults();
        int correctAnswers = resultsSingleton.getCorrectAnswerCount();
        int numberOfQuestions = results.size();

        if(results.isEmpty()){
            finish();
            return;
        }
        setResultStatisticText(correctAnswers, numberOfQuestions);
        setResultsMessge(correctAnswers, numberOfQuestions);
        quizResultsList.setAdapter(new ResultsListAdapter(context, R.layout.result_row, results));
    }


    public void setResultStatisticText(int correctAnswers, int numberOfQuestions){
        quizResultsStatistic.setText(getResources().getString(R.string.quiz_results_statistic, correctAnswers, numberOfQuestions));
    }


    public void setResultsMessge(int correctAnswers, int numberOfQuestions){
        String resultsMessage = new ResultsMessageHelper(context).getResultMessage(correctAnswers, numberOfQuestions);
        quizResultsMessage.setText(resultsMessage);
    }


    public void retryQuiz(){
        QuizSingleton quizSingleton = QuizSingleton.getInstance();
        quizSingleton.retryQuiz();
        Intent intent = new Intent(context, QuizActivity.class);
        startActivity(intent);
    }

}
