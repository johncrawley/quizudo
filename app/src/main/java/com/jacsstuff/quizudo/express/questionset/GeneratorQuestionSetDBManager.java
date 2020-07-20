package com.jacsstuff.quizudo.express.questionset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.DbContract;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.ALL;
import static com.jacsstuff.quizudo.db.DbConsts.DELETE;
import static com.jacsstuff.quizudo.db.DbConsts.DELETE_FROM;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.SET;
import static com.jacsstuff.quizudo.db.DbConsts.UPDATE;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;


public class GeneratorQuestionSetDBManager {

    private DBHelper mDbHelper;
    private SQLiteDatabase db;
    private ChunkParser chunkParser;


    GeneratorQuestionSetDBManager(Context context){
        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        chunkParser = new ChunkParser(context);
    }


    void remove(SimpleListItem item){
        long id = item.getId();
        String query = DELETE_FROM + DbContract.QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + DbContract.QuestionGeneratorChunkEntry._ID +
                EQUALS + id;
        executeStatment(query);

    }

    void updateQuestionTemplate(long questionSetId, String text){
        String query = UPDATE + DbContract.QuestionGeneratorSetEntry.TABLE_NAME +
                SET + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE +
                EQUALS +  inQuotes(text) +
                WHERE + DbContract.QuestionGeneratorSetEntry._ID + EQUALS + questionSetId + ";";
        executeStatment(query);
    }

    String getQuestionTemplateText(long questionSetId){
        String query = SELECT + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE +
                FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME +
                WHERE + DbContract.QuestionGeneratorSetEntry._ID +
                EQUALS + questionSetId + ";";
        return retrieveQuestionTemplate(query);
    }


    private String retrieveQuestionTemplate(String query){
        Cursor cursor = db.rawQuery(query, null);
        String questionTemplate = "";
        List<SimpleListItem> list = new ArrayList<>();
        while(cursor.moveToNext()){
            questionTemplate = getString(cursor, DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE);
        }
        cursor.close();
        return questionTemplate;
    }


    long addChunk(long questionSetId, String text){
        Log.i("quizz", "genQSDbMan addChunk("  + questionSetId + ", " + text + ")");
        ChunkEntity chunkEntity = chunkParser.parse(text);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID, questionSetId);
        contentValues.put(DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_ANSWER, chunkEntity.getAnswer());
        contentValues.put(DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT, chunkEntity.getQuestionSubject());
        return addValuesToTable(DbContract.QuestionGeneratorChunkEntry.TABLE_NAME, contentValues);
    }

    List<SimpleListItem> retrieveChunksFor(long id){

        Log.i("quizz", "genQSDbMan retrieveChunks("  + id + ")");
        String query = SELECT + ALL + FROM + DbContract.QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID +
                EQUALS + id + ";";
        return retrieveListFromDb(query);
    }


    private List<SimpleListItem> retrieveListFromDb(String query){
        Cursor cursor = db.rawQuery(query, null);
        List<SimpleListItem> list = new ArrayList<>();
        Log.i("quizz", " retrieveListFromDb() cursor size : " + cursor.getCount());
        while(cursor.moveToNext()){
            addToList(cursor, list);
        }
        cursor.close();
        return list;
    }


    private void addToList(Cursor cursor, List<SimpleListItem> list){
        String answer = getString(cursor, DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_ANSWER);
        String subject = getString(cursor, DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT);
        String trivia = getString(cursor, DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_TRIVIA);
        String questionSetId = getString(cursor, DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID);

        long id = getLong(cursor, DbContract.QuestionGeneratorChunkEntry._ID);
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
