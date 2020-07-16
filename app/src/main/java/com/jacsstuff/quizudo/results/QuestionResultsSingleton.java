package com.jacsstuff.quizudo.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton pattern to store results of questions and status of quiz - whether is has been completed already
 */
public class QuestionResultsSingleton {

    private List<QuestionResult> questionResults;
    private int correctAnswerCount = 0;
    // if the user hits 'back' in the results activity, we want to swiftly bring them back
    // to the createQuizActivity rather than let them answer the previous answer again
    // the quiz activity will check for this variable on resume and redirect the user, if the quiz has already been completed.
    private boolean isQuizFinished = false;


    private static QuestionResultsSingleton instance;


    private QuestionResultsSingleton(){
        questionResults = new ArrayList<>();
    }

    public static QuestionResultsSingleton getInstance(){
        if(instance == null){
            instance = new QuestionResultsSingleton();
        }
        return instance;
    }

    public boolean isQuizFinished(){
        return this.isQuizFinished;
    }
    public void setQuizFinished(boolean isFinished){
        this.isQuizFinished = isFinished;
    }

    public void resetResults(){
        this.questionResults = new ArrayList<>();
        setQuizFinished(false);
        this.correctAnswerCount = 0;
    }

    public void addResult(QuestionResult result){
        questionResults.add(result);
        if(result.wasAnsweredCorrectly()){
            correctAnswerCount++;
        }
    }

    public int getCorrectAnswerCount(){
        return correctAnswerCount;
    }

    public List<QuestionResult> getResults(){
        return questionResults;
    }
}
