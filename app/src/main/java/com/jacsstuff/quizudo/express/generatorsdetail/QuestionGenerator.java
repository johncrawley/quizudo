package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.Context;
import android.util.Log;

import com.jacsstuff.quizudo.R;
import com.jacsstuff.quizudo.answerPool.AnswerPoolDBManager;
import com.jacsstuff.quizudo.db.DBWriter;
import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetDbManager;
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

    private QuestionSetDbManager questionSetDBManager;
    private AnswerPoolDBManager answerPoolDBManager;
    private DBWriter dbWriter;
    private QuestionTextMaker questionTextMaker;

    public QuestionGenerator(Context context){
        questionSetDBManager = new QuestionSetDbManager(context);
        answerPoolDBManager = new AnswerPoolDBManager(context);
        dbWriter = new DBWriter(context);
        questionTextMaker = new QuestionTextMaker(context.getString(R.string.question_generator_question_subject_placeholder));
    }


    public void addQuestionsTo(String questionPackName, String generatorName, List<SimpleListItem> questionSets){
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
        for(SimpleListItem item:  answerPoolDBManager.getAnswerPools()){
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
