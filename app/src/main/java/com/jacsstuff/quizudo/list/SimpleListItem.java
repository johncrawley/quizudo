package com.jacsstuff.quizudo.list;

public class SimpleListItem {

    private String name;
    private long id;

    public SimpleListItem(String name, long id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return this.name;
    }


    public long getId(){
        return this.id;
    }
}
