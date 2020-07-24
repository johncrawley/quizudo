package com.jacsstuff.quizudo.answerPool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.list.SimpleListItem;

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


    public List<SimpleListItem> getAnswerPools(){
        List<SimpleListItem> answerPoolNames = new ArrayList<>();
        String query =  SELECT + "*" + FROM + AnswerPoolNamesEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
            String itemName = getString(cursor, AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME);
            long id = getLong(cursor, AnswerPoolNamesEntry._ID);
            if(itemName != null && !itemName.isEmpty()){
                answerPoolNames.add(new SimpleListItem(itemName, id));
            }
        }
        cursor.close();
        return answerPoolNames;
    }


    public long addAnswerPool(String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME, name);
        return addValuesToTable(AnswerPoolNamesEntry.TABLE_NAME, contentValues);
    }

    public List<String> getAnswerPools(String answerPoolName){
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


    public List<SimpleListItem> getAnswerItems(long answerPoolId){
        List<SimpleListItem> answerItems = new ArrayList<>();
        Cursor cursor;
        String query = SELECT + ALL
                + FROM + AnswerPoolItemsEntry.TABLE_NAME
                + WHERE + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID
                + EQUALS + answerPoolId + ";";

        try {
            cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                String text = getString(cursor, AnswerPoolItemsEntry.COLUMN_NAME_ANSWER);
                long id = getLong(cursor, AnswerPoolItemsEntry._ID);
                answerItems.add(new SimpleListItem(text, id));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return answerItems;
        }
        cursor.close();
        return  answerItems;
    }


    public long addAnswerPoolItem(long answerPoolId, String answerItem){
        ContentValues contentValues = createContentValuesFor(answerPoolId, answerItem);
        return addValuesToTable(AnswerPoolItemsEntry.TABLE_NAME, contentValues);
    }


    public void addAnswerPoolItems(long answerPoolId, List<String> answerItems){
        for(String item : answerItems) {
            if(item.trim().isEmpty()){
                continue;
            }
            addValuesToTable(AnswerPoolItemsEntry.TABLE_NAME, createContentValuesFor(answerPoolId, item));
        }
    }


    private ContentValues createContentValuesFor(long answerPoolId, String answerItem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnswerPoolItemsEntry.COLUMN_NAME_ANSWER, answerItem);
        contentValues.put(AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID, answerPoolId);
        String uVal = answerItem + "_" + String.valueOf(answerPoolId);
        contentValues.put(AnswerPoolItemsEntry.COLUMN_NAME_U_VAL, uVal);
        return contentValues;
    }


    public void closeConnection(){
        mDbHelper.close();
    }


    private String quotes(String value){
        return "'" + value + "'";
    }

    private String quotes(long value){
        return "'" + value + "'";
    }

    public boolean removeAnswerPool(SimpleListItem answerPoolItem){

        String deleteAnswerPoolQuery = DELETE + FROM + AnswerPoolNamesEntry.TABLE_NAME
                + WHERE + AnswerPoolNamesEntry._ID
                + EQUALS + quotes(answerPoolItem.getId())+ ";";

        String deleteAllAssociatedAnswersQuery = DELETE + FROM + AnswerPoolItemsEntry.TABLE_NAME
                + WHERE + AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID +
                EQUALS +  quotes(answerPoolItem.getId())+ ";";

        execute(deleteAnswerPoolQuery, deleteAllAssociatedAnswersQuery);
        return  true;
    }

    public boolean removeAnswer(SimpleListItem answer){

        String query = DELETE + FROM + AnswerPoolItemsEntry.TABLE_NAME
                + WHERE + AnswerPoolItemsEntry._ID + EQUALS + quotes(answer.getId()) + ";";
        return execute(query);
    }


    private boolean execute(String ...queries){
        db.beginTransaction();
        try {
            for(String query: queries){
                db.execSQL(query);
            }
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
            db.endTransaction();
            return false;
        }
        db.endTransaction();
        return  true;
    }

    private long addValuesToTable(String tableName, ContentValues contentValues){
        long id = -1;
        db.beginTransaction();
        try {
            id = db.insertOrThrow(tableName, null, contentValues);
            db.setTransactionSuccessful();
        }catch(SQLException e){
            e.printStackTrace();
        }
        db.endTransaction();
        return id;
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
