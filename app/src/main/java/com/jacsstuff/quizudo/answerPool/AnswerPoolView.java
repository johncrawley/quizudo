package com.jacsstuff.quizudo.answerPool;

import java.util.List;

public interface AnswerPoolView {

    void updateList();
    void setupList(List<String> answerPoolNames);
    void addToList(String name);
}
