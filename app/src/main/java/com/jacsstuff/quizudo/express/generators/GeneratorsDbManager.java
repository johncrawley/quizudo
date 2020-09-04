package com.jacsstuff.quizudo.express.generators;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jacsstuff.quizudo.db.DBHelper;
import com.jacsstuff.quizudo.db.DbContract;
import com.jacsstuff.quizudo.express.generatorsdetail.QuestionGeneratorDbManager;
import com.jacsstuff.quizudo.express.questionset.ChunkEntity;
import com.jacsstuff.quizudo.express.questionset.QuestionSetDbManager;
import com.jacsstuff.quizudo.express.questionset.QuestionSetEntity;
import com.jacsstuff.quizudo.list.SimpleListItem;

import java.util.ArrayList;
import java.util.List;

import static com.jacsstuff.quizudo.db.DbConsts.DELETE_FROM;
import static com.jacsstuff.quizudo.db.DbConsts.EQUALS;
import static com.jacsstuff.quizudo.db.DbConsts.FROM;
import static com.jacsstuff.quizudo.db.DbConsts.SELECT;
import static com.jacsstuff.quizudo.db.DbConsts.WHERE;

public class GeneratorsDbManager {

    private DBHelper mDbHelper;
    private SQLiteDatabase db;
    private QuestionGeneratorDbManager questionGeneratorDbManager;
    private QuestionSetDbManager questionSetDbManager;

    GeneratorsDbManager(Context context){
        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
        questionGeneratorDbManager = new QuestionGeneratorDbManager(context);
        questionSetDbManager = new QuestionSetDbManager(context);
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
        String query = DELETE_FROM + DbContract.QuestionGeneratorEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorEntry._ID
                + EQUALS + item.getId() + ";";

        if(executeStatement(query)){
            deleteAllQuestionSetsBelongingTo(item.getId());
       }
    }


    private void deleteAllQuestionSetsBelongingTo(long generatorId){
        deleteAllChunksBelongingToQuestionSetsOf(generatorId);

        String query = DELETE_FROM + DbContract.QuestionGeneratorSetEntry.TABLE_NAME
                + WHERE + DbContract.QuestionGeneratorSetEntry.COLUMN_NAME_GENERATOR_ID
                + EQUALS + generatorId;
        executeStatement(query);

    }

    private void deleteAllChunksBelongingToQuestionSetsOf(long generatorId){

        String q3 = "DELETE FROM q_generator_chunks WHERE _id IN " +
                    " ( SELECT a._id from q_generator_chunks a INNER JOIN  question_generator_sets b ON " +
                " a.question_set_id = b._id " +
                " WHERE b.generator_id = "  + generatorId + ");";

       executeStatement(q3);
    }



    // db.rawQuery(MY_QUERY, new String[]{String.valueOf(generatorId)});

    //String q4 = "SELECT * from q_generator_chunks INNER JOIN question_generator_sets ON " +
    //"q_generator_chunks.question_set_id = question_generator_sets._id;";// WHERE b.generator_id = "  + generatorId + ";";
    //Log.i("quizz", " query4: " + q4);



    private List<SimpleListItem> retrieve2(String query, String... params){
        Cursor cursor = db.rawQuery(query, params);
        List<SimpleListItem> list = new ArrayList<>();
        while(cursor.moveToNext()){
            addToList2(cursor, list);
        }
        cursor.close();
        return list;
    }


    private void addToList2(Cursor cursor, List<SimpleListItem> list){
        /*
        String entry = " chunk_id "  + getLong(cursor, "chunk_id") +
                " subject: " + getString(cursor, "q_subject") +
                " q_set_id " + getString(cursor, "q_set_id") +
                " gen_id : " + getString(cursor, "gen_id");

         */
        String entry = getString(cursor, "_id");
        long id = getLong(cursor, "_id");
        Log.i("quizz", "addToList2() entry: "+  entry);
        list.add(new SimpleListItem(entry, id));
    }



    private List<SimpleListItem> retrieve3(String query){
        Cursor cursor = db.rawQuery(query, null);
        List<SimpleListItem> list = new ArrayList<>();
        while(cursor.moveToNext()){
            addToList3(cursor, list);
        }
        cursor.close();
        return list;
    }


    private void addToList3(Cursor cursor, List<SimpleListItem> list){
        String name = "subject: " + getString(cursor, DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_SUBJECT)
        + "qSetId: " + getString(cursor, DbContract.QuestionGeneratorChunkEntry.COLUMN_NAME_QUESTION_SET_ID);
        long id = getLong(cursor, DbContract.QuestionGeneratorChunkEntry._ID);
        Log.i("quizz", "addToList3() item: "+  name);
        list.add(new SimpleListItem(name, id));
    }





    private boolean executeStatement(String query){
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
