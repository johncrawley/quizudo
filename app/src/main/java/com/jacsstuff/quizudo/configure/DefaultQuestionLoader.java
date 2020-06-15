package com.jacsstuff.quizudo.configure;

import android.content.Context;
import android.content.SharedPreferences;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.utils.Consts;
import com.jacsstuff.quizudo.db.DBWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created by John on 23/01/2017.
 *
 * Finds all the default question packs that are saved as strings in the defaultQuestionPacks.xml and
 * loads them into the database. This should only be done once.
 */
class DefaultQuestionLoader {

    private Context context;

    DefaultQuestionLoader(Context context){
        this.context = context;
    }


    void loadQuestions(){
        if(isFirstAttempt()){
            DBWriter dbWriter = new DBWriter(context);
            List<Integer> ids = Arrays.asList(
                    R.raw.capital_cities,
                    R.raw.general_knowledge,
                    R.raw.the_periodic_table,
                    R.raw.musical_intervals);

            for(int questionPackId : ids){
                addQuestionPackFromFile(dbWriter, questionPackId);
            }
            saveAttemptMadePreference();
        }
    }


    private void addQuestionPackFromFile(DBWriter dbWriter, int fileId){
       dbWriter.addQuestionPack(getFileContents(fileId));
    }


    private boolean isFirstAttempt() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Consts.QUIZ_SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        return !sharedPreferences.getBoolean(Consts.WERE_DEFAULT_QPS_ADDED_PREFERENCE, false);
    }


    private void saveAttemptMadePreference(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Consts.QUIZ_SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Consts.WERE_DEFAULT_QPS_ADDED_PREFERENCE, true);
        editor.apply();
    }


    private String getFileContents(int resourceId) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder str = new StringBuilder();
        String line;

        try{
            while ((line = bufferedReader.readLine()) != null) {
                str.append(line);
            }
            inputStream.close();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();
    }
}
