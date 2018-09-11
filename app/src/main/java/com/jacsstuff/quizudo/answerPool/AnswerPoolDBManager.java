package com.jacsstuff.quizudo.answerPool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.*;
import static com.jacsstuff.quizudo.db.DbContract.AnswerPoolNamesEntry;
import static com.jacsstuff.quizudo.db.DbContract.AnswerPoolItemsEntry;

public class AnswerPoolDBManager {


    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    public AnswerPoolDBManager(Context context){

        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
    }

    public List<String> getAnswerPoolNames(){

        List<String> answerPoolNames = new ArrayList<>();

        String query =  SELECT + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME
                + FROM + AnswerPoolNamesEntry.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            String answerPoolName = getString(cursor, AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME);
            if(answerPoolName != null && !answerPoolName.isEmpty()){
                answerPoolNames.add(answerPoolName);
            }
        }
        cursor.close();
        return answerPoolNames;
    }


    public List<String> getAnswerPoolItems(String answerPoolName){

        List<String> answerPoolItems = new ArrayList<>();

        String query =    SELECT        + AnswerPoolItemsEntry.COLUMN_NAME_ANSWER
                + FROM          + AnswerPoolItemsEntry.TABLE_NAME
                + INNER_JOIN    + AnswerPoolNamesEntry.TABLE_NAME
                + ON            + AnswerPoolNamesEntry.TABLE_NAME + "." + AnswerPoolNamesEntry._ID
                + EQUALS        + AnswerPoolItemsEntry.TABLE_NAME + "." + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID
                + WHERE         + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME + EQUALS + quotes(answerPoolName);

        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            String answer = getString(cursor, AnswerPoolItemsEntry.COLUMN_NAME_ANSWER);
            if(answerPoolName != null && !answerPoolName.isEmpty()){
                answerPoolItems.add(answer);
            }
        }
        cursor.close();
        return answerPoolItems;
    }


    public void addAnswerPool(String name){

        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME, name);
        addValuesToTable(AnswerPoolNamesEntry.TABLE_NAME, contentValues);
    }

    /*

    public void addAnswerPool(String name){

        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME, name);

        try {
            db.insertOrThrow(AnswerPoolNamesEntry.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
        }

        db.endTransaction();
    }



     */

    public void addAnswerPoolItem(String answerPoolName, String answerItem){

        long answerPoolNameId = -1L;
        String idQuery = SELECT + AnswerPoolNamesEntry._ID
                        + FROM + AnswerPoolNamesEntry.TABLE_NAME
                        + WHERE + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME
                        + EQUALS + quotes(answerPoolName) + ";";

        Cursor cursor = db.rawQuery(idQuery, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            answerPoolNameId = getInt(cursor, AnswerPoolNamesEntry._ID);
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerPoolItemsEntry.COLUMN_NAME_ANSWER, answerItem);
        contentValues.put(AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID, answerPoolNameId);

        addValuesToTable(AnswerPoolItemsEntry.TABLE_NAME, contentValues);
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


    public void closeConnection(){
        mDbHelper.close();
    }

    private String quotes(String value){
        return "'" + value + "'";
    }

    public boolean removeAnswerPool(String answerPoolName){


        String deleteAnswerPoolQuery = DELETE + FROM + AnswerPoolNamesEntry.TABLE_NAME
                + WHERE + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME
                + EQUALS + "'" + answerPoolName + "';";

        String deleteAllAssociatedAnswersQuery = DELETE + FROM + AnswerPoolItemsEntry.TABLE_NAME
                + WHERE + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID + IN +
                "(" +
                        SELECT + AnswerPoolNamesEntry._ID +
                        FROM + AnswerPoolNamesEntry.TABLE_NAME +
                        WHERE + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME +
                        EQUALS + quotes(answerPoolName) + ")";

        db.beginTransaction();
        try {
            db.execSQL(deleteAllAssociatedAnswersQuery);
            db.execSQL(deleteAnswerPoolQuery);
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

        String query = DELETE + FROM + AnswerPoolItemsEntry.TABLE_NAME
                     + WHERE + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID + IN +
                        "(" +
                            SELECT + AnswerPoolNamesEntry._ID +
                            FROM + AnswerPoolNamesEntry.TABLE_NAME +
                            WHERE + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME +
                            EQUALS + "'" + answerPoolName + "'" + ")" +
                            AND + AnswerPoolItemsEntry.COLUMN_NAME_ANSWER +
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
                    SELECT + AnswerPoolItemsEntry.COLUMN_NAME_ANSWER
                + FROM + AnswerPoolItemsEntry.TABLE_NAME
                + INNER_JOIN + AnswerPoolNamesEntry.TABLE_NAME
                + ON + AnswerPoolItemsEntry.TABLE_NAME + "." + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID
                + EQUALS + AnswerPoolNamesEntry.TABLE_NAME + "." + AnswerPoolNamesEntry._ID
                + WHERE + AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME
                + EQUALS + "'" + answerPoolName + "';";

        try {
            cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
               answerItems.add(getString(cursor, AnswerPoolItemsEntry.COLUMN_NAME_ANSWER));
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
