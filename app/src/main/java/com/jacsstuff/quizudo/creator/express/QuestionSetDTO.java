package com.jacsstuff.quizudo.creator.express;

public class QuestionSetDTO {

    private String name;
    private int id;

    public QuestionSetDTO(String name, int id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

}
