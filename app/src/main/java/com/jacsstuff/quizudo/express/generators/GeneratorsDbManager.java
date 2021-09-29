package com.jacsstuff.quizudo.express.generators;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.Utils;
import com.jacsstuff.quizudo.express.generatorsdetail.QuestionGeneratorDbManager;
import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetDbManager;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorEntry;
import static com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorSetEntry;

import static com.jacsstuff.quizudo.db.DbConsts.DELETE_FROM;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;

public class GeneratorsDbManager {

    private final DBHelper mDbHelper;
    private final SQLiteDatabase db;
    private final QuestionGeneratorDbManager questionGeneratorDbManager;
    private final QuestionSetDbManager questionSetDbManager;

    GeneratorsDbManager(Context context){
        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        questionGeneratorDbManager = new QuestionGeneratorDbManager(context);
        questionSetDbManager = new QuestionSetDbManager(context);
    }


    List<SimpleListItem> retrieveGeneratorNames(){
        String query = SELECT + "*" + FROM + QuestionGeneratorEntry.TABLE_NAME;
        return retrieveListFromDb(query);
    }


    private List<SimpleListItem> retrieveListFromDb(String query){
        Cursor cursor = db.rawQuery(query, null);
        List<SimpleListItem> list = new ArrayList<>();
        while(cursor.moveToNext()){
            addToList(cursor, list);
        }
        cursor.close();
        return list;
    }


    private void addToList(Cursor cursor, List<SimpleListItem> list){
        String name = getString(cursor, QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME);
        long id = getLong(cursor, QuestionGeneratorEntry._ID);
        if(name != null && !name.isEmpty()){
            list.add(new SimpleListItem(name, id));
        }
    }


    long addQuestionGenerator(String name){
        long existingGeneratorId = findExistingGeneratorIdFor(name);
        if(existingGeneratorId != -1){
            return existingGeneratorId;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME, name);
        return Utils.addValuesToTable(db, QuestionGeneratorEntry.TABLE_NAME, contentValues);
    }


    private long findExistingGeneratorIdFor(String name){
        long generatorId = -1;
        String query = SELECT + QuestionGeneratorEntry._ID + FROM + QuestionGeneratorEntry.TABLE_NAME
                + WHERE + QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME
                + EQUALS + Utils.inQuotes(name) + ";";

        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.getCount() >= 1) {
                cursor.moveToNext();
                generatorId = getLong(cursor, QuestionGeneratorEntry._ID);
            }
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        cursor.close();
        return generatorId;
    }


    void save(GeneratorEntity generatorEntity){
        long generatorId = addQuestionGenerator(generatorEntity.getGeneratorName());
        for(QuestionSetEntity questionSetEntity : generatorEntity.getQuestionSetEntities()){
           saveQuestionSetAndChunks(questionSetEntity, generatorId);
        }
    }


    private void saveQuestionSetAndChunks(QuestionSetEntity questionSetEntity, long generatorId){
        long questionSetId = questionGeneratorDbManager.addQuestionSet(generatorId, questionSetEntity.getName(), questionSetEntity.getQuestionTemplate());
        saveChunks(questionSetEntity, questionSetId);
    }


    private void saveChunks(QuestionSetEntity questionSetEntity, long questionSetId){
        for(ChunkEntity chunkEntity : questionSetEntity.getChunkEntities()){
            questionSetDbManager.addChunk(questionSetId, chunkEntity);
        }
    }


    void closeConnection(){
        mDbHelper.close();
    }


    void removeQuestionGenerator(SimpleListItem item){
        String query = DELETE_FROM + QuestionGeneratorEntry.TABLE_NAME
                + WHERE + QuestionGeneratorEntry._ID
                + EQUALS + item.getId() + ";";

        if(Utils.executeStatement(db, query)){
            deleteAllQuestionSetsBelongingTo(item.getId());
       }
    }


    private void deleteAllQuestionSetsBelongingTo(long generatorId){
        deleteAllChunksBelongingToQuestionSetsOf(generatorId);

        String query = DELETE_FROM + QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID
                + EQUALS + generatorId;
        Utils.executeStatement(db, query);
    }

    private void deleteAllChunksBelongingToQuestionSetsOf(long generatorId){

        String q3 = "DELETE FROM q_generator_chunks WHERE _id IN " +
                    " ( SELECT a._id from q_generator_chunks a INNER JOIN  question_generator_sets b ON " +
                " a.question_set_id = b._id " +
                " WHERE b.generator_id = "  + generatorId + ");";

       Utils.executeStatement(db, q3);
    }


    private String getString(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }


    private long getLong(Cursor cursor, String name){
        return cursor.getLong(cursor.getColumnIndexOrThrow(name));
    }
}
