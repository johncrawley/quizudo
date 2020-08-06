package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;

import com.jacsstuff.quizudo.R;

public class QuestionTextMaker {

    private final String PLACEHOLDER;

    public QuestionTextMaker(String placeholder){
        PLACEHOLDER = placeholder;
    }

    final char QUESTION_MARK = '?';
    final char FULL_STOP = '.';

    public String getQuestionText(String template, String subject){

        if(template == null || isNullOrEmpty(subject)){
            return "";
        }
        subject = subject.trim();
        template = template.trim();
        template = addQuestionMarkIfRequired(template);

        if(template.contains(PLACEHOLDER)){
            return  template.replace(PLACEHOLDER, subject);
        }

        int lastIndex = template.length() -1;
        char lastChar = template.charAt(lastIndex);
        if(template.length() > 1) {
            subject = " " + subject;
        }
        return template.substring(0, lastIndex) + subject + lastChar;
    }

    private boolean isNullOrEmpty(String str){
        return str == null || str.trim().isEmpty();
    }

    private String addQuestionMarkIfRequired(String str){
        if(str.isEmpty()){
            return str + QUESTION_MARK;
        }
        int lastIndex = str.length() -1;
        char lastChar = str.charAt(lastIndex);

        if(lastChar != QUESTION_MARK && lastChar != FULL_STOP){
            return str + QUESTION_MARK;
        }
        return str;
    }


}
