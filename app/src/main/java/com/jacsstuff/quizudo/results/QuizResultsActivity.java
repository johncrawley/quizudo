package com.jacsstuff.quizudo.results;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.Utils;

public class QuizResultsActivity extends AppCompatActivity {

    private TextView quizResultsMessage;
    private TextView quizResultsStatistic;
    private ListView quizResultsList;
    private QuizResultsController quizResultsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        ToolbarBuilder.setupToolbarWithTitle(QuizResultsActivity.this, getResources().getString(R.string.quiz_completed_heading));
        quizResultsMessage      = findViewById(R.id.quiz_results_message);
        quizResultsStatistic    = findViewById(R.id.quiz_results_statistic);
        quizResultsList         = findViewById(R.id.quiz_results_list);

        quizResultsController = new QuizResultsController(QuizResultsActivity.this, this);
        quizResultsController.processResults();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz_results_menu, menu);
        return true;
    }

    public ListView getResultsListView(){
        return this.quizResultsList;
    }
    public TextView getResultsTextView(){
        return this.quizResultsMessage;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home_menu_item) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Utils.bringBackActivity(QuizResultsActivity.this, MainActivity.class);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.retry_menu_item) {
            quizResultsController.retryQuiz();
        }
        return super.onOptionsItemSelected(item);
    }


    public void setStatistic(int correctlyAnswered, int totalQuestions){
        quizResultsStatistic.setText(getResources().getString(R.string.quiz_results_statistic, correctlyAnswered, totalQuestions));
    }

}
