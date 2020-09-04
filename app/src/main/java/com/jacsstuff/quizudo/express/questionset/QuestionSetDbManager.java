package com.jacsstuff.quizudo.express.questionset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorSetEntry;
import com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorChunkEntry;
import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.DbContract;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.DELETE;
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

    private DBHelper mDbHelper;
    private SQLiteDatabase db;
    private ChunkParser chunkParser;


    public QuestionSetDbManager(Context context){
        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        chunkParser = new ChunkParser(context);
    }


    void remove(SimpleListItem item){
        long id = item.getId();
        String query = DELETE_FROM + QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + QuestionGeneratorChunkEntry._ID +
                EQUALS + id;
        executeStatment(query);

    }

    void updateQuestionTemplate(long questionSetId, String text){
        String query = UPDATE + QuestionGeneratorSetEntry.TABLE_NAME +
                SET + QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE +
                EQUALS +  inQuotes(text) +
                WHERE + QuestionGeneratorSetEntry._ID + EQUALS + questionSetId + ";";
        executeStatment(query);
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
        List<SimpleListItem> list = new ArrayList<>();
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID, questionSetId);
        contentValues.put(QuestionGeneratorChunkEntry.COLUMN_NAME_ANSWER, chunkEntity.getAnswer());
        contentValues.put(QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT, chunkEntity.getQuestionSubject());
        return addValuesToTable(QuestionGeneratorChunkEntry.TABLE_NAME, contentValues);
    }


    public QuestionSetEntity findQuestionSetById(long id){
        String query = SELECT_ALL_FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME +
                WHERE + DbContract.QuestionGeneratorSetEntry._ID + EQUALS + id + LIMIT_1;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() == 0){
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        String questionTemplate = getString(cursor, DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE);
        String name = getString(cursor, DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME);
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
            String trivia = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_TRIVIA);

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
        String trivia = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_TRIVIA);
        String questionSetId = getString(cursor, QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID);

        long id = getLong(cursor, QuestionGeneratorChunkEntry._ID);
        String name = chunkParser.getString(subject, answer);
        list.add(new SimpleListItem(name, id));
    }



    private long addValuesToTable(String tableName, ContentValues contentValues){
        db.beginTransaction();
        long id = -1;
        try {
            id = db.insertOrThrow(tableName, null, contentValues);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
        }
        db.endTransaction();
        return id;
    }


    void closeConnection(){
        mDbHelper.close();
    }

    private String inQuotes(String value){
        return "'" + value + "'";
    }
    private String inQuotes(long value){
        return "'" + value + "'";
    }


    void removeQuestionGenerator(SimpleListItem item){
        String query = DELETE + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorEntry._ID
                + EQUALS + inQuotes(item.getId());

        if(executeStatment(query)){
            deleteAllQuestionSetsBelongingTo(item);
        };
    }


    private void deleteAllQuestionSetsBelongingTo(SimpleListItem item){
        String query = DELETE + FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID
                + EQUALS + inQuotes(item.getId());
        executeStatment(query);
    }


    private boolean executeStatment(String query){
        db.beginTransaction();
        try {
            db.execSQL(query);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
            db.endTransaction();
            return false;
        }
        db.endTransaction();
        return true;
    }



    private String getString(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }

    private int getInt(Cursor cursor, String name){
        return cursor.getInt(cursor.getColumnIndexOrThrow(name));
    }
    private long getLong(Cursor cursor, String name){
        return cursor.getLong(cursor.getColumnIndexOrThrow(name));
    }
}
