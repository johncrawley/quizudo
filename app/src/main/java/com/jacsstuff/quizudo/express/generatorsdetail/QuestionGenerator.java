package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;
import android.util.Log;

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
    private ChunkParser chunkParser;
    private QuestionTextMaker questionTextMaker;

    public QuestionGenerator(Context context){
        this.context = context;
        questionSetDBManager = new GeneratorQuestionSetDBManager(context);
        answerPoolDBManager = new AnswerPoolDBManager(context);
        questionPackDBManager = new QuestionPackDBManager(context);
        dbWriter = new DBWriter(context);
        chunkParser = new ChunkParser(context);
        questionTextMaker = new QuestionTextMaker(context);
    }


    public void addQuestionsTo(String questionPackName, SimpleListItem generator, List<SimpleListItem> questionSets){

        generatorName = generator.getName();
        generatorId = generator.getId();
        long questionPackId = createQuestionPackIfDoesntExist(questionPackName);
        Map<String, Long> answerPoolMap = getMapOfExistingAnswerPools();

        for(SimpleListItem questionSet : questionSets){
            long questionSetId = questionSet.getId();

            List<ChunkEntity> chunks = questionSetDBManager.retrieveChunksFor(questionSetId);
            List<String> answers = getAnswersFrom(chunks);
            String answerPoolName = AnswerPoolNameResolver.getName(generatorName, questionSet.getName());

            addAnswersToAnswerPool(answers, answerPoolName , answerPoolMap);
            addQuestionsBasedOn(chunks, questionSetId, questionPackId, answerPoolName);
        }

    }


    private Map<String, Long> getMapOfExistingAnswerPools(){
        Map <String, Long> answerPoolMap = new HashMap<>();
        existingAnswerPools = answerPoolDBManager.getAnswerPools();

        for(SimpleListItem item: existingAnswerPools){
            answerPoolMap.put(item.getName(), item.getId());
        }
        return answerPoolMap;
    }




    private List<String> getAnswersFrom(List<ChunkEntity> chunks){
        List<String> answers = new ArrayList<>(chunks.size());
        for(ChunkEntity chunkEntity : chunks){
            answers.add(chunkEntity.getAnswer());
        }
        return answers;
    }

    private List<SimpleListItem> getListItemsFromChunks(List<ChunkEntity> chunks){
        List<SimpleListItem> items = new ArrayList<>(chunks.size());
        for(ChunkEntity chunk: chunks){
            String displayStr = chunkParser.getString(chunk);
            items.add(new SimpleListItem(displayStr, chunk.getId()));
        }
        return items;
    }


    private void addQuestionsBasedOn(List<ChunkEntity> chunks, long questionSetId, long questionPackId, String answerPoolName){
        QuestionSetEntity questionSetEntity = questionSetDBManager.findQuestionSetById(questionSetId);
        if(questionSetEntity == null){
            return;
        }
        for(ChunkEntity chunkEntity: chunks){
            Question question = new Question();
            setQuestionText(question, questionSetEntity, chunkEntity);
            question.setAnswerPoolName(answerPoolName);
            question.setCorrectAnswer(chunkEntity.getAnswer());
            if(!isValid(question)){
                continue;
            }
            dbWriter.addOrOverwriteQuestion(question, questionPackId);
        }
    }


    private void setQuestionText(Question question, QuestionSetEntity questionSetEntity, ChunkEntity chunkEntity){
        String questionText = questionTextMaker.getQuestionText(questionSetEntity.getQuestionTemplate(), chunkEntity.getQuestionSubject());
        question.setQuestionText(questionText);
        Log.i("QuestionGenerator", "setQuestionText() " + questionText);
    }



    private boolean isValid(Question q){
        if(isNullOrEmpty(q.getAnswerPoolName())) return false;
        if(isNullOrEmpty(q.getCorrectAnswer())) return false;
        return !isNullOrEmpty(q.getQuestionText());
    }



    private boolean isNullOrEmpty(String str){
        return str == null || str.isEmpty();
    }

    private long createQuestionPackIfDoesntExist(String questionPackName){
        long questionPackId = dbWriter.getQuestionPackIdForName(questionPackName);
        if(questionPackId == -1){
            QuestionPackDbEntity qpEntity= new QuestionPackDbEntity();
            qpEntity.setDateCreated(DateProvider.getNow());
            qpEntity.setNameAndAuthor(questionPackName, "User");
            Log.i("QGenerator", "createQPIfDoesntExist() qpDbEntry: " + qpEntity.toString());
            questionPackId = dbWriter.saveQuestionPackRecord(qpEntity);
        }
        return questionPackId;
    }


    private void addAnswersToAnswerPool(List<String> answers, String answerPoolName, Map<String, Long> answerPoolMap){
        long answerPoolId = createAnswerPoolIfDoesntExist(answerPoolName, answerPoolMap);
        answerPoolDBManager.addAnswerPoolItems(answerPoolId, answers);
    }


    private long createAnswerPoolIfDoesntExist(String resolvedName, Map<String, Long> answerPoolMap){
        if(!answerPoolMap.containsKey(resolvedName)) {
            return answerPoolDBManager.addAnswerPool(resolvedName);
        }
        return answerPoolMap.get(resolvedName);
    }


}
