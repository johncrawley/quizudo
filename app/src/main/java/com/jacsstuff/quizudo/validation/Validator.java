package com.jacsstuff.quizudo.validation;

import android.util.Log;

import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.model.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {


    public void validate(QuestionPackDbEntity qp){

        if(qp.getName() == null || qp.getName().isEmpty()){
            return;
        }
        List <Question> questions = qp.getQuestions();

        Log.i("Validator", "QP Entity has quesitons: " + qp.hasQuestions());
        qp.setQuestions(getValidQuestions(questions));
        Log.i("Validator", "QP Entity has quesitons after validate: " + qp.hasQuestions());
    }

    private List<Question> getValidQuestions(List<Question> questions){


        Map<String, Question> questionExistsMap = new HashMap<>();

        for(Question question : questions){
            if(!isQuestionValid(question)){
                continue;
            }
            String questionText = question.getQuestionText();
            if(questionExistsMap.containsKey(questionText)){
                continue;
            }
            questionExistsMap.put(questionText, question);

        }
        List <Question> outputList = new ArrayList<>(questionExistsMap.values());

        for(Question question : outputList){
            verifyUniqueAnswerChoices(question);
        }


        return outputList;

    }

    private void verifyUniqueAnswerChoices(Question question){

        List<String> answerChoices = question.getAnswerChoices();
        // the boolean being whether or not the key is the correct answer text
        // because we want to remove any incorrect answers that are identical to the correct answer.
        Map <String, Boolean> answerMap = new HashMap<>();
        List<String> verifiedAnswerList = new ArrayList<>();
        answerMap.put(question.getCorrectAnswer(), true);

        for(String answerChoice : answerChoices){

            if(answerMap.containsKey(answerChoice)){
                continue;
            }
            answerMap.put(answerChoice, false);
        }
        for(String answer : answerMap.keySet()){
            if(answerMap.get(answer) ){
                continue;
            }
            verifiedAnswerList.add(answer);
        }
        question.setAnswerChoices(verifiedAnswerList);
    }


    private boolean isQuestionValid(Question q){

        return hasValidQuestionText(q) && hasValidCorrectAnswer(q) && hasValidAnswerChoices(q);

    }

    // looking for at least 1 good answer choice, unless using an answer pool
    private boolean hasValidAnswerChoices(Question q){

        if(hasValidAnswerPool(q)){
            return true;
        }
        for(String answerChoice : q.getAnswerChoices()){

            if(answerChoice.trim().isEmpty()){
                continue;
            }
            return true;
        }
        Log.i("Validator", "no valid answer choices for question : " + q.getQuestionText());
        return false;
    }

    private boolean hasValidAnswerPool(Question q){
        return !q.getAnswerPoolName().trim().isEmpty();

    }

    private boolean hasValidQuestionText(Question q){
        return !isNullOrEmpty(q.getQuestionText());
    }
    private boolean hasValidCorrectAnswer(Question q){
        return !isNullOrEmpty(q.getCorrectAnswer());
    }

    private boolean isNullOrEmpty(String str){

        return str == null || str.isEmpty();
    }



}
