package com.jacsstuff.quizudo.express.generators.parser;

public class BadParserResult extends ParserResult {

    public BadParserResult(String message, int line){
        this.success = false;
        this.message = message;
        this.line = line;
    }
}
