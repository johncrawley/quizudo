package com.jacsstuff.quizudo.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 30/08/2016.
 *
 * Used for saving the Question Pack File objects chosen by the user, also saves the settings
 * in the 'configure quiz' activity for the next time a quiz is created.
 */
public class QuestionsSingleton {

    private static QuestionsSingleton instance;
    private List<Integer> questionIds;

    private QuestionsSingleton(){
        questionIds = new ArrayList<>();
    }

    public static QuestionsSingleton getInstance(){
        if(instance == null){
            instance = new QuestionsSingleton();
        }
        return instance;
    }

    public List<Integer> getQuestionIds(){
        return questionIds;
    }

    public void reset(){
        questionIds = new ArrayList<>();
    }

    public void addNewQuestionIds(List<Integer> questionIds){
        Log.i("QuestionsSingleton", "Number of questionIds added : " + questionIds.size());
        this.questionIds.clear();
        this.questionIds = new ArrayList<>(questionIds);
    }

    public boolean isEmpty(){
        return this.questionIds.isEmpty();
    }

    public int getNumberOfQuestions(){
        return this.questionIds.size();
    }

}
