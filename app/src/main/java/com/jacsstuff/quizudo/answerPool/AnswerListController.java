package com.jacsstuff.quizudo.answerPool;

public interface AnswerListController {

    void addAnswer(String text);
    void showDialogWithText(String message, String answer, String answerPoolName);
    void refreshListFromDb();
}
