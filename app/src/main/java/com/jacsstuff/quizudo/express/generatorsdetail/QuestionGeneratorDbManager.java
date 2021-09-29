package com.jacsstuff.quizudo.express.generatorsdetail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.Utils;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorSetEntry;
import static com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorEntry;
import static com.jacsstuff.quizudo.db.DbContract.QuestionGeneratorChunkEntry;

import static com.jacsstuff.quizudo.db.DbConsts.AND;
import static com.jacsstuff.quizudo.db.DbConsts.DELETE_FROM;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.LIMIT_1;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.SET;
import static com.jacsstuff.quizudo.db.DbConsts.UPDATE;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;


public class QuestionGeneratorDbManager {

    private final SQLiteDatabase db;


    public QuestionGeneratorDbManager(Context context){
        DBHelper mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
    }


    List<SimpleListItem> retrieveQuestionSets(long generatorId){
        String query = SELECT + "*" + FROM + QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID + EQUALS + generatorId + ";";
        return retrieveListFromDb(query);
    }


    private List<SimpleListItem> retrieveListFromDb(String query){
        Cursor cursor = db.rawQuery(query, null);
        List<SimpleListItem> list = new ArrayList<>();
        while(cursor.moveToNext()){
            addNameToList(cursor, list);
        }
        cursor.close();
        return list;
    }


    private void addNameToList(Cursor cursor, List<SimpleListItem> list){
        String name = getString(cursor, QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME);
        long id = getLong(cursor, QuestionGeneratorSetEntry._ID);
        if(name != null && !name.isEmpty()){
            list.add(new SimpleListItem(name, id));
        }
    }


    public long addQuestionSet(long generatorId, String name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME, name);
        contentValues.put(QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID, generatorId);
        return addQuestionSet(generatorId, name, "");
    }


    public long addQuestionSet(long generatorId, String name, String questionTemplate){
        long existingQuestionSetId = getQuestionSetId(generatorId, name);
        if(existingQuestionSetId != -1) {
            return existingQuestionSetId;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME, name);
        contentValues.put(QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID, generatorId);
        contentValues.put(QuestionGeneratorSetEntry.COLUMN_NAME_QUESTION_TEMPLATE, questionTemplate);
        return Utils.addValuesToTable(db, QuestionGeneratorSetEntry.TABLE_NAME, contentValues);
    }


    private long getQuestionSetId(long generatorId, String questionSetName){

        long questionSetId = -1;
        String query = SELECT + "*" + FROM + QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID + EQUALS + generatorId
                + AND + QuestionGeneratorSetEntry.COLUMN_NAME_SET_NAME + EQUALS + Utils.inQuotes(questionSetName) + ";";

        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.getCount() >= 1) {
                cursor.moveToNext();
                questionSetId = getLong(cursor, QuestionGeneratorSetEntry._ID);
            }
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        cursor.close();
        return questionSetId;
    }


    String getQuestionSetName(long generatorId){
        String query = SELECT + QuestionGeneratorEntry.COLUMN_NAME_QUESTION_PACK_NAME +
                FROM + QuestionGeneratorEntry.TABLE_NAME +
                WHERE + QuestionGeneratorEntry._ID + EQUALS + generatorId + LIMIT_1 + ";";

        Cursor cursor = db.rawQuery(query, null);
        String questionPackName = "";
        try {
            if (cursor.getCount() == 1) {
                cursor.moveToNext();
                questionPackName = getString(cursor, QuestionGeneratorEntry.COLUMN_NAME_QUESTION_PACK_NAME);
            }
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        cursor.close();
        return questionPackName;
    }


    void updateQuestionPackName(long generatorId, String questionPackName){
        String query = UPDATE + QuestionGeneratorEntry.TABLE_NAME +
                SET + QuestionGeneratorEntry.COLUMN_NAME_QUESTION_PACK_NAME +
                EQUALS + Utils.inQuotes(questionPackName) +
                WHERE + QuestionGeneratorEntry._ID + EQUALS + generatorId + ";";

        Utils.executeStatement(db, query);
    }


    void removeQuestionSet(SimpleListItem item){
        String query = DELETE_FROM + QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + QuestionGeneratorSetEntry._ID
                + EQUALS + item.getId();
        Utils.executeStatement(db, query);

        String removeAllRelatedChunksQuery = DELETE_FROM + QuestionGeneratorChunkEntry.TABLE_NAME +
                WHERE + QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID +
                EQUALS + item.getId();
        Utils.executeStatement(db, removeAllRelatedChunksQuery);
    }


    private String getString(Cursor cursor, String name){
        return cursor.getString(cursor.getColumnIndexOrThrow(name));
    }

    private long getLong(Cursor cursor, String name){
        return cursor.getLong(cursor.getColumnIndexOrThrow(name));
    }
}
