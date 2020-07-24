package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;

import com.jacsstuff.quizudo.answerPool.AnswerPoolDBManager;
import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.ChunkParser;
import com.jacsstuff.quizudo.express.questionset.GeneratorQuestionSetDBManager;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionGenerator {

    private Context context;
    private GeneratorQuestionSetDBManager dbManager;
    private AnswerPoolDBManager answerPoolDBManager;
    private long generatorId;
    private String generatorName;
    private List<SimpleListItem> existingAnswerPools;
    ChunkParser chunkParser;

    public QuestionGenerator(Context context){
        this.context = context;
        dbManager = new GeneratorQuestionSetDBManager(context);
        answerPoolDBManager = new AnswerPoolDBManager(context);
        chunkParser = new ChunkParser(context);
    }


    public void addQuestionsTo(String questionPackName, SimpleListItem generator, List<SimpleListItem> questionSets){

        generatorName = generator.getName();
        generatorId = generator.getId();
        existingAnswerPools = answerPoolDBManager.getAnswerPools();

        Map<String, Long> answerPoolMap = new HashMap<>();
        for(SimpleListItem item: existingAnswerPools){
            answerPoolMap.put(item.getName(), item.getId());
        }


        for(SimpleListItem questionSet : questionSets){
            long questionSetId = questionSet.getId();
            List<SimpleListItem> items = dbManager.retrieveChunksFor(questionSetId);
            addAnswersToAnswerPool(items, questionSet.getName(), answerPoolMap);
        }

    }


    private void addAnswersToAnswerPool(List<SimpleListItem> items, String questionSetName, Map<String, Long> answerPoolMap){
        String answerPoolName = AnswerPoolNameResolver.getName(generatorName, questionSetName);
        long answerPoolId = createAnswerPoolIfDoesntExist(answerPoolName, answerPoolMap);
        answerPoolDBManager.addAnswerPoolItems(answerPoolId, getAnswers(items));
    }


    private long createAnswerPoolIfDoesntExist(String resolvedName, Map<String, Long> answerPoolMap){
        if(!answerPoolMap.containsKey(resolvedName)) {
            return answerPoolDBManager.addAnswerPool(resolvedName);
        }
        return answerPoolMap.get(resolvedName);
    }


    private List<String> getAnswers(List<SimpleListItem> items){
        List<String> answers = new ArrayList<>();
        for(SimpleListItem item : items){
            ChunkEntity chunk = chunkParser.parse(item.getName());
            answers.add(chunk.getAnswer());
        }
        return answers;
    }

}
