package com.jacsstuff.quizudo.creator;

import android.content.Context;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jacsstuff.quizudo.main.MainActivity;
import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.ToolbarBuilder;
import com.jacsstuff.quizudo.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizWriterActivity extends AppCompatActivity implements View.OnClickListener, QuizWriterView {

    private Spinner defaultAnswerPoolSpinner;
    private Spinner questionAnswerPoolSpinner;
    private Context context;
    private QuizWriterController controller;
    private Button nextPageButton, previousPageButton, firstPageButton, lastPageButton;
    private TextView pageNumberText;
    private CheckBox usesDefaultAnswerPoolCheckbox;
    private TextView questionText, correctAnswerText, triviaText, topicsText, descriptionText, questionPackNameText, defaultTopicsText;
    private List<TextView> answerChoiceViews;
    private Map<String, Integer> spinnerMap;
    private String noneOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_writer);
        context = QuizWriterActivity.this;
        noneOption =  getResources().getString(R.string.answer_pools_spinner_none_option); //represents the null element on the spinners
        setupViews();
        ToolbarBuilder.setupToolbarWithTitle(this, getResources().getString(R.string.title_quiz_writer_activity));


        controller = new QuizWriterControllerImpl(this, context, noneOption);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz_writer_menu, menu);
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
        else if(id == R.id.save_menu_item){
            controller.saveQuestionPack();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViews(){
        setupLayoutViews();
        setupNagivationViews();
        setupInputFieldViews();
        setupAnswerPoolViews();
        setupAnswerChoicesViews();
    }


    private void setupLayoutViews(){
        View questionPackLayout = findViewById(R.id.question_pack_fields_layout);
        View questionItemsLayout = findViewById(R.id.question_items_fields_layout);
        questionPackLayout.setVisibility(View.VISIBLE);
        questionItemsLayout.setVisibility(View.GONE);
        scrollView = findViewById(R.id.scrollView1);
    }


    private void setupInputFieldViews(){

        correctAnswerText = findViewById(R.id.correctAnswerTextInput);
        triviaText = findViewById(R.id.triviaTextInput);
        questionText =  findViewById(R.id.questionTextInput);

        topicsText = findViewById(R.id.topicsTextInput);
        descriptionText = findViewById(R.id.questionPackDescriptionTextInput);
        questionPackNameText = findViewById(R.id.questionPackNameTextInput);
        defaultTopicsText =findViewById(R.id.defaultTopicsTextInput);

    }


    private void setupNagivationViews(){

        firstPageButton = findViewById(R.id.firstPageButton);
        nextPageButton = findViewById(R.id.nextPageButton);
        previousPageButton = findViewById(R.id.previousPageButton);
        lastPageButton = findViewById(R.id.lastPageButton);

        pageNumberText = findViewById(R.id.pageNumber);

        firstPageButton.setOnClickListener(this);
        nextPageButton.setOnClickListener(this);
        previousPageButton.setOnClickListener(this);
        lastPageButton.setOnClickListener(this);

    }

    public void setupAnswerPoolViews(){

        defaultAnswerPoolSpinner = findViewById(R.id.default_answer_pools_spinner);
        questionAnswerPoolSpinner = findViewById(R.id.questionAnswerPoolSpinner);

        usesDefaultAnswerPoolCheckbox = findViewById(R.id.usesDefaultAnswerPool);
        usesDefaultAnswerPoolCheckbox.setOnClickListener(this);
        usesDefaultAnswerPoolCheckbox.setChecked(false);
    }


    private void setupAnswerChoicesViews(){
        List<Integer> ids = Arrays.asList(  R.id.answerChoices1TextInput,
                                            R.id.answerChoices2TextInput,
                                            R.id.answerChoices3TextInput,
                                            R.id.answerChoices4TextInput,
                                            R.id.answerChoices5TextInput );

        answerChoiceViews = new ArrayList<>();
        for(int i : ids){
            answerChoiceViews.add(findViewById(i));
        }

    }

    @Override
    public List<String> getAnswerChoices() {

        List<String> answerChoicesText = new ArrayList<>();
        for(TextView textView : answerChoiceViews){
            answerChoicesText.add(textView.getText().toString());

        }
        return answerChoicesText;
    }

    @Override
    public void setAnswerChoices(List<String> answerChoices) {

        int limit = Math.min(answerChoices.size(), answerChoiceViews.size());

        for(int i=0; i< limit; i++){
            answerChoiceViews.get(i).setText(answerChoices.get(i));
        }
    }




    public void enableAnswerPoolSelection(){
        questionAnswerPoolSpinner.setEnabled(true);
    }

    public void disableAnswerPoolSelection(){
        questionAnswerPoolSpinner.setEnabled(false);
    }



    public String getQuestionText(){
        return questionText.getText().toString();
    }

    @Override
    public String getCorrectAnswerText() {
        return correctAnswerText.getText().toString();
    }

    @Override
    public boolean isUsingDefaultAnswerPool() {
        return usesDefaultAnswerPoolCheckbox.isChecked();
    }

    @Override
    public String getAnswerPool() {
        return questionAnswerPoolSpinner.getSelectedItem().toString();
    }

    public String getTopics(){ return topicsText.getText().toString();}

    @Override
    public String getTriviaText() {
        return triviaText.getText().toString();
    }


    @Override
    public void setAnswerPoolChoices(List<String> answerPoolNames) {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add(noneOption);
        spinnerItems.addAll(answerPoolNames);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, spinnerItems);

        spinnerMap = new HashMap<>();
        for(int i = 0; i < spinnerItems.size(); i++){
            spinnerMap.put(spinnerItems.get(i), i);
        }
        defaultAnswerPoolSpinner.setAdapter( spinnerAdapter);
        questionAnswerPoolSpinner.setAdapter( spinnerAdapter);
    }


    public String getDescription(){
        return descriptionText.getText().toString();
    }

    @Override
    public String getDefaultAnswerPoolName() {
        return defaultAnswerPoolSpinner.getSelectedItem().toString();
    }



    @Override
    public String getDefaultTopics() {
        return defaultTopicsText.getText().toString();
    }

    @Override
    public void showQuestionScreen() {
        switchViews(R.id.question_pack_fields_layout, R.id.question_items_fields_layout);
    }

    private ScrollView scrollView;

    @Override
    public void showQuestionPackScreen() {
        switchViews(R.id.question_items_fields_layout, R.id.question_pack_fields_layout);
    }

    private void switchViews(int oldViewId, int newViewId){

        findViewById(oldViewId).setVisibility(View.GONE);
        findViewById(newViewId).setVisibility(View.VISIBLE);
        scrollView.scrollTo(0,0);

    }

    @Override
    public void setQuestionNumber(int questionNumber) {

        setTitleString(getResources().getString(R.string.quiz_writer_questions_title, questionNumber));
    }

    private void setTitleString(String title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null){
            return;
        }
        actionBar.setTitle(title);
    }


    @Override
    public void setPageNumber(int pageNumber){
        String pageNumberValue = "" + pageNumber;
        pageNumberText.setText(pageNumberValue);

    }

    @Override
    public void setQuestionText(String questionText) {
        this.questionText.setText(questionText);
    }

    @Override
    public void setCorrectAnswerText(String correctAnswerText) {
        this.correctAnswerText.setText(correctAnswerText);
    }

    @Override
    public void setTriviaText(String triviaText) {
        this.triviaText.setText(triviaText);
    }

    @Override
    public void setTopics(String topics) {
        topicsText.setText(topics);
    }

    @Override
    public void setUsesDefaultAnswerPool(boolean usesDefaultAnswerPool) {
        this.usesDefaultAnswerPoolCheckbox.setChecked(usesDefaultAnswerPool);
    }



    @Override
    public void setQuestionPackName(String name) {
        questionPackNameText.setText(name);
    }

    @Override
    public void setDescription(String description) {
        descriptionText.setText(description);
    }


    @Override
    public void setDefaultAnswerPoolName(String answerPoolName) {

        Log.i("QuizWriterActivity", "About to set default spinner, answerpoolname: "+ answerPoolName);
        setSpinnerValue(defaultAnswerPoolSpinner, answerPoolName);
    }


    @Override
    public void setAnswerPool(String answerPoolName) {
        Log.i("QuizWriterActivity", "About to set question spinner, answerpoolname: "+ answerPoolName);
        setSpinnerValue(questionAnswerPoolSpinner, answerPoolName);
    }

    // Need to set an index, so the map stores the string in the key, and the int in the value
    private void setSpinnerValue(Spinner spinner, String key){
        if(key == null || spinner == null || spinnerMap.get(key) == null){
            return;
        }
        Integer number = spinnerMap.get(key);
        if(number != null) {
            spinner.setSelection(number);
        }
    }


    @Override
    public void setDefaultTopics(String topics) {
        defaultTopicsText.setText(topics);
    }

    public String getQuestionPackName(){
        return questionPackNameText.getText().toString();
    }


    public void disableLastButton(){
        this.lastPageButton.setEnabled(false);

    }
    public void disablePreviousButton(){
        this.previousPageButton.setEnabled(false);
    }


    public void disableFirstButton(){
        this.firstPageButton.setEnabled(false);

    }
    public void enableLastButton(){
        this.lastPageButton.setEnabled(true);

    }
    public void enablePreviousButton(){
        this.previousPageButton.setEnabled(true);

    }
    public void enableNextButton(){
        this.nextPageButton.setEnabled(true);

    }
    public void enableFirstButton(){
        this.firstPageButton.setEnabled(true);

    }

    public void onClick(View view){
        int id = view.getId();

        if(id == R.id.nextPageButton){
            controller.loadNextPage();
        }
        else if(id == R.id.previousPageButton){
            controller.loadPreviousPage();
        }
        else if(id == R.id.firstPageButton){
            controller.loadFirstPage();
        }
        else if(id == R.id.lastPageButton){
            controller.loadLastPage();
        }
        else if(id == R.id.usesDefaultAnswerPool){
            onUseDefaultCheckBoxClick();
        }
    }


    private void onUseDefaultCheckBoxClick(){
        if(usesDefaultAnswerPoolCheckbox.isChecked()){
            questionAnswerPoolSpinner.setEnabled(false);
            return;
        }
        questionAnswerPoolSpinner.setEnabled(true);

    }

    public void setFirstPageTitle(){
        String str = getString(R.string.title_quiz_writer_activity);
        this.setTitleString(str);
    }



    public void displaySaveSuccessMessage(int numberOfQuestionsSaved){
        shortToast(R.string.quiz_writer_save_question_pack_success, "" + numberOfQuestionsSaved);
    }

    public void displaySaveFailMessage(){
        shortToast(R.string.quiz_writer_save_question_pack_fail, "");
    }

    public void displayNothingToSaveMessage(){

        shortToast(R.string.quiz_writer_save_question_pack_unable, "");
    }

    private void shortToast(int messageId, String additional){
        String msg = getResources().getString(messageId,additional);
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();

    }

}
