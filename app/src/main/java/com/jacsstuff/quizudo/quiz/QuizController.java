package com.jacsstuff.quizudo.quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.jacsstuff.quizudo.results.QuizResultsActivity;
import com.jacsstuff.quizudo.utils.Consts;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.results.QuestionResultsSingleton;
import com.jacsstuff.quizudo.R;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.utils.Consts.IS_ANSWER_CORRECT_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.CORRECT_ANSWER_INTENT_EXTRA;
import static com.jacsstuff.quizudo.utils.Consts.QUIZ_SETTINGS_PREFERENCES;
import static com.jacsstuff.quizudo.utils.Consts.TRIVIA_INTENT_EXTRA;
/**
 * Created by John on 27/06/2016.
 *
 * Handles the bulk of the control logic for the Quiz Activity
 *
 */
public class QuizController {

    private QuizView view;
    private Context context;
    private Quiz quiz;
    private boolean isAnswerSubmittedOnTouch, isResultDisplayedAfterQuestion;
    private List<Integer> selectedItemPositions;
    private boolean isAnswerSubmitted = false;
    private DBWriter dbWriter;
    private AnswerPoolManager answerPoolManager;


    QuizController(QuizView view, Context context){

        QuizSingleton quizSingleton = QuizSingleton.getInstance();
        quiz = quizSingleton.getQuiz();
        this.view = view;
        this.context = context;
        configureDbWriter();
        configureQuizPreferences();
        configureAnswerPoolManager();
        loadQuestion();
        setToolbarTitle();
        view.setAnswerChoices(quiz.getCurrentAnswerChoices());
        if(isAnswerSubmittedOnTouch){
            view.hideNextButton();
        }
        endActivityIfNoQuestions();
    }

    private void configureQuizPreferences(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(QUIZ_SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        this.isAnswerSubmittedOnTouch = sharedPreferences.getBoolean(Consts.PREVIOUS_SUBMIT_ANSWER_ON_TOUCH_PREFERENCE, false);
        this.isResultDisplayedAfterQuestion = sharedPreferences.getBoolean(Consts.PREVIOUS_SHOW_ANSWER_PREFERENCE, false);
    }

    private void configureDbWriter(){
        if(dbWriter == null){
            dbWriter = new DBWriter(context);
            quiz.setDbWriter(dbWriter);
        }
    }

    private void configureAnswerPoolManager(){
        final int MAX_ANSWER_CHOICES = 6;
        if(answerPoolManager == null){
            answerPoolManager = new AnswerPoolManagerImpl(context, MAX_ANSWER_CHOICES);
        }
        quiz.setAnswerPoolManager(answerPoolManager);
    }

    private void endActivityIfNoQuestions(){
        if(quiz.hasNoQuestions()){
            view.finish();
        }
    }

    public void setAnswer(int position){
        selectedItemPositions.clear();
        selectedItemPositions.add(position);
    }

    private boolean submitAnswer() {

        if (!selectedItemPositions.isEmpty()) {
            quiz.submitAnswers(selectedItemPositions);
            return true;
        }
        return false;
    }


    private void finishQuiz() {
        Log.i("QuizCtrl", "Entered finishQuiz()");
        quiz.finish();
        Intent intent = new Intent(context, QuizResultsActivity.class);
        context.startActivity(intent);
    }


    private String getCurrentCorrectAnswers() {

        List<String> currentCorrectAnswers = quiz.getCurrentCorrectAnswers();
        if (currentCorrectAnswers == null) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder();

        for (String correctAnswer : currentCorrectAnswers) {
            strBuilder.append(correctAnswer);
            strBuilder.append(", ");
        }
        if(strBuilder.length() > 1) {
            strBuilder.deleteCharAt(strBuilder.length() - 2); //removing the last comma.
        }
        return strBuilder.toString();
    }


    private void loadNextQuestion() {

        isAnswerSubmitted = false;
        if (quiz.isFinalQuestion()) {
            finishQuiz();
        }
        else {
            quiz.nextQuestion();
            loadQuestion();
            setToolbarTitle();
        }
    }

    private void setToolbarTitle(){
        int currentQuestion = quiz.getCurrentQuestionNumber();
        int totalQuestions = quiz.getTotalNumberOfQuestions();
        String title = context.getResources().getString(R.string.quiz_activity_title, currentQuestion, totalQuestions);
        view.setToolbarTitle(title);
    }


    // this assigns the current question data to the UI Views. It's called once when the quiz
    // starts and then each time the user completes a question (unless that question is the last question).
    private void loadQuestion(){
        view.setQuestion(quiz.getCurrentQuestionText());
        view.setQuestionCounter(quiz.getQuestionCounter());
        view.setAnswerChoices(quiz.getCurrentAnswerChoices());
        selectedItemPositions = new ArrayList<>();

        if(quiz.isFinalQuestion()){
            view.displayFinishButton();
        }
    }

    // what happens when the 'next question' button is clicked.
    public void nextButton(){
        if(submitAnswer()) {
            if (isResultDisplayedAfterQuestion) {
                displayResult();
                //loadNextQuestion(); // we won't load the next question because closing the result dialogue should do that for us.
                return;
            }
            loadNextQuestion();
        }
    }

    private void displayResult(){
        displayResultDialog(quiz.isCurrentAnswerCorrect(),  quiz.getCurrentQuestionTrivia(), getCurrentCorrectAnswers());
    }

    // if the dialog activity was started, it means that we previously answered a question
    // so, when that dialog activity finishes, we should move onto the next question.
    // can resume in 2 main ways: when the answer dialog closes, and when the screen rotates.
    // if the resume is triggered by the answer dialog closing,
    //  1. a var will be set on the singleton by the answer dialog
    //  2. we will check that the status of that var here, and reset the var.
    //  3. we will go to the next question
    // and then reset that
    public void notifyQuizResumed(){

        endThisActivityIfQuizIsOver();

        if(quiz.wasDialogClosed()){
            quiz.setDialogClosed(false);
            loadNextQuestion();
        }
    }

    // This is used when the 'back' button is pressed on the quiz results activity
    // Since this quiz has already finished, we want to go back to the configure quiz activity.
    private void endThisActivityIfQuizIsOver(){

        QuestionResultsSingleton questionResults = QuestionResultsSingleton.getInstance();
        if(questionResults.isQuizFinished()){
            questionResults.resetResults();
            view.finish();
        }
    }


    private void displayResultDialog(boolean isCorrect, String trivia, String correctAnswer){

        Intent intent = new Intent(context, AnswerDialogActivity.class);
        if(trivia == null){
            trivia = "";
        }
        if(correctAnswer == null){
            correctAnswer = "";
        }
        intent.putExtra(TRIVIA_INTENT_EXTRA, trivia);
        intent.putExtra(IS_ANSWER_CORRECT_INTENT_EXTRA, isCorrect);
        intent.putExtra(CORRECT_ANSWER_INTENT_EXTRA, correctAnswer);
        context.startActivity(intent);
    }



    public void answerItemClick(int position){
        setAnswer(position);
        if(isAnswerSubmittedOnTouch && !isAnswerSubmitted){
            submitAnswer();
            isAnswerSubmitted = true; //to stop the being displayed more than once on fast clicks, bool is resetted in loadNextQuestion()
            if(isResultDisplayedAfterQuestion){
                displayResult();
            }
            else{
                loadNextQuestion();
            }
        }
    }


    public void resultClick(){
            loadNextQuestion();
    }


}
