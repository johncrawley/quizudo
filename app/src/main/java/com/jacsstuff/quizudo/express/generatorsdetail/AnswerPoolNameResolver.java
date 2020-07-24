package com.jacsstuff.quizudo.express.generatorsdetail;

public class AnswerPoolNameResolver {

    static String getName(String generatorName, String questionSetName){
        return generatorName + "_" + questionSetName;
    }
}
