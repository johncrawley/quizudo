package com.jacsstuff.quizudo.creator.express;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.DbContract;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.AND;
import static com.jacsstuff.quizudo.db.DbConsts.DELETE;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.IN;
import static com.jacsstuff.quizudo.db.DbConsts.INNER_JOIN;
import static com.jacsstuff.quizudo.db.DbConsts.ON;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;

public class QuestionGeneratorDbManager {


    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    QuestionGeneratorDbManager(Context context){

        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
    }


    List<String> retrieveGeneratorNames(){

        List<String> generatorNames = new ArrayList<>();
        String query = SELECT + "*" + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
          addNameToList(cursor, generatorNames);
        }
        cursor.close();
        return generatorNames;
    }


    private void addNameToList(Cursor cursor, List<String> list){
        String name = getString(cursor, DbContract.QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME);
        if(name != null && !name.isEmpty()){
            list.add(name);
        }
    }


    void addQuestionGenerator(String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME, name);
        addValuesToTable(DbContract.QuestionGeneratorEntry.TABLE_NAME, contentValues);
    }


    private void addValuesToTable(String tableName, ContentValues contentValues){
        db.beginTransaction();
        try {
            db.insertOrThrow(tableName, null, contentValues);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
        }
        db.endTransaction();
    }


    void closeConnection(){
        mDbHelper.close();
    }

    private String quotes(String value){
        return "'" + value + "'";
    }

    boolean removeQuestionGenerator(String name){


        String query = DELETE + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME
                + EQUALS + "'" + name + "';";

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
        return  true;
    }

    public boolean removeAnswer(String answerPoolName, String answerValue){

        db.beginTransaction();

        String query = DELETE + FROM + DbContract.AnswerPoolItemsEntry.TABLE_NAME
                + WHERE + DbContract.AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID + IN +
                "(" +
                SELECT + DbContract.AnswerPoolNamesEntry._ID +
                FROM + DbContract.AnswerPoolNamesEntry.TABLE_NAME +
                WHERE + DbContract.AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME +
                EQUALS + "'" + answerPoolName + "'" + ")" +
                AND + DbContract.AnswerPoolItemsEntry.COLUMN_NAME_ANSWER +
                EQUALS + quotes(answerValue) + ";";
        try {
            db.execSQL(query);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
            db.endTransaction();
            return false;
        }
        db.endTransaction();
        return  true;
    }


    public List<String> getAnswerItems(String answerPoolName){

        List<String> answerItems = new ArrayList<>();

        Cursor cursor;

        String query =
                SELECT + DbContract.AnswerPoolItemsEntry.COLUMN_NAME_ANSWER
                        + FROM + DbContract.AnswerPoolItemsEntry.TABLE_NAME
                        + INNER_JOIN + DbContract.AnswerPoolNamesEntry.TABLE_NAME
                        + ON + DbContract.AnswerPoolItemsEntry.TABLE_NAME + "." + DbContract.AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID
                        + EQUALS + DbContract.AnswerPoolNamesEntry.TABLE_NAME + "." + DbContract.AnswerPoolNamesEntry._ID
                        + WHERE + DbContract.AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME
                        + EQUALS + "'" + answerPoolName + "';";

        try {
            cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                answerItems.add(getString(cursor, DbContract.AnswerPoolItemsEntry.COLUMN_NAME_ANSWER));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return answerItems;
        }
        cursor.close();
        return  answerItems;
    }

/*
    private String getAnswersItemsByIdQuery(long id){
        return SELECT + DISTINCT + AnswerPoolItemsEntry.COLUMN_NAME_ANSWER
                +  FROM + AnswerPoolItemsEntry.TABLE_NAME
                + WHERE + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID
                + EQUALS + "'" + id + "';";
    }
*/

    private String getString(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }

    private int getInt(Cursor cursor, String name){
        return cursor.getInt(cursor.getColumnIndexOrThrow(name));
    }
}
