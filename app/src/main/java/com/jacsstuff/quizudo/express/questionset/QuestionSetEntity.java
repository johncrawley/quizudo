package com.jacsstuff.quizudo.express.questionset;

public class QuestionSetEntity {

    private String name, questionTemplate;

    public QuestionSetEntity(String name, String questionTemplate){
        this.name = name;
        this.questionTemplate = questionTemplate;
    }

    public String getName(){
        return name;
    }

    public String getQuestionTemplate(){
        return questionTemplate;
    }

}
