package com.jacsstuff.quizudo.quiz;

public interface QuizView {

   void setAnswerChoices(String[] currentAnswerChoices);
   void hideNextButton();
   void finish();
   void setToolbarTitle(String title);
   void setQuestionCounter(String counter);
   void setQuestion(String currentQuestionText);
   void displayFinishButton();
}
