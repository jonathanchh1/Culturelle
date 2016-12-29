package com.example.jonat.capstonestage1;

import android.content.Context;
import android.database.Cursor;

import com.example.jonat.capstonestage1.ContentProviders.NewsContract;

/**
 * Created by jonat on 10/4/2016.
 */
public class Utility {

    //takes movie_id and tells whether or not that movie is favored
    public static int isFavored(Context context, String title) {
        Cursor cursor = context.getContentResolver().query(
                NewsContract.NewsEntry.CONTENT_URI,
                null,   // projection
                NewsContract.NewsEntry.COLUMN_NEWS_TITLE + " = ?", // selection
                new String[] { title },   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }

    public static String buildPosterUrl(String PosterPath) {
        //use recommended w185 size for image
        return "http://image.tmdb.org/t/p/w185" + PosterPath;
    }

    public static String buildBackdropUrl(String Backdrop){
        return "http://image.tmdb.org/t/p/original" + Backdrop;
    }
}

