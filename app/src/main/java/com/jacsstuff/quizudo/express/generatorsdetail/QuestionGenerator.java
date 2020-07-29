package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;

import com.jacsstuff.quizudo.answerPool.AnswerPoolDBManager;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.db.QuestionPackDBManager;
import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.ChunkParser;
import com.jacsstuff.quizudo.express.questionset.GeneratorQuestionSetDBManager;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;
import com.jacsstuff.quizudo.list.SimpleListItem;
import com.jacsstuff.quizudo.model.Question;
import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.utils.DateProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionGenerator {

    private Context context;
    private GeneratorQuestionSetDBManager questionSetDBManager;
    private AnswerPoolDBManager answerPoolDBManager;
    private DBWriter dbWriter;
    private QuestionPackDBManager questionPackDBManager;
    private long generatorId;
    private String generatorName;
    private List<SimpleListItem> existingAnswerPools;
    ChunkParser chunkParser;

    public QuestionGenerator(Context context){
        this.context = context;
        questionSetDBManager = new GeneratorQuestionSetDBManager(context);
        answerPoolDBManager = new AnswerPoolDBManager(context);
        questionPackDBManager = new QuestionPackDBManager(context);
        dbWriter = new DBWriter(context);
        chunkParser = new ChunkParser(context);
    }


    public void addQuestionsTo(String questionPackName, SimpleListItem generator, List<SimpleListItem> questionSets){

        generatorName = generator.getName();
        generatorId = generator.getId();
        existingAnswerPools = answerPoolDBManager.getAnswerPools();

        long questionPackId = createQuestionPackIfDoesntExist(questionPackName);
        Map <String, Long> answerPoolMap = new HashMap<>();

        for(SimpleListItem item: existingAnswerPools){
            answerPoolMap.put(item.getName(), item.getId());
        }


        for(SimpleListItem questionSet : questionSets){
            long questionSetId = questionSet.getId();

            List<ChunkEntity> chunks = questionSetDBManager.retrieveChunksFor(questionSetId);
            List<SimpleListItem> items = getListItemsFromChunks(chunks);
            addAnswersToAnswerPool(items, questionSet.getName(), answerPoolMap);
            addQuestionsBasedOn(chunks, questionSetId, questionPackId);
        }

    }


    private List<SimpleListItem> getListItemsFromChunks(List<ChunkEntity> chunks){
        List<SimpleListItem> items = new ArrayList<>(chunks.size());
        for(ChunkEntity chunk: chunks){
            String displayStr = chunkParser.getString(chunk);
            items.add(new SimpleListItem(displayStr, chunk.getId()));
        }
        return items;
    }


    private void addQuestionsBasedOn(List<ChunkEntity> chunks, long questionSetId, long questionPackId){

        QuestionSetEntity questionSetEntity = questionSetDBManager.findQuestionSetById(questionSetId);
        for(ChunkEntity entity: chunks){
            Question question = new Question();

            question.setQuestionText("");
            question.setAnswerPoolName("");

            dbWriter.addQuestion(question, questionPackId);
        }



    }


    private long createQuestionPackIfDoesntExist(String questionPackName){
        long questionPackId = dbWriter.getQuestionPackIdForName(questionPackName);
        if(questionPackId == -1){
            QuestionPackDbEntity qpEntity= new QuestionPackDbEntity();
            qpEntity.setDateCreated(DateProvider.getNow());
            qpEntity.setAuthor("User");
            qpEntity.setName(questionPackName);



            questionPackId = dbWriter.saveQuestionPackRecord(qpEntity);
        }
        return questionPackId;
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
