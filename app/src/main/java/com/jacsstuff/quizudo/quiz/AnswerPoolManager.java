package com.jacsstuff.quizudo.quiz;

import java.util.List;

public interface AnswerPoolManager {

    List<String> generateShuffledAnswerList(String answerPoolName, List<String> existingAnswers, String correctAnswer);
}
