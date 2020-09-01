package com.jacsstuff.quizudo.express.questionset;

import java.util.ArrayList;
import java.util.List;

public class QuestionSetEntity {

    private String name, questionTemplate;
    private List<ChunkEntity> chunkEntities;

    public QuestionSetEntity(String name, String questionTemplate){
        this.name = name;
        this.questionTemplate = questionTemplate;
        chunkEntities = new ArrayList<>();
    }

    public QuestionSetEntity(){
        this("","");
    }

    public void setName(String name){
        this.name = name;
    }

    public void setQuestionTemplate(String template){
        this.questionTemplate = template;
    }

    public String getName(){
        return name;
    }

    public String getQuestionTemplate(){
        return questionTemplate;
    }

    public List<ChunkEntity> getChunkEntities(){
        return chunkEntities;
    }

    public void add(ChunkEntity chunkEntity){
        chunkEntities.add(chunkEntity);
    }

}
