package com.example.jonat.capstonestage1.ContentProviders;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by jonat on 9/1/2016.
 */public class NewsDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = NewsDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "favs.db";


    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create database table here
        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " ( " +
                NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                NewsContract.NewsEntry.COLUMN_NEWS_TITLE + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_URL + "TEXT NOT NULL," +
                NewsContract.NewsEntry.COLUMN_AUTHOR + " REAL NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_PUBLISHED + " TEXT NOT NULL "  +
                "); ";

        //gotta do logging
        Log.d(LOG_TAG,SQL_CREATE_FAV_TABLE);

        db.execSQL(SQL_CREATE_FAV_TABLE);

        Log.d(LOG_TAG,"all tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this will be invoked when we will change DATABASE_VERSION aka schema of database.
        // if we upgrade schema user will lost his fav. collection
        //comment this out if you don't want this to happen
        db.execSQL("DROP TABLE IF EXISTS " + NewsContract.NewsEntry.TABLE_NAME);
        onCreate(db);
    }
}

