package com.jacsstuff.quizudo.creator;

import java.util.ArrayList;
import java.util.List;

// Represents one question page on the editor
public class QuestionItem {

    private String question;
    private String correctAnswer;
    private List<String> answerChoices;
    private boolean usesDefaultAnswerPool;
    private String chosenAnswerPool;
    private String topics;
    private String trivia;


    QuestionItem(int answerChoiceLimit){

        this.question = "";
        this.correctAnswer = "";
        this.answerChoices = new ArrayList<>();
        this.usesDefaultAnswerPool = true;
        this.topics = "";
        this.chosenAnswerPool = "";
        this.trivia = "";
        for(int i =0 ; i < answerChoiceLimit; i++){
            answerChoices.add("");
        }

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getAnswerChoices() {
        return answerChoices;
    }

    public void setAnswerChoices(List<String> answerChoices) {
        this.answerChoices = answerChoices;
    }

    public boolean isUsingDefaultAnswerPool() {
        return usesDefaultAnswerPool;
    }

    public void setUsesDefaultAnswerPool(boolean usesDefaultAnswerPool) {
        this.usesDefaultAnswerPool = usesDefaultAnswerPool;
    }

    public String getChosenAnswerPool() {
        return chosenAnswerPool;
    }

    public void setChosenAnswerPool(String chosenAnswerPool) {
        this.chosenAnswerPool = chosenAnswerPool;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getTrivia() {
        return trivia;
    }

    public void setTrivia(String trivia) {
        this.trivia = trivia;
    }

}
