package com.jacsstuff.quizudo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by John on 31/12/2016.
 *
 *
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;
    private final Context context;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Quiz.db";

    private static final String OPENING_BRACKET = " (";
    private static final String CLOSING_BRACKET = " );";
    private static final  String INTEGER = " INTEGER";
    private static final String TEXT = " TEXT";
    private static final String BLOB = " BLOB";
    private static final String COMMA = ",";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";


    private static final String SQL_CREATE_QUESTION_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.QuestionsEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.QuestionPackEntry._ID                          + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_QUESTION_PACK_ID    + INTEGER               + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_QUESTION_TEXT       + TEXT                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_CORRECT_ANSWER      + TEXT                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_ANSWER_CHOICES      + TEXT                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_TRIVIA              + TEXT                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_FLAGS               + TEXT                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_IMAGE               + BLOB                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_SOUND               + BLOB                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_DIFFICULTY          + INTEGER               + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_ANSWER_POOL_NAME    + TEXT                  + COMMA +
                    DbContract.QuestionsEntry.COLUMN_NAME_TOPICS              + TEXT + CLOSING_BRACKET;

    private static final String SQL_CREATE_QUESTION_PACK_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.QuestionPackEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.QuestionPackEntry._ID                            + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_AUTHOR             + TEXT                  + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_DATE_CREATED       + TEXT                  + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_DATE_DOWNLOADED    + INTEGER               + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_VERSION            + INTEGER               + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_DESCRIPTION        + TEXT                  + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_DIFFICULTY         + INTEGER               + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_THUMBNAIL          + BLOB                  + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_UNIQUE_NAME        + TEXT                  + COMMA +
                    DbContract.QuestionPackEntry.COLUMN_NAME_TITLE              + TEXT + CLOSING_BRACKET;


    private static final String SQL_CREATE_ANSWER_POOL_NAMES_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.AnswerPoolNamesEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.AnswerPoolNamesEntry._ID                             + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.AnswerPoolNamesEntry.COLUMN_NAME_APOOL_NAME          + TEXT    + " unique "  + CLOSING_BRACKET;

    private static final String SQL_CREATE_ANSWER_POOL_ITEMS_TABLE =
            CREATE_TABLE_IF_NOT_EXISTS + DbContract.AnswerPoolItemsEntry.TABLE_NAME + OPENING_BRACKET +
                    DbContract.AnswerPoolItemsEntry._ID                            + INTEGER + PRIMARY_KEY  + COMMA +
                    DbContract.AnswerPoolItemsEntry.COLUMN_NAME_APOOL_ID           + INTEGER                + COMMA +
                    DbContract.AnswerPoolItemsEntry.COLUMN_NAME_ANSWER             + TEXT  + " unique "     + CLOSING_BRACKET;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbContract.QuestionsEntry.TABLE_NAME;


    private DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DBHelper getInstance(Context context){
        if(instance == null){
            instance = new DBHelper(context);
        }
        return instance;
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", " Entering onCreate() ...creating the db tables.");
        db.execSQL(SQL_CREATE_QUESTION_TABLE);
        db.execSQL(SQL_CREATE_QUESTION_PACK_TABLE);
        db.execSQL(SQL_CREATE_ANSWER_POOL_NAMES_TABLE);
        db.execSQL(SQL_CREATE_ANSWER_POOL_ITEMS_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }





}
