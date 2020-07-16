package com.jacsstuff.quizudo.validation;


import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.model.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {


    public void validate(QuestionPackDbEntity qp){
        if(isNullOrEmpty(qp.getName())){
            return;
        }
        List <Question> questions = qp.getQuestions();
        qp.setQuestions(getValidQuestions(questions));
    }


    private List<Question> getValidQuestions(List<Question> questions){
        Map<String, Question> questionExistsMap = new HashMap<>();

        for(Question question : questions){
           addValidQuestionTo(questionExistsMap, question);
        }
        List <Question> outputList = new ArrayList<>(questionExistsMap.values());

        for(Question question : outputList){
            verifyUniqueAnswerChoices(question);
        }
        return outputList;
    }


    private void addValidQuestionTo(Map<String, Question> questionExistsMap, Question question){
        if(!isQuestionValid(question)){
            return;
        }
        String questionText = question.getQuestionText();
        if(questionExistsMap.containsKey(questionText)){
            return;
        }
        questionExistsMap.put(questionText, question);
    }


    private void verifyUniqueAnswerChoices(Question question){

        List<String> answerChoices = question.getAnswerChoices();
        // the boolean being whether or not the key is the correct answer text
        // because we want to remove any incorrect answers that are identical to the correct answer.
        Map <String, Boolean> answerMap = new HashMap<>();
        answerMap.put(question.getCorrectAnswer(), true);
        addAnswers(answerMap, answerChoices);
        List<String> verifiedAnswerList = getVerifiedAnswerListFrom(answerMap);
        question.setAnswerChoices(verifiedAnswerList);
    }

    private void addAnswers(Map<String, Boolean> answerMap, List<String> answerChoices){
        for(String answerChoice : answerChoices){
            if(answerMap.containsKey(answerChoice)){
                continue;
            }
            answerMap.put(answerChoice, false);
        }
    }


    private List<String> getVerifiedAnswerListFrom(Map<String, Boolean> answerMap){
        List<String> verifiedAnswerList = new ArrayList<>();
        for(String answer : answerMap.keySet()){
            if(answerMap.get(answer) ){
                continue;
            }
            verifiedAnswerList.add(answer);
        }
        return verifiedAnswerList;
    }


    private boolean isQuestionValid(Question q){
        return hasValidQuestionText(q) && hasValidCorrectAnswer(q) && hasValidAnswerChoices(q);
    }


    private boolean hasValidAnswerChoices(Question q){
        return hasValidAnswerPool(q) || hasNonEmptyAnswerChoices(q.getAnswerChoices());
    }


    private boolean hasNonEmptyAnswerChoices(List<String> answerChoices){
        for(String answerChoice : answerChoices){
            if(!answerChoice.trim().isEmpty()){
                return true;
            }
        }
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
