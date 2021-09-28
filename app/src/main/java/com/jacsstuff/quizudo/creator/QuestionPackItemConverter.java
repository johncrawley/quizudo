package com.jacsstuff.quizudo.creator;

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


    private List<Question> parseQuestions(QuestionPackItem qpItem, List<QuestionItem> questionItems){
        List<Question> questions = new ArrayList<>();
        for(QuestionItem questionItem : questionItems){
            Question question = createQuestionFrom(qpItem, questionItem);
            if(question != null) {
                questions.add(question);
            }
        }
        return  questions;
    }


    private Question createQuestionFrom(QuestionPackItem qpItem, QuestionItem questionItem){
        trimData(questionItem);
        if(!isQuestionValid(qpItem, questionItem)){
            return null;
        }
        String trivia = questionItem.getTrivia();
        String questionText = questionItem.getQuestion();
        String correctAnswer = questionItem.getCorrectAnswer();
        List<String> answerChoices = questionItem.getAnswerChoices();

        String topics = questionItem.getTopics();
        topics += Consts.TOPICS_DELIMITER + qpItem.getDefaultTopics();
        String answerPoolName = deriveAnswerPoolName(qpItem, questionItem);
        return new Question(questionText, correctAnswer, answerChoices, trivia, topics, answerPoolName);
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
        if(list == null){
            return null;
        }
        List <String> trimmedList = new ArrayList<>();
        for(String item : list){
           trimAndAddToList(item, trimmedList);
        }
        return trimmedList;
    }


    private void trimAndAddToList(String item, List<String> list){
        if(item == null){
            return;
        }
        String trimmedItem = item.trim();
        if(!trimmedItem.isEmpty()){
            list.add(trimmedItem);
        }
    }


    private String deriveAnswerPoolName(QuestionPackItem qp, QuestionItem q){
        String answerPoolName = q.isUsingDefaultAnswerPool() ? qp.getDefaultAnswerPool() : q.getChosenAnswerPool();
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
        if(!q.getChosenAnswerPool().equals(NONE_OPTION)){
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
