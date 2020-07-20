package com.jacsstuff.quizudo.express.generators;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.DbContract;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.DELETE;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;

public class GeneratorsDbManager {

    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    GeneratorsDbManager(Context context){

        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
    }


    List<SimpleListItem> retrieveGeneratorNames(){
        String query = SELECT + "*" + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME;
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
        String name = getString(cursor, DbContract.QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME);
        long id = getLong(cursor, DbContract.QuestionGeneratorEntry._ID);
        if(name != null && !name.isEmpty()){
            list.add(new SimpleListItem(name, id));
        }
    }


    long addQuestionGenerator(String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.QuestionGeneratorEntry.COLUMN_NAME_GENERATOR_NAME, name);
        return addValuesToTable(DbContract.QuestionGeneratorEntry.TABLE_NAME, contentValues);
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


    private String inQuotes(long value){
        return "'" + value + "'";
    }


    void removeQuestionGenerator(SimpleListItem item){
        String query = DELETE + FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorEntry._ID
                + EQUALS + item.getId() + ";";

        if(executeStatment(query)){
            deleteAllQuestionSetsBelongingTo(item);
        }
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
