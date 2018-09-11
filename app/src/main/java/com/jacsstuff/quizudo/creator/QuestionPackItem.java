package com.jacsstuff.quizudo.creator;

/*
 An object to hold data about the QuestionPack that the user is creating.
 */
public class QuestionPackItem {

    private String questionPackName;
    private String description;
    private String defaultTopics;
    private String defaultAnswerPool;
    private String author;


    QuestionPackItem(){
        this.questionPackName = "";
        this.description = "";
        this. defaultTopics = "";
        this.defaultAnswerPool = "";
        this.author = "";
    }

    public String getQuestionPackName() {
        return questionPackName;
    }

    public void setQuestionPackName(String questionPackName) {
        this.questionPackName = questionPackName;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultTopics() {
        return defaultTopics;
    }

    public void setDefaultTopics(String defaultTopics) {
        this.defaultTopics = defaultTopics;
    }

    public String getDefaultAnswerPool() {
        return defaultAnswerPool;
    }

    public void setDefaultAnswerPool(String defaultAnswerPool) {
        this.defaultAnswerPool = defaultAnswerPool;
    }

}
