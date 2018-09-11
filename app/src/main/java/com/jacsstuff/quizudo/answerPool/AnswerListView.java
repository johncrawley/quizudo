package com.jacsstuff.quizudo.answerPool;

import java.util.List;

public interface AnswerListView {


    void updateList(String text);

    void setupList(List<String> answerPoolNames);
}
