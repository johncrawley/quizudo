package com.jacsstuff.quizudo.quiz;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.quizudo.answerPool.AnswerPoolDBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AnswerPoolManagerImpl implements AnswerPoolManager {

    private Map<String, List<String>> retrievedAnswerPools;
    private AnswerPoolDBManager db;
    private final int MAX_ANSWER_CHOICES;

    AnswerPoolManagerImpl(Context context, int maxAnswerChoices){

        db = new AnswerPoolDBManager(context);
        retrievedAnswerPools = new HashMap<>();
        this.MAX_ANSWER_CHOICES = maxAnswerChoices;

    }

    @Override
    public List<String> generateShuffledAnswerList(String answerPoolName, List<String> existingAnswers, String correctAnswer) {

        Set<String> answerChoices = new HashSet<>(existingAnswers);

        answerChoices.add(correctAnswer);
        if (answerChoices.size() >= MAX_ANSWER_CHOICES) {
            removeEmpties(answerChoices);
            return getShuffledList(answerChoices);
        }
        addAnswerPoolItemsToSet(answerChoices, getAnswerPool(answerPoolName));
        answerChoices = removeEmpties(answerChoices);
        return getShuffledList(answerChoices);
    }

    private void addAnswerPoolItemsToSet(Set<String> set, List<String> answerPool){

        if(answerPool == null || answerPool.isEmpty()){
            return;
        }

        while (set.size() < MAX_ANSWER_CHOICES && answerPool.size() > 0) {
            int index = ThreadLocalRandom.current().nextInt( answerPool.size());
            set.add(answerPool.remove(index));

        }
    }

    private void log(String msg){

        Log.i("AnswerPoolMngr", msg);
    }

    private void logList(List<String> answerPool){

        StringBuilder str = new StringBuilder("retrievedAnswerPool contents:");
        for(String answer: answerPool){
            str.append(" ");
            str.append(answer);
        }
        log( str.toString());

    }

    private List<String> getAnswerPool(String answerPoolName){
        List<String> answerPool;

        if (retrievedAnswerPools.containsKey(answerPoolName)) {
            answerPool = retrievedAnswerPools.get(answerPoolName);
        }
        else {
            List<String> retrievedAnswerPool = db.getAnswerPoolItems(answerPoolName);
            retrievedAnswerPools.put(answerPoolName, retrievedAnswerPool);
            answerPool = retrievedAnswerPool;
        }
        return new ArrayList<>(answerPool); // because we'll be removing items from the return list, so need to make a new one.
    }

    private Set<String> removeEmpties(Set<String> set){
        Set<String> outputSet = new HashSet<>();
        for(String item: set){
            if(!item.trim().isEmpty()){
                outputSet.add(item);
            }
        }
        return outputSet;
    }

    private List<String> getShuffledList(Set<String> set){
        List<String> list = new ArrayList<>(set);
        Collections.shuffle(list, ThreadLocalRandom.current());
        return list;
    }
}
