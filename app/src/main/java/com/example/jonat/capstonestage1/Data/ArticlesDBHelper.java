package com.example.jonat.capstonestage1.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jonat on 1/9/2017.
 */

public class ArticlesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ArticlesDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "culturelle.db";
    private static final int DATABASE_VERSION = 13;

    public ArticlesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                ArticlesContract.ArticleEntry.TABLE_ARTICLES + "(" +
                ArticlesContract.ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArticlesContract.ArticleEntry.COLUMN_AUTHOR + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_PUBLISHEDAT + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_TITLE + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_URL + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_URLTOIMAGE + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_VERSION_NAME + " TEXT, " +
                ArticlesContract.ArticleEntry.COLUMN_SOURCE +	"  TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArticlesContract.ArticleEntry.TABLE_ARTICLES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                ArticlesContract.ArticleEntry.TABLE_ARTICLES + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}