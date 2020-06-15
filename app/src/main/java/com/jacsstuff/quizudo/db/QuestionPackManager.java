package com.jacsstuff.quizudo.db;

import com.jacsstuff.quiz.Question;
import com.jacsstuff.quiz.QuestionPack;
import com.jacsstuff.quizudo.model.QuestionPackOverview;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by John on 19/12/2016.
 * Interface.
 */
public interface QuestionPackManager {

    List<QuestionPackOverview> getQuestionPackOverviews();
    List <Integer> getQuestionIds(Set<Integer> ids);
    void  closeConnections();
    void init();
}

