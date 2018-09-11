package com.jacsstuff.quizudo.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 24/06/2016.
 */
public class QuestionResult {

    private String correctAnswer;
    private String incorrectChosenAnswer;
    private boolean wasAnsweredCorrectly;
    private int questionNumber;
    private String questionText;

    public QuestionResult(Question question, int questionNumber, String chosenAnswer){

        this.incorrectChosenAnswer = "";
        this.correctAnswer = question.getCorrectAnswer();
        this.questionNumber = questionNumber;
        this.questionText = question.getQuestionText();
        this.wasAnsweredCorrectly = isAnswerCorrect(chosenAnswer);
    }

    private boolean isAnswerCorrect(String chosenAnswer){


        if(this.correctAnswer.equals(chosenAnswer)){
            return true;
        }
        this.incorrectChosenAnswer = chosenAnswer;
        return false;
    }

    public void setQuestionNumber(int number){
        this.questionNumber = number;
    }


    //some of the answers supplied may be correct, but if only 1 is incorrect
    // then the overall answer is incorrect.
    public void setIncorrectResult(String correctAnswer, String incorrectAnswer){
        this.wasAnsweredCorrectly = false;
        this.correctAnswer = correctAnswer;
        this.incorrectChosenAnswer = incorrectAnswer;
    }

    private int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionResultText(){
        return (this.getQuestionNumber() + 1) + ". "  + this.getQuestionText();
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean wasAnsweredCorrectly() {
        return wasAnsweredCorrectly;
    }



}
