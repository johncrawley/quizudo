package com.jacsstuff.quizudo.express.questionset;

public class ChunkEntity {

    private String questionSubject;
    private String answer;
    private long id;

    public ChunkEntity(String questionSubject, String answer, long id){
        this.questionSubject = questionSubject;
        this.answer = answer;
        this.id = id;
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

    public long getId(){ return this.id; }

}
