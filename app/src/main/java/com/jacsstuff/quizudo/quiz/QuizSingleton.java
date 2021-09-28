package com.jacsstuff.quizudo.quiz;

import com.jacsstuff.quizudo.db.DBWriter;

/**
 * Created by John on 19/01/2017.
 *
 *  Used to keep  a Quiz object in memory between activities, just for the purpose
 *  of allowing the user to resume a quiz after pressing the back button.
 *
 */
public class QuizSingleton {

    private static QuizSingleton instance;
    private final Quiz quiz;

    private QuizSingleton(){
        quiz = new Quiz();
    }

    public static QuizSingleton getInstance(){
        if(instance == null){
            instance = new QuizSingleton();
        }
        return instance;
    }

    public Quiz getQuiz(){

        return this.quiz;
    }

    public void createNewQuiz(DBWriter dbWriter, int maxQuestions){
        quiz.createQuiz(dbWriter, maxQuestions);
    }

    public void retryQuiz(){
        quiz.retry();
    }

    public boolean isQuizRunning(){
        return quiz.isRunning();
    }

    public void killRunningQuiz(){
        quiz.finish();
    }

}
