package com.jacsstuff.quizudo.db;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.quiz.Question;
import com.jacsstuff.quiz.QuestionPack;
import com.jacsstuff.quizudo.model.QuestionPackOverview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by John on 31/12/2016.
 *
 *  Takes care of managing the the db connection for loading and saving question packs
 *  Also contains a detailed list of question packs available
 */
public class QuestionPackDBManager implements QuestionPackManager {

    private DBWriter dbWriter;
    private Map<Integer, QuestionPackOverview> questionPacks;


    public QuestionPackDBManager(Context context){
        questionPacks = new HashMap<>();
        dbWriter = new DBWriter(context);
    }


    public int saveQuestionPacks(Map<String, String> dataMap, String authorName){

        int successCount = 0;
        for(String key: dataMap.keySet()){
           if(dbWriter.addQuestionPack(dataMap.get(key))){
               successCount++;
           }
        }
        return successCount;
    }

    public List<QuestionPackOverview> getQuestionPackDetails() {
        return dbWriter.getQuestionPackDetails();
    }



    private Set<String> getQuestionPackUniqueNames(Set <Integer> ids){

        Set <String> uniqueNames = new HashSet<>();
        for(int id: ids){
            QuestionPackOverview qpDetail = questionPacks.get(id);
            if(qpDetail != null){
                uniqueNames.add(qpDetail.getUniqueName());
            }
        }
        return uniqueNames;
    }

    public List <QuestionPack> getQuestionPacks(Set<Integer> ids){
        return null;
    }

    public List <Question> getQuestions(Set<Integer> keys){
        StringBuilder idBldr = new StringBuilder();
        Set<Long> ids = new HashSet<>();
        for(int key: keys){
            QuestionPackOverview qpDetail = questionPacks.get(key);
            if(qpDetail != null) {
                ids.add(qpDetail.getId());
            }
        }

        for(Long id : ids){
            idBldr.append(" ");
            idBldr.append(id);
        }
        Log.i("QPDBManager","getQuestionPacks() - ids: "+ idBldr.toString());
     return null; //dbWriter.getQuestions(getRowIdSet(ids));
      //  return null; //dbWriter.getQuestions(ids);
    }


    public List<Integer> getQuestionIds(Set<Integer> questionPackIds){

         return dbWriter.getQuestionIdsFromQuestionPackIds(questionPackIds);
    }

    public boolean isEmpty(){
        return questionPacks.isEmpty();
    }


    public void closeConnections(){

        if(dbWriter != null){
            dbWriter.closeConnection();
        }
    }
}
