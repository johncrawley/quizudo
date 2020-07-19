package com.jacsstuff.quizudo.list;

public interface ListActionExecutor {

    public void onClick(SimpleListItem item);
    public void onLongClick(SimpleListItem item);
    public void onTextEntered(String text);

}
