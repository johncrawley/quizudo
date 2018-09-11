package com.jacsstuff.quizudo.creator;

import android.util.Log;

import com.jacsstuff.quizudo.utils.Consts;
import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.model.Question;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuestionPackItemConverter {

    private final String NONE_OPTION;

    QuestionPackItemConverter(String noneOption){
        this.NONE_OPTION = noneOption;
    }

    /*
            We're taking in the question pack info and list of question items from the UI, and convert it to this
            object that already has a db writer for it.

            It's not just a simple conversion, we skip answer choices that are blank, and we skip whole question items that aren't complete


     */

    public QuestionPackDbEntity convertToQuestionPackDbDetail(QuestionPackItem qpItem, List<QuestionItem> qItems){

        QuestionPackDbEntity qpDetail = new QuestionPackDbEntity();

        qpDetail.setNameAndAuthor(qpItem.getQuestionPackName(), qpItem.getAuthor());
        qpDetail.setDescription(qpItem.getDescription());
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        qpDetail.setDateCreated(date);
        qpDetail.setDateDownloaded(0);

        qpDetail.addQuestions(parseQuestions(qpItem, qItems));

        return qpDetail;
    }
/*
    private void log(String msg){
        Log.i("QP Converter", msg);
    }
*/






    private List<Question> parseQuestions(QuestionPackItem qpItem, List<QuestionItem> questionItems){
        Log.i("QP Converter", "number of question items :" + questionItems.size());

        List<Question> questions = new ArrayList<>();

        for(QuestionItem questionItem : questionItems){

            trimData(questionItem);
            if(!isQuestionValid(qpItem, questionItem)){
                continue;
            }
            String trivia = questionItem.getTrivia();
            String questionText = questionItem.getQuestion();
            String correctAnswer = questionItem.getCorrectAnswer();
            List<String> answerChoices = questionItem.getAnswerChoices();

            String topics = questionItem.getTopics();
            topics += Consts.TOPICS_DELIMITER + qpItem.getDefaultTopics();
            String answerPoolName = deriveAnswerPoolName(qpItem, questionItem);

            questions.add( new Question(questionText, correctAnswer, answerChoices, trivia, topics, answerPoolName));
        }
        return  questions;
    }

    private void trimData(QuestionItem q){

        q.setQuestion(      trimIfNotNull(q.getQuestion()));
        q.setTrivia(        trimIfNotNull(q.getTrivia()));
        q.setCorrectAnswer((trimIfNotNull(q.getCorrectAnswer())));
        q.setTopics(        trimIfNotNull(q.getTopics()));
        q.setAnswerChoices(trimListItems(q.getAnswerChoices()));
    }


    private String trimIfNotNull(String str){

        if(str == null){
            return null;
        }
        return str.trim();
    }

    private List<String> trimListItems(List<String> list){
        List <String> trimmedList = new ArrayList<>();
        if(list == null){
            return null;
        }

        for(String item : list){
            if(item == null){
               continue;
            }
            String trimmedItem = item.trim();
            if(!trimmedItem.isEmpty()){
               trimmedList.add(trimmedItem);
            }
        }
        return trimmedList;
    }



    private void log(String msg){
        Log.i("QPItemConverter", msg);
    }

    private String deriveAnswerPoolName(QuestionPackItem qp, QuestionItem q){

        String answerPoolName;
        if(q.isUsingDefaultAnswerPool()){
            answerPoolName = qp.getDefaultAnswerPool();
        }
        else{
                answerPoolName = q.getChosenAnswerPool();
        }

        return answerPoolName.equals(NONE_OPTION) ? "" : answerPoolName;
    }

    private boolean isQuestionValid(QuestionPackItem qp, QuestionItem q){
        boolean hasInvalidQuestionText = isNullOrEmpty(q.getQuestion());
        boolean hasInvalidCorrectAnswer = isNullOrEmpty(q.getCorrectAnswer());
        boolean hasInvalidAnswerChoices = hasInvalidAnswerChoices(qp, q);

        return !(hasInvalidQuestionText || hasInvalidCorrectAnswer || hasInvalidAnswerChoices);
    }

    private boolean hasInvalidAnswerChoices(QuestionPackItem qp, QuestionItem q){

        if(q.isUsingDefaultAnswerPool() && !qp.getDefaultAnswerPool().equals(NONE_OPTION)){
            return false;
        }
        else if(!q.getChosenAnswerPool().equals(NONE_OPTION)){
            return false;
        }
        return isNullOrEmpty(q.getAnswerChoices());
    }


    private boolean isNullOrEmpty(List<String> list){

        if(list == null || list.isEmpty()){
            return true;
        }
        for(String item : list){
            if(!isNullOrEmpty(item)){
                return false;
            }
        }
        return true;
    }

    private boolean isNullOrEmpty(String str){

        return str == null || str.isEmpty();
    }

}
