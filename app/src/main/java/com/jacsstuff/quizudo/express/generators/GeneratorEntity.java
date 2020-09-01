package com.jacsstuff.quizudo.express.generators;

import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;

import java.util.ArrayList;
import java.util.List;

public class GeneratorEntity {


    private List<QuestionSetEntity> questionSetEntities;
    private String generatorName;

    public GeneratorEntity(){

        questionSetEntities = new ArrayList<>();
    }


    public void addQuestionSetEntity(QuestionSetEntity questionSetEntity){
        this.questionSetEntities.add(questionSetEntity);
    }

    public List<QuestionSetEntity> getQuestionSetEntities(){
        return questionSetEntities;
    }

    public String getGeneratorName(){
        return generatorName;
    }

    public void setGeneratorName(String name){
        this.generatorName = name;
    }

}
