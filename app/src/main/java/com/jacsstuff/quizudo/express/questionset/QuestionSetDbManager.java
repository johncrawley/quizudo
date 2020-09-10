package com.jacsstuff.quizudo.express.questionset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorSetEntry;
import com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorChunkEntry;
import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.Utils;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.AND;
import static com.jacsstuff.quizudo.db.DbConsts.DELETE_FROM;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.LIMIT_1;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT_ALL_FROM;
import static com.jacsstuff.quizudo.db.DbConsts.SET;
import static com.jacsstuff.quizudo.db.DbConsts.UPDATE;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;


public class QuestionSetDbManager {

    private SQLiteDatabase db;
    private ChunkParser chunkParser;


    public QuestionSetDbManager(Context context){
        DBHelper mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        chunkParser = new ChunkParser(context);
    }


    void remove(SimpleListItem item){
        long id = item.getId();
        String query = DELETE_FROM + QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + QuestionGeneratorChunkEntry._ID +
                EQUALS + id;
        Utils.executeStatement(db, query);

    }

    void updateQuestionTemplate(long questionSetId, String text){
        String query = UPDATE + QuestionGeneratorSetEntry.TABLE_NAME +
                SET + QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE +
                EQUALS +  Utils.inQuotes(text) +
                WHERE + QuestionGeneratorSetEntry._ID + EQUALS + questionSetId + ";";
        Utils.executeStatement(db, query);
    }

    String getQuestionTemplateText(long questionSetId){
        String query = SELECT + QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE +
                FROM + QuestionGeneratorSetEntry.TABLE_NAME +
                WHERE + QuestionGeneratorSetEntry._ID +
                EQUALS + questionSetId + ";";
        return retrieveQuestionTemplate(query);
    }


    private String retrieveQuestionTemplate(String query){
        Cursor cursor = db.rawQuery(query, null);
        String questionTemplate = "";
        while(cursor.moveToNext()){
            questionTemplate = getString(cursor, QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE);
        }
        cursor.close();
        return questionTemplate;
    }


    long addChunk(long questionSetId, String text){
        ChunkEntity chunkEntity = chunkParser.parse(text);
       return addChunk(questionSetId, chunkEntity);
    }


    public long addChunk(long questionSetId, ChunkEntity chunkEntity){
        deleteSimilarTo(chunkEntity, questionSetId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID, questionSetId);
        contentValues.put(QuestionGeneratorChunkEntry.COLUMN_NAME_ANSWER, chunkEntity.getAnswer());
        contentValues.put(QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT, chunkEntity.getQuestionSubject());
        return Utils.addValuesToTable(db, QuestionGeneratorChunkEntry.TABLE_NAME, contentValues);
    }



    private void deleteSimilarTo(ChunkEntity chunkEntity, long questionSetId){

        String query = DELETE_FROM + QuestionGeneratorChunkEntry.TABLE_NAME
                + WHERE + QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID + EQUALS + questionSetId
                + AND + QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT + EQUALS + Utils.inQuotes(chunkEntity.getQuestionSubject()) + ";";

        Utils.executeStatement(db, query);
    }






    public QuestionSetEntity findQuestionSetById(long id){
        String query = SELECT_ALL_FROM + QuestionGeneratorSetEntry.TABLE_NAME +
                WHERE + QuestionGeneratorSetEntry._ID + EQUALS + id + LIMIT_1;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        String questionTemplate = getString(cursor, QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE);
        String name = getString(cursor, QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME);
        cursor.close();
        return new QuestionSetEntity(name, questionTemplate);
    }


    public List<SimpleListItem> retrieveChunkListItemsFor(long id){
        String query = SELECT_ALL_FROM + QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID +
                EQUALS + id + ";";
        return retrieveListFromDb(query);
    }


    public List<ChunkEntity> retrieveChunksFor(long questionSetId){
        String query = SELECT_ALL_FROM + QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID +
                EQUALS + questionSetId + ";";

        Cursor cursor = db.rawQuery(query, null);
        List<ChunkEntity> list = new ArrayList<>();
        while(cursor.moveToNext()){
            String answer = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_ANSWER);
            String subject = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT);
            //String trivia = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_TRIVIA);

            long id = getLong(cursor, QuestionGeneratorChunkEntry._ID);
            ChunkEntity chunkEntity = new ChunkEntity(subject,answer, id);
            list.add(chunkEntity);
        }
        cursor.close();
        return list;
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
        String answer = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_ANSWER);
        String subject = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT);
        //String trivia = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_TRIVIA);
        //String questionSetId = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID);

        long id = getLong(cursor, QuestionGeneratorChunkEntry._ID);
        String name = chunkParser.getString(subject, answer);
        list.add(new SimpleListItem(name, id));
    }


    private String getString(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }


    private long getLong(Cursor cursor, String name){
        return cursor.getLong(cursor.getColumnIndexOrThrow(name));
    }

}
