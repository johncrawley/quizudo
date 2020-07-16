package com.jacsstuff.quizudo.results;

import android.content.Context;
import android.content.res.Resources;

import com.jacsstuff.quizudo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 05/08/2016.
 *
 * Used to derive the appropriate messages (based on quiz score) that are displayed in the quiz results activity.
 */
public class ResultsMessageHelper {

    private Map<Integer, String> resultMessageMap;
    private Resources resources;
    public ResultsMessageHelper(Context context){
        resultMessageMap = new HashMap<>();

        resources = context.getResources();

        resultMessageMap.put(20,resources.getString(R.string.results_message_less_than_twenty));
        resultMessageMap.put(40,resources.getString(R.string.results_message_less_than_forty));
        resultMessageMap.put(50,resources.getString(R.string.results_message_less_than_fifty));
        resultMessageMap.put(60,resources.getString(R.string.results_message_less_than_sixty));
        resultMessageMap.put(70,resources.getString(R.string.results_message_less_than_seventy));
        resultMessageMap.put(80,resources.getString(R.string.results_message_less_than_eighty));
        resultMessageMap.put(90,resources.getString(R.string.results_message_less_than_ninty));
        resultMessageMap.put(99,resources.getString(R.string.results_message_less_than_perfect));
    }


    public String getResultMessage(int correctAnswers, int totalQuestionNumber){
        // calling the zero and perfect messages out individually because we don't want them
        // getting called based on a rounded up/rounded down percentage.
        if(correctAnswers  == 0){
            return resources.getString(R.string.results_message_none_correct);
        }
        else if(correctAnswers == totalQuestionNumber){
            return resources.getString(R.string.results_message_perfect);
        }
        float percentage = ((float)correctAnswers / totalQuestionNumber) * 100;
        return getResultMessage((int)percentage);
    }



    private String getResultMessage(int scorePercentage){
        if(scorePercentage < 0 || scorePercentage > 100){
            return "";
        }

        int currentHighest = 1000;
        // we're looking for the lowest threshold that is higher than the provided scorePercentage
        for(int key : resultMessageMap.keySet()){
            if(scorePercentage < key){
                if(key < currentHighest){
                    currentHighest = key;
                }
            }
        }
        return resultMessageMap.get(currentHighest);

    }
}
