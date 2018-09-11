package com.jacsstuff.quizudo.quiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.jacsstuff.quizudo.R;

import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.jacsstuff.quizudo.utils.Consts.IS_ANSWER_CORRECT_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.CORRECT_ANSWER_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.TRIVIA_INTENT_EXTRA;

public class AnswerDialogActivity extends Activity implements View.OnClickListener {

    private View layout;
    private Context context;
    private boolean isTriviaPresent = true;
    private QuizSingleton quizSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = AnswerDialogActivity.this;
        quizSingleton = QuizSingleton.getInstance();

        setContentView(R.layout.activity_answer_dialog);
        layout = findViewById(R.id.answer_dialog_layout);
        layout.setOnClickListener(this);
        setupTextElements();
       // setDialogDimensions();
    }

    private void setupTextElements(){

        Intent intent = getIntent();

        boolean isAnswerCorrect = intent.getBooleanExtra(IS_ANSWER_CORRECT_INTENT_EXTRA, true);
        String correctAnswer = intent.getStringExtra(CORRECT_ANSWER_INTENT_EXTRA);
        String trivia = intent.getStringExtra(TRIVIA_INTENT_EXTRA);

        // if the activity has been inactive and restarts, there will be no intent data to pass in.
        // In such a scenario, it's better to just move to the next question.
        if(correctAnswer == null){
            finish();
        }

        if(trivia == null){
            trivia = "";
        }
        if(trivia.isEmpty()){
            isTriviaPresent = false;
        }
        TextView triviaText = findViewById(R.id.trivia);
        View triviaLayout = findViewById(R.id.trivia_layout);
        View correctAnswerLayout = findViewById(R.id.correctAnswerLayout);
        TextView correctAnswerText = findViewById(R.id.correctAnswerText);
        TextView answerStatusText = findViewById(R.id.answerStatusText);



        if(isAnswerCorrect){
            triviaText.setText(trivia);
            correctAnswerLayout.setVisibility(View.GONE);
            answerStatusText.setText(getResources().getString(R.string.result_overlay_correct_heading));
            int correctColor = ContextCompat.getColor(context,R.color.resultOverlayCorrectAnswerText);
            answerStatusText.setTextColor(correctColor);
            triviaLayout.setVisibility(View.VISIBLE);
            if(trivia.trim().isEmpty()){
                triviaLayout.setVisibility(View.GONE);
            }
            return;
        }

        answerStatusText.setText(getResources().getString(R.string.result_overlay_incorrect_heading));
        int incorrectColor = ContextCompat.getColor(context,R.color.resultOverlayIncorrectAnswerText);
        answerStatusText.setTextColor(incorrectColor);
        triviaLayout.setVisibility(View.GONE);
       // View tapToContinueText = findViewById(R.id.tapToContinueText);
        correctAnswerLayout.setVisibility(View.VISIBLE);
        correctAnswerText.setText(correctAnswer);
    }


    private void setDialogDimensions(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowResolutionX =size.x;
        int windowResolutionY = size.y;
        int borderX = (int)getResources().getDimension(R.dimen.dialog_activity_border_width);
        int borderY = (int)getResources().getDimension(R.dimen.dialog_activity_border_height);
        int dialogHeight = windowResolutionY - borderY * 2;
        int dialogWidth = windowResolutionX - borderX * 2;
        layout.setMinimumWidth(dialogWidth);
        layout.setMinimumHeight(dialogHeight);


        int triviaHeight = (int)getResources().getDimension(R.dimen.dialog_trivia_height);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        int totalHeight = windowResolutionY - borderY * 2;
        Log.i("answerdialog", "checking for trivia, isPresent:" + isTriviaPresent +"  totalHeight: "+ totalHeight+ "  triviaHeight: "+ triviaHeight);

        if(!isTriviaPresent && totalHeight > triviaHeight){
            Log.i("answerdialog", "Trivia is not present, totalHeight > triviaHeight");
            totalHeight -= triviaHeight;
        }
        // Changes the height and width to the specified *pixels*
        params.height = totalHeight;
        params.width = windowResolutionX - borderX * 2;
        layout.setLayoutParams(params);

    }


    public void onClick(View view){
        quizSingleton.getQuiz().setDialogClosed(true);
        finish();
    }

    @Override
    public void onPause(){
        quizSingleton.getQuiz().setDialogClosed(true);
        super.onPause();
    }

}
