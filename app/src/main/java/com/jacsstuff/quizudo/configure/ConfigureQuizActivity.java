package com.jacsstuff.quizudo.configure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import static com.jacsstuff.quizudo.utils.Consts.PREVIOUS_NUMBER_OF_QUESTIONS_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.PREVIOUS_SUBMIT_ANSWER_ON_TOUCH_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.PREVIOUS_SHOW_ANSWER_PREFERENCE;
import static com.jacsstuff.quizudo.utils.Consts.DEFAULT_NUMBER_OF_QUESTIONS;
import static com.jacsstuff.quizudo.utils.Consts.NUMBER_OF_QUESTIONS_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.DISPLAY_ANSWER_DIALOG_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.SUBMIT_ANSWER_ON_TOUCH_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.QUIZ_SETTINGS_PREFERENCES;

import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.model.QuestionsSingleton;
import com.jacsstuff.quizudo.quiz.QuizSingleton;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.quiz.QuizActivity;
import com.jacsstuff.quizudo.utils.Utils;

public class ConfigureQuizActivity extends AppCompatActivity implements View.OnClickListener{

    private CheckBox instantResults;
    private CheckBox submitAnswerOnTouch;
    private Button resumeQuizButton, beginQuizButton;
    private QuestionsSingleton questionsSingleton;
    private NumberPicker numberPicker;
    private SharedPreferences preferences;
    private QuizSingleton quizSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_quiz);
        ToolbarBuilder.setupToolbar(this);
        quizSingleton = QuizSingleton.getInstance();
        preferences = getSharedPreferences(QUIZ_SETTINGS_PREFERENCES, MODE_PRIVATE);
        setupViews();
        setupQuestionsFoundText();
        setupNumberPicker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_enabled_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home_menu_item) {
            Utils.bringBackActivity(this, MainActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews(){
        questionsSingleton = QuestionsSingleton.getInstance();
        instantResults =            findViewById(R.id.instantResults);
        submitAnswerOnTouch =       findViewById(R.id.submitAnswerOnTouch);
        beginQuizButton =    findViewById(R.id.beginQuiz);
        beginQuizButton.setVisibility(View.VISIBLE);
        resumeQuizButton =          findViewById(R.id.resumeQuiz);

        if(beginQuizButton == null){
            return;
        }
        beginQuizButton.setOnClickListener(this);
        resumeQuizButton.setOnClickListener(this);
        submitAnswerOnTouch.setChecked(preferences.getBoolean(PREVIOUS_SUBMIT_ANSWER_ON_TOUCH_PREFERENCE, false));
        instantResults.setChecked(preferences.getBoolean(PREVIOUS_SHOW_ANSWER_PREFERENCE, false));
    }
    @Override
    protected void onResume(){
        showResumeButtonIfQuizStarted();
        super.onResume();
    }

    private void showResumeButtonIfQuizStarted(){

        if(!quizSingleton.isQuizRunning()){
            resumeQuizButton.setVisibility(View.GONE);
        }
        else{
            resumeQuizButton.setVisibility(View.VISIBLE);
        }
    }



    private void setupQuestionsFoundText() {
        TextView numberOfQuestionsAvailableText = (TextView) findViewById(R.id.numberOfQuestionsAvailable);
        if (numberOfQuestionsAvailableText != null) {
            numberOfQuestionsAvailableText.setVisibility(View.GONE); //setting to gone, instead of removing from layout, due to time overrun.
        }
        int numberOfQuestions = questionsSingleton.getNumberOfQuestions();
        TextView numberOfQuestionsLabel = (TextView) findViewById(R.id.selectNumberOfQuestionsLabel);
        if (numberOfQuestionsLabel == null) {
            return;
        }
        numberOfQuestionsLabel.setText(getResources().getString(R.string.number_of_questions_label, numberOfQuestions));
    }


    private void setupNumberPicker(){

        numberPicker = findViewById(R.id.numberPicker);
        if(numberPicker == null){
            return;
        }
        numberPicker.setMinValue(1);

        int maxValue = questionsSingleton.getNumberOfQuestions();
        if(maxValue == 0){
            setupElementsForNoQuestionsFound();
            return;
        }
        String [] valuesArray = createValuesArray(maxValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDisplayedValues(valuesArray);

        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(getPreviousNumberOfQuestions());
    }


    private String[] createValuesArray(int maxValue){
        String [] valuesArray = new String[maxValue];
        for(int i = 0; i < maxValue; i++){
            valuesArray[i]  = "" + (i + 1);
        }
        return valuesArray;
    }


    private void setupElementsForNoQuestionsFound(){
        Toast.makeText(ConfigureQuizActivity.this,R.string.no_questions_available_toast , Toast.LENGTH_SHORT).show();
        numberPicker.setVisibility(View.GONE);
        beginQuizButton.setVisibility(View.GONE);
    }


    //returns the value for the previously selected number of questions
    // if that value is above the current maximum, the current maximum is returned instead.
    private int getPreviousNumberOfQuestions() {
        int numberOfAvailableQuestions = questionsSingleton.getNumberOfQuestions();
        int numberOfQuestions = preferences.getInt(PREVIOUS_NUMBER_OF_QUESTIONS_PREFERENCE, DEFAULT_NUMBER_OF_QUESTIONS);

        if (numberOfQuestions > numberOfAvailableQuestions){
            numberOfQuestions = numberOfAvailableQuestions;
        }
        if(numberOfQuestions < 1){
            numberOfQuestions = 1;
        }
        return numberOfQuestions;
    }


    public void onClick(View view){
        if(view.getId()==R.id.beginQuiz){
            saveCurrentConfig();
            int numberOfQuestions = numberPicker.getValue();
            DBWriter dbWriter = new DBWriter(ConfigureQuizActivity.this);
            quizSingleton.createNewQuiz(dbWriter, numberOfQuestions);
            startQuizActivity();
        }
        else if(view.getId() == R.id.resumeQuiz){
            saveCurrentConfig();
            startQuizActivity();
        }
    }


    private void saveCurrentConfig(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREVIOUS_NUMBER_OF_QUESTIONS_PREFERENCE, numberPicker.getValue());
        editor.putBoolean(PREVIOUS_SHOW_ANSWER_PREFERENCE, instantResults.isChecked());
        editor.putBoolean(PREVIOUS_SUBMIT_ANSWER_ON_TOUCH_PREFERENCE, submitAnswerOnTouch.isChecked());
        editor.apply();
    }


    private void startQuizActivity(){
        Log.i("startQuizActivity()", "Starting quiz activity.");
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_INTENT_EXTRA, numberPicker.getValue());
        intent.putExtra(DISPLAY_ANSWER_DIALOG_INTENT_EXTRA, instantResults.isChecked());
        intent.putExtra(SUBMIT_ANSWER_ON_TOUCH_INTENT_EXTRA, submitAnswerOnTouch.isChecked());

        startActivity(intent);
    }



}
