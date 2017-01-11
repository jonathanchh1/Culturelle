package com.example.jonat.capstonestage1.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by jonat on 1/9/2017.
 */

public class ArticlesProvider extends ContentProvider {
    private static final String LOG_TAG = ArticlesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArticlesDBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int FLAVOR = 100;
    private static final int FLAVOR_WITH_ID = 200;
    ////////

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArticlesContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, ArticlesContract.ArticleEntry.TABLE_ARTICLES, FLAVOR);
        matcher.addURI(authority, ArticlesContract.ArticleEntry.TABLE_ARTICLES + "/#", FLAVOR_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new ArticlesDBHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FLAVOR:{
                return ArticlesContract.ArticleEntry.CONTENT_DIR_TYPE;
            }
            case FLAVOR_WITH_ID:{
                return ArticlesContract.ArticleEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Articles selected
            case FLAVOR:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArticlesContract.ArticleEntry.TABLE_ARTICLES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                if (retCursor != null) {
                    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return retCursor;
            }
            // Individual flavor based on Id selected
            case FLAVOR_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArticlesContract.ArticleEntry.TABLE_ARTICLES,
                        projection,
                        ArticlesContract.ArticleEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                if (retCursor != null) {
                    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                }
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case FLAVOR: {
                long _id = db.insert(ArticlesContract.ArticleEntry.TABLE_ARTICLES, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = ArticlesContract.ArticleEntry.buildArticlesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case FLAVOR:
                numDeleted = db.delete(
                        ArticlesContract.ArticleEntry.TABLE_ARTICLES, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        ArticlesContract.ArticleEntry.TABLE_ARTICLES + "'");
                break;
            case FLAVOR_WITH_ID:
                numDeleted = db.delete(ArticlesContract.ArticleEntry.TABLE_ARTICLES,
                        ArticlesContract.ArticleEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        ArticlesContract.ArticleEntry.TABLE_ARTICLES + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return numDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case FLAVOR:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInserted = 0;
                try{
                    for(ContentValues value : values){
                        if (value == null){
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try{
                            _id = db.insertOrThrow(ArticlesContract.ArticleEntry.TABLE_ARTICLES,
                                    null, value);
                        }catch(SQLiteConstraintException e) {
                            Log.w(LOG_TAG, "Attempting to insert " +
                                    value.getAsString(
                                            ArticlesContract.ArticleEntry.COLUMN_VERSION_NAME)
                                    + " but value is already in database.");
                        }
                        if (_id != -1){
                            numInserted++;
                        }
                    }
                    if(numInserted > 0){
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0){
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case FLAVOR:{
                numUpdated = db.update(ArticlesContract.ArticleEntry.TABLE_ARTICLES,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FLAVOR_WITH_ID: {
                numUpdated = db.update(ArticlesContract.ArticleEntry.TABLE_ARTICLES,
                        contentValues,
                        ArticlesContract.ArticleEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }


}

