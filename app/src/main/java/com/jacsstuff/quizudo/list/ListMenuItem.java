package com.jacsstuff.quizudo.list;

/**
 * Created by John on 29/10/2016.
 */
public class ListMenuItem {

    private int id;
    private String text;

    public ListMenuItem(int id, String text){

        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
