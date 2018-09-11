package com.jacsstuff.quizudo.download;

public class Response {

    private String body;
    public enum Code { SUCCESS, NOT_FOUND, SERVER_NOT_FOUND, SERVER_ERROR, OTHER_ERROR  }
    private Code code;

    public Response(String body, Code code){
        this.body = body;
        this.code = code;
    }

    public String getBody(){
        return this.body;
    }

    public Code getCode(){
        return this.code;
    }

    public boolean codeEquals(Code code){
        return this.code == code;
    }
}
