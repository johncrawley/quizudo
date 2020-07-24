package com.jacsstuff.quizudo.express.questionset;

import android.content.Context;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.List;

public class ChunkParser {

    private String delimiter;

    public ChunkParser(Context context){
        delimiter = context.getString(R.string.question_generator_chunk_delimiter);
    }

    public ChunkEntity parse(String str){

        String questionSubject = str;
        String answer = "";
        if(str.contains(delimiter)){
            String [] array = str.split(delimiter);
            questionSubject = array[0];
            if(array.length > 1) {
                answer = array[1];
            }
        }

        return new ChunkEntity(questionSubject, answer);
    }

    public String getString(String subject, String answer){

        return subject + delimiter + answer;
    }


}
