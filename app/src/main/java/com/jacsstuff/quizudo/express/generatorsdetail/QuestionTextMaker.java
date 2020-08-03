package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;

import com.jacsstuff.quizudo.R;

public class QuestionTextMaker {

    private final String PLACEHOLDER;

    public QuestionTextMaker(Context context){
        PLACEHOLDER = context.getString(R.string.question_generator_question_subject_placeholder);
    }

    public String getQuestionText(String template, String subject){
        if(template == null || template.isEmpty()){
            return "";
        }
        if(template.contains(PLACEHOLDER)){
            return template.replace(PLACEHOLDER, subject);
        }
        int lastIndex = template.length() -1;
        char lastChar = template.charAt(lastIndex);
        if(lastChar == '?' || lastChar == '.'){
            return template.substring(0, lastIndex) + subject + lastChar;
        }
        return template + subject + '?';
    }

}
