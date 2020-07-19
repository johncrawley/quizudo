package com.jacsstuff.quizudo.creator.express;

import java.util.Map;

public class QuestionSetEntity {


    private String questionSetName;
    private int questionSetId;
    private String question;
    private String inverseQuestion;
    private Map<Integer, ChunkEntity> questionSubjectAndAnswers;

}
