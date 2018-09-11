package com.jacsstuff.quizudo.creator;

import java.util.List;

public interface QuizWriterView {

    void showQuestionPackScreen();
    void setQuestionPackName(String name);
    void setDescription(String description);
    void setDefaultAnswerPoolName(String answerPoolName);
    void setDefaultTopics(String topics);


    String getQuestionPackName();
    String getDescription();
    String getDefaultAnswerPoolName();
    String getDefaultTopics();

    void showQuestionScreen();
    void setPageNumber(int pageNumber);
    void setQuestionNumber(int questionNumber);
    void setQuestionText(String questionText);
    void setCorrectAnswerText(String correctAnswerText);
    void setTriviaText(String triviaText);
    void setTopics(String topics);
    void setUsesDefaultAnswerPool(boolean usesDefaultAnswerPool);
    void setAnswerPool(String answerPoolName);
    void setAnswerChoices(List<String> answerChoiceViews);

    void enableAnswerPoolSelection();
    void disableAnswerPoolSelection();

    String getQuestionText();
    String getCorrectAnswerText();
    boolean isUsingDefaultAnswerPool();
    String getAnswerPool();
    String getTopics();
    String getTriviaText();
    List<String> getAnswerChoices();

    void setFirstPageTitle();

    void setAnswerPoolChoices(List<String> answerPoolNames);

    void displaySaveSuccessMessage(int numberOfQuesetionsSaved);
    void displaySaveFailMessage();
    void displayNothingToSaveMessage();
    void disableLastButton();
    void disablePreviousButton();
    void disableFirstButton();
    void enableLastButton();
    void enablePreviousButton();
    void enableNextButton();
    void enableFirstButton();
}
