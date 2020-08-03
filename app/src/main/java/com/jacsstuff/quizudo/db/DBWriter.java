package com.jacsstuff.quizudo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.jacsstuff.quizudo.db.DbContract.QuestionPackEntry;

import com.jacsstuff.quizudo.model.QuestionPackDbEntity;
import com.jacsstuff.quizudo.utils.JSONParser;
import com.jacsstuff.quizudo.model.Question;
import com.jacsstuff.quizudo.model.QuestionPackOverview;
import com.jacsstuff.quizudo.db.DbContract.QuestionsEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static com.jacsstuff.quizudo.db.DbConsts.*;


/**
 * Created by John on 31/12/2016.
 * Used for writing question pack and question data to the DB
 */
public class DBWriter {

    private SQLiteDatabase db;
    private DBHelper mDbHelper;

    public DBWriter(Context context){
        mDbHelper = DBHelper.getInstance(context);
        db = mDbHelper.getWritableDatabase();
    }


    public long saveQuestionPackRecord(QuestionPackDbEntity questionPack){

        db.beginTransaction();
        deleteQuestionPackWithUniqueName(questionPack.getUniqueName());
        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
        long qpId = addQuestionPackRecord(questionPack, db);

        if(questionPack.getQuestions() == null){
            db.endTransaction();
            closeConnection();
            return -1; // transaction was not successful.
        }

        for(Question question : questionPack.getQuestions()){
            addQuestion(question, qpId);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return qpId; //transaction was successful.
    }


    public boolean addQuestionPack(String jsonData){
        JSONParser parser = new JSONParser();
        List<QuestionPackDbEntity> questionPacks = parser.parseQuestionPacksFromJson(jsonData);
        for(QuestionPackDbEntity qp : questionPacks) {
            saveQuestionPackRecord(qp);
        }
        return true;
    }


    public int deleteQuestionPacks(Set<Integer> questionPackIds){
        int deleteCount = 0;
        for( int questionPackId: questionPackIds){
            if(deleteQuestionPack(questionPackId)){
                deleteCount++;
            }
        }
        return deleteCount;
    }


    private boolean deleteQuestionPack(int questionPackId){
        db.beginTransaction();

            if(!deleteQuestionPackWithId(questionPackId)){
                db.endTransaction();
                return false;
            }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }


    public long getQuestionPackIdForName(String qpName){

        String query = SELECT_ALL_FROM + QuestionPackEntry.TABLE_NAME +
                WHERE + QuestionPackEntry.COLUMN_NAME_TITLE +
                EQUALS + Utils.inQuotes(qpName) + LIMIT_1;

        long questionPackId = -1;
        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
            questionPackId = getLong( cursor, QuestionPackEntry._ID);
        }
        cursor.close();
        return questionPackId;
    }


    private void deleteQuestionPackWithUniqueName(String uniqueName){

        String getIdQuery = SELECT + QuestionPackEntry._ID +
                FROM + QuestionPackEntry.TABLE_NAME +
                WHERE + QuestionPackEntry.COLUMN_NAME_UNIQUE_NAME +
                EQUALS + "'" +   uniqueName + "';" ;

        Cursor cursor = db.rawQuery(getIdQuery, null);

        long questionPackId = -1;
        while(cursor.moveToNext()){
            questionPackId = getLong( cursor, QuestionPackEntry._ID);
        }
        cursor.close();

        // i.e. return true(success) if both the questions and question pack are deleted.
        if(questionPackId != -1){
            if( deleteQuestions(db, questionPackId)){
             deleteQuestionPackRow(db, questionPackId);}}
    }



    private boolean deleteQuestionPackWithId(int questionPackId){

        boolean isSuccessful = true;

        String deleteQuestions = DELETE_FROM + QuestionsEntry.TABLE_NAME +
                WHERE + QuestionsEntry.COLUMN_NAME_QUESTION_PACK_ID +
                EQUALS + "'" + questionPackId + "';";

        String deleteQuestionPack = DELETE_FROM + QuestionPackEntry.TABLE_NAME +
                WHERE + QuestionPackEntry._ID +
                EQUALS + "'" + questionPackId + "';";
        try {
            db.execSQL(deleteQuestions);
            db.execSQL(deleteQuestionPack);
        }catch(SQLException e){
            e.printStackTrace();
            isSuccessful = false;
        }
        return isSuccessful;
    }


    private void deleteQuestionPackRow(SQLiteDatabase db, long questionPackId){

        String query = DELETE_FROM + QuestionPackEntry.TABLE_NAME +
                WHERE + QuestionPackEntry._ID + " = " +
                questionPackId + ";" ;
        runQuery(db, query);
    }


    private boolean deleteQuestions(SQLiteDatabase db, long questionPackId) {

        String query =  DELETE_FROM + QuestionsEntry.TABLE_NAME +
                        WHERE      + QuestionsEntry.COLUMN_NAME_QUESTION_PACK_ID +
                        EQUALS + questionPackId + ";" ;
        return runQuery(db, query);
    }


    private boolean runQuery(SQLiteDatabase db, String query){
        boolean isSuccessful = true;
        try {
            db.execSQL(query);
        }catch(SQLException e){
            e.printStackTrace();
            isSuccessful = false;
        }
        return isSuccessful;
    }


    public boolean addOrOverwriteQuestion(Question q, long questionPackId){
        deleteIfExists(q, questionPackId);
        return addQuestion(q, questionPackId);
    }


    private void deleteIfExists(Question q, long questionPackId){

        String query = DELETE_FROM + QuestionsEntry.TABLE_NAME +
                WHERE + QuestionsEntry.COLUMN_NAME_QUESTION_TEXT +
                EQUALS + Utils.inQuotes(q.getQuestionText()) +
                AND + QuestionsEntry.COLUMN_NAME_QUESTION_PACK_ID +
                EQUALS + questionPackId + ";";

        try {
            db.execSQL(query);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean addQuestion(Question q, long questionPackId){

        ContentValues questionValues = new ContentValues();
        questionValues.put(QuestionsEntry.COLUMN_NAME_QUESTION_TEXT,  q.getQuestionText());
        questionValues.put(QuestionsEntry.COLUMN_NAME_CORRECT_ANSWER, q.getCorrectAnswer());
        questionValues.put(QuestionsEntry.COLUMN_NAME_ANSWER_CHOICES, q.getAnswerChoicesAsString());
        questionValues.put(QuestionsEntry.COLUMN_NAME_TRIVIA,         q.getTrivia());
        questionValues.put(QuestionsEntry.COLUMN_NAME_QUESTION_PACK_ID, questionPackId);
        questionValues.put(QuestionsEntry.COLUMN_NAME_TOPICS, q.getTopicsAsString());
        questionValues.put(QuestionsEntry.COLUMN_NAME_DIFFICULTY, q.getDifficulty());
        questionValues.put(QuestionsEntry.COLUMN_NAME_ANSWER_POOL_NAME, q.getAnswerPoolName());
        questionValues.put(QuestionsEntry.COLUMN_NAME_FLAGS, q.getFlags());
        try {
            db.insertOrThrow(QuestionsEntry.TABLE_NAME, null, questionValues);
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            db.endTransaction();
        }
        return false;
    }


    private long addQuestionPackRecord(QuestionPackDbEntity qp, SQLiteDatabase db ){
        ContentValues questionPackValues = new ContentValues();
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_AUTHOR,             qp.getAuthor());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_TITLE,              qp.getName());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_VERSION,            qp.getVersion());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_UNIQUE_NAME,        qp.getUniqueName());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_DATE_DOWNLOADED,    qp.getDateDownloaded());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_DATE_CREATED,       qp.getDateCreated());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_DESCRIPTION,        qp.getDescription());
        questionPackValues.put(QuestionPackEntry.COLUMN_NAME_DIFFICULTY,          qp.getDifficulty());
        return db.insert(QuestionPackEntry.TABLE_NAME, null, questionPackValues);
    }


    public List <QuestionPackOverview> getQuestionPackOverviews(){

        Cursor cursor =  db.rawQuery(SELECT_ALL_FROM + QuestionPackEntry.TABLE_NAME + ";", null);
        List<QuestionPackOverview> overviews = new ArrayList<>();
        while(cursor.moveToNext()) {
            QuestionPackOverview qpDetail = new QuestionPackOverview();
            qpDetail.setName             (getString( cursor, QuestionPackEntry.COLUMN_NAME_TITLE));
            qpDetail.setDescription      (getString( cursor, QuestionPackEntry.COLUMN_NAME_DESCRIPTION));
            qpDetail.setUniqueName       (getString( cursor, QuestionPackEntry.COLUMN_NAME_UNIQUE_NAME));
            qpDetail.setAuthor           (getString( cursor, QuestionPackEntry.COLUMN_NAME_AUTHOR));
            qpDetail.setDateCreated      (getString( cursor, QuestionPackEntry.COLUMN_NAME_DATE_CREATED));
            qpDetail.setVersion          (getInt(    cursor, QuestionPackEntry.COLUMN_NAME_VERSION));
            qpDetail.setDateDownloaded   (getLong(   cursor, QuestionPackEntry.COLUMN_NAME_DATE_DOWNLOADED));
            qpDetail.setId               (getLong(   cursor, QuestionPackEntry._ID));
            overviews.add(qpDetail);
        }
        cursor.close();
        return overviews;
    }


    List<Integer> getQuestionIdsFromQuestionPackIds(Set <Integer> questionPackIds){

        List<Integer> questionIds = new ArrayList<>();
        String ids = getIds(questionPackIds);

        String query = SELECT + QuestionsEntry._ID +
                        FROM + QuestionsEntry.TABLE_NAME +
                        WHERE + QuestionsEntry.TABLE_NAME + "." + QuestionsEntry.COLUMN_NAME_QUESTION_PACK_ID +
                        IN + ids;

        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
            questionIds.add(getInt(cursor, QuestionsEntry._ID));
        }
        cursor.close();
        return questionIds;
    }


    private String getIds(Set<Integer> questionPackIds){
        String ids = "";
        ids += "(";
        ids += android.text.TextUtils.join( ", ", questionPackIds);
        ids += ")";
        return ids;
    }


    public Question getQuestion(int questionId){
        Question question = null;
        String query = SELECT_ALL_FROM + QuestionsEntry.TABLE_NAME +
                        WHERE + QuestionsEntry._ID +
                        EQUALS + questionId;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            cursor.moveToFirst();
            String questionText     = getString(cursor, QuestionsEntry.COLUMN_NAME_QUESTION_TEXT);
            String correctAnswer    = getString(cursor, QuestionsEntry.COLUMN_NAME_CORRECT_ANSWER);
            String answerChoices    = getString(cursor, QuestionsEntry.COLUMN_NAME_ANSWER_CHOICES);
            String trivia           = getString(cursor, QuestionsEntry.COLUMN_NAME_TRIVIA);
            String topics           = getString(cursor, QuestionsEntry.COLUMN_NAME_TOPICS);
            //int difficulty          = getInt(cursor, QuestionsEntry.COLUMN_NAME_DIFFICULTY);
            String answerPool       = getString(cursor, QuestionsEntry.COLUMN_NAME_ANSWER_POOL_NAME);
            question = new Question(questionText, correctAnswer, answerChoices, trivia, topics, answerPool);
            cursor.close();
        }
        return  question;
    }



    public void closeConnection(){
        mDbHelper.close();
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
