package com.jacsstuff.quizudo.model;

import com.jacsstuff.quizudo.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.jacsstuff.quizudo.utils.Consts.ANSWER_CHOICE_DELIMITER;
import static com.jacsstuff.quizudo.utils.Consts.TOPICS_DELIMITER;

public class Question {
    private String questionText;
    private List<String> answerChoices;
    private String correctAnswer;
    private List<String> topics;
    private String trivia;
    private int difficulty;
    private String flags;
    private String answerPoolName;


    public Question() {
        this.answerChoices = new ArrayList<>();
        this.correctAnswer = "";
        this.trivia = "";
        this.questionText = "";
        this.topics = new ArrayList<>();
    }

    //new Question(questionText, correctAnswer, answerChoices, trivia);

    public Question(String questionText, String correctAnswer, String answerChoices, String trivia, String topics, String answerPoolName) {
        this.questionText = questionText;
        this.answerChoices = parseString(answerChoices, ANSWER_CHOICE_DELIMITER);
        this.correctAnswer = correctAnswer;
        this.trivia = trivia;
        this.topics = parseString(topics, TOPICS_DELIMITER);
        this.difficulty = 3;
        this.flags = "";
        this.answerPoolName = answerPoolName;
    }



    public Question(String questionText, String correctAnswer, List<String> answerChoices, String trivia, String topics, String answerPoolName) {
        this.questionText = questionText;
        this.answerChoices = new ArrayList<>(answerChoices);
        this.correctAnswer = correctAnswer;
        this.trivia = trivia;
        this.topics = parseString(topics, Consts.TOPICS_DELIMITER);
        this.difficulty = 3;
        this.flags = "";
        this.answerPoolName = answerPoolName;
    }

    public String getAnswerPoolName(){
        return this.answerPoolName;
    }
    public int getDifficulty(){
        return this.difficulty;
    }

    public String getFlags(){
        return this.flags;
    }


    private List<String> parseString(String str, String delimiter){

        return Arrays.asList(str.split(delimiter));

    }

    private String parseList(List<String> list, String delimiter){

        return android.text.TextUtils.join(delimiter, list);
    }


    public String getAnswerChoicesAsString(){
        return parseList(this.answerChoices, ANSWER_CHOICE_DELIMITER);
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAnswerChoices() {
        return answerChoices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getTopicsAsString(){
        return parseList(topics, TOPICS_DELIMITER);
    }

    public String getTrivia() {
        return trivia;
    }



    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setAnswerChoices(List<String> answerChoices) {
        this.answerChoices = new ArrayList<>(answerChoices);
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public void setTrivia(String trivia) {
        this.trivia = trivia;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public void setAnswerPoolName(String answerPoolName) {
        this.answerPoolName = answerPoolName;
    }

}
