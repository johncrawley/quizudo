package com.jacsstuff.quizudo.express.generators.parser;

public abstract class ParserResult {

    String message;
    boolean success;
    int line;

    public ParserResult(){

    }

    public int getLine(){
        return line;
    }

    public String getMessage(){
        return message;
    }

    public boolean isSuccess(){
        return success;
    }

}
