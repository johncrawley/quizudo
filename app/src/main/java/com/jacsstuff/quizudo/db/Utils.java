package com.jacsstuff.quizudo.db;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Utils {

    public static String inQuotes(String str){
        return "'" + str + "'";
    }

    public static long addValuesToTable(SQLiteDatabase db, String tableName, ContentValues contentValues){
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


    public static boolean executeStatement(SQLiteDatabase db, String query){
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

}
