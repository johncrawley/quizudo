package com.jacsstuff.quizudo.quiz;

import android.util.Log;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.model.Question;
import com.jacsstuff.quizudo.model.QuestionResult;
import com.jacsstuff.quizudo.results.QuestionResultsSingleton;
import com.jacsstuff.quizudo.model.QuestionsSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by John on 25/06/2016.
 *
 * Contains the state of the current quiz.
 */
public class Quiz {

    private int maxNumberOfQuestions;
    private int questionIndex;
    private Question currentQuestion;
    private List<String> currentAnswerChoices;
    private boolean isRunning = false;
    private QuestionResultsSingleton singletonResultsStore;
    private boolean isDialogOpen = false;
    private boolean hasCurrentQuestionBeenAnswered;
    private List<Integer> questionIds;
    private DBWriter dbWriter;
    private AnswerPoolManager answerPoolManager;

    private boolean isCurrentAnswerCorrect;

    Quiz() {
        singletonResultsStore = QuestionResultsSingleton.getInstance();
    }



    // still need to set max Number of questions
    public void createQuiz(DBWriter dbWriter, int maxQuestions) {
        isRunning = true;
        questionIndex = 0; //init at -1, because first question is index 0, and
        singletonResultsStore = QuestionResultsSingleton.getInstance();
        singletonResultsStore.resetResults();
        hasCurrentQuestionBeenAnswered = false;
        maxNumberOfQuestions = maxQuestions;
        logIfQuestionsAreNull();
        QuestionsSingleton questionsSingleton = QuestionsSingleton.getInstance();
        questionIds = questionsSingleton.getQuestionIds();
        this.dbWriter = dbWriter;
        shuffleQuestionOrder();
        assignCurrentQuestion();
    }

    public void setDbWriter(DBWriter dbWriter) {
        this.dbWriter = dbWriter;
    }

    public void setAnswerPoolManager(AnswerPoolManager answerPoolManager){

        this.answerPoolManager = answerPoolManager;
    }

    public void retry() {
        isRunning = true;
        singletonResultsStore.resetResults();
        hasCurrentQuestionBeenAnswered = false;
        questionIndex = 0;
        logIfQuestionsAreNull();
        assignCurrentQuestion();
    }


    private void logIfQuestionsAreNull() {
        if (questionIds == null) {
            Log.i("Quiz - createQuiz()", "question ID list is null before loading from store.");
        }
    }

    private void shuffleQuestionOrder(){
        if(questionIds != null){
            Collections.shuffle(questionIds, ThreadLocalRandom.current());
        }

    }


    private void assignCurrentQuestion(){
        if(questionIds !=null && !questionIds.isEmpty()){
            currentQuestion = dbWriter.getQuestion(questionIds.get(questionIndex));


        }
        else {
            Log.i("Quiz - createQuiz()", "questions list is null or empty");
        }
    }


    public void setDialogClosed(boolean isOpen){
        this.isDialogOpen = isOpen;
    }

    public boolean wasDialogClosed(){
        return this.isDialogOpen;
    }

    public void finish(){
        singletonResultsStore.setQuizFinished(true);
        this.isRunning = false;
    }

    public boolean isRunning(){
        return this.isRunning;
    }

    public boolean hasNoQuestions(){
        return this.questionIds.isEmpty();
    }


    public void submitAnswers(List <Integer> selectedAnswerPositions){
        if(hasCurrentQuestionBeenAnswered){
            return;
        }
        String chosenAnswer = "";
        for(int position : selectedAnswerPositions){
            chosenAnswer = currentAnswerChoices.get(position);
        }

        QuestionResult questionResult = new QuestionResult(currentQuestion, questionIndex + 1, chosenAnswer);
        isCurrentAnswerCorrect = questionResult.wasAnsweredCorrectly();
        questionResult.setQuestionNumber(questionIndex);
        singletonResultsStore.addResult(questionResult);
        hasCurrentQuestionBeenAnswered = true;
        //Log.i("Quiz", "#" + questionIndex + ".  answer given: " + asString(chosenAnswers) + "  actual answers: "+ asString(questionResult.getCorrectAnswers()));
    }


    public boolean isFinalQuestion(){
        //Log.i("Quiz isFinalQuestion()", "maxNumberOfQuestions: "+ maxNumberOfQuestions + " questionIndex: " +  questionIndex);
        return questionIndex == (maxNumberOfQuestions - 1);
    }


    // We assume that all questions from the database will be properly formed,
    // mostly because we're loading the questions one by one, and we have to commit
    // to a total number of questions selected, i.e. if we let a use select 10 questions
    // from a pool of 10, and 5 of them aren't properly formed, the user will end of wondering
    // where the other 5 questions went.
    public void nextQuestion(){
        currentQuestion = dbWriter.getQuestion(questionIds.get(++questionIndex));
        hasCurrentQuestionBeenAnswered = false;
    }


    public int getTotalNumberOfQuestions(){
        return maxNumberOfQuestions;
    }

    public String getQuestionCounter(){
        return questionIndex + 1 + "/" + ( maxNumberOfQuestions);
    }

    public String getCurrentQuestionText(){
        if(currentQuestion != null) {
            return currentQuestion.getQuestionText();
        }
        return "";
    }

    // for display purposes, the first question will be of index 0 in the list,
    // but we want to display it as "Question 1", for example, so adding 1 to the return value.
    public int getCurrentQuestionNumber(){
        return questionIndex + 1;
    }

    private void log(String msg){
        Log.i("Quiz", msg);
    }

    public String[] getCurrentAnswerChoices(){
        if(!questionIds.isEmpty()) {
            log("question answer pool : " + currentQuestion.getAnswerPoolName());
            currentAnswerChoices = answerPoolManager.generateShuffledAnswerList(currentQuestion.getAnswerPoolName(),
                                                                                currentQuestion.getAnswerChoices(),
                                                                                currentQuestion.getCorrectAnswer());


            return currentAnswerChoices.toArray(new String[currentAnswerChoices.size()]);
        }
        return new String[0];
    }


    public List<String> getCurrentCorrectAnswers(){
        if(!questionIds.isEmpty()){
           List<String> list =  new ArrayList<>();
           list.add(currentQuestion.getCorrectAnswer());
           return list;
        }
        return null;
    }

    public String getCurrentQuestionTrivia(){
        if(currentQuestion!= null){
            return currentQuestion.getTrivia();
        }
        return "";
    }

    public boolean isCurrentAnswerCorrect(){
        return isCurrentAnswerCorrect;
    }


}
