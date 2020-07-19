package com.jacsstuff.quizudo.creator.express;

public class ChunkEntity {

    private String questionSubject;
    private String answer;

    public ChunkEntity(String questionSubject, String answer){
        this.questionSubject = questionSubject;
        this.answer = answer;
    }

    public void setQuestionSubject(String questionSubject){
        this.questionSubject = questionSubject;
    }

    public void setAnswer(String answer){
        this.answer = answer;
    }

    public String getQuestionSubject(){
        return this.questionSubject;
    }

    public String getAnswer(){
        return this.answer;
    }

}
