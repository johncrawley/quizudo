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

        String query = SELECT + "*" + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME;
        return retrieveListFromDb(query);
    }


    List<String> retrieveQuestionSetNames(String generatorName){
        String query = SELECT + "*" + FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_NAME + EQUALS + inQuotes(generatorName);

        return retrieveListFromDb(query);
    }


    private List<String> retrieveListFromDb(String query){
        Cursor cursor = db.rawQuery(query, null);
        List<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            addNameToList(cursor, list);
        }
        cursor.close();
        return list;
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

    void addQuestionSet(String generatorName, String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME, name);
        contentValues.put(DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_NAME, generatorName);
        addValuesToTable(DbContract.QuestionGeneratorSetEntry.TABLE_NAME, contentValues);
    }

    void removeQuestionSet(String name){
        String query = DELETE + FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME
                + EQUALS + inQuotes(name);
        executeStatment(query);
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

    private String inQuotes(String value){
        return "'" + value + "'";
    }


    void removeQuestionGenerator(String name){
        String query = DELETE + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME
                + EQUALS + inQuotes(name);

        if(executeStatment(query)){
            deleteAllQuestionSetsBelongingTo(name);
        };
    }


    private void deleteAllQuestionSetsBelongingTo(String name){
        String query = DELETE + FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_NAME
                + EQUALS + inQuotes(name);
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
}
