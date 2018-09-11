package com.jacsstuff.quizudo.answerPool;

public interface AnswerPoolController {

    void addAnswerPool(String name);
    void showDialogWithText(String message, String answerPoolName);
    void refreshListFromDb();
    void closeConnections();
}
