package com.jacsstuff.quizudo.results;

import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import com.jacsstuff.quizudo.quiz.QuizActivity;
import com.jacsstuff.quizudo.model.QuestionResult;
import com.jacsstuff.quizudo.quiz.QuizSingleton;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.list.ResultsListAdapter;

import java.util.List;

/**
 * Created by John on 29/06/2016.
 *
 * Helps out the Quiz Results Activity
 */
public class QuizResultsController {

    Context context;

    private QuestionResultsSingleton singletonResultsStore;
    private List<QuestionResult> questionResults;
    private ResultsMessageHelper resultsMessageHelper;
    private QuizResultsActivity quizResultsActivity;
    private TextView quizResultsMessage;
    private ListView quizResultsList;

    public QuizResultsController(Context context, QuizResultsActivity quizResultsActivity) {
        this.context = context;
        this.quizResultsActivity = quizResultsActivity;
        this.quizResultsMessage = quizResultsActivity.getResultsTextView();
        this.quizResultsList = quizResultsActivity.getResultsListView();
        singletonResultsStore = QuestionResultsSingleton.getInstance();
        questionResults = singletonResultsStore.getResults();
        resultsMessageHelper = new ResultsMessageHelper(context);
    }

    public void processResults(){
        if(questionResults.isEmpty()){
            quizResultsActivity.finish();
            return;
        }
        int correctAnswerCount = singletonResultsStore.getCorrectAnswerCount();
        int questionCount = questionResults.size();

        quizResultsActivity.setStatistic(correctAnswerCount, questionCount);
        String resultsMessage = resultsMessageHelper.getResultMessage(correctAnswerCount, questionResults.size());
        quizResultsMessage.setText(resultsMessage);
        quizResultsList.setAdapter(new ResultsListAdapter(context, R.layout.result_row, questionResults));
    }

    public void retryQuiz(){
        QuizSingleton quizSingleton = QuizSingleton.getInstance();
        quizSingleton.retryQuiz();
        Intent intent = new Intent(context, QuizActivity.class);
        context.startActivity(intent);
    }

}
