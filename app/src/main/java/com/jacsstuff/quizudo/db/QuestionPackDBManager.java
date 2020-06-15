package com.jacsstuff.quizudo.db;

import android.content.Context;

import com.jacsstuff.quizudo.model.QuestionPackOverview;

import java.util.HashMap;
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
    private Context context;

    public QuestionPackDBManager(Context context){
        questionPacks = new HashMap<>();
        this.context = context;
    }

    @Override
    public void init(){
        dbWriter = new DBWriter(context);
    }

    @Override
    public List<QuestionPackOverview> getQuestionPackOverviews() {
        return dbWriter.getQuestionPackOverviews();
    }


    @Override
    public List<Integer> getQuestionIds(Set<Integer> questionPackIds){
         return dbWriter.getQuestionIdsFromQuestionPackIds(questionPackIds);
    }

    @Override
    public void closeConnections(){
        if(dbWriter != null){
            dbWriter.closeConnection();
        }
    }
}
