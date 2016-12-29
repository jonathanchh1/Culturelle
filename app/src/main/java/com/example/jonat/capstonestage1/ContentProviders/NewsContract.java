package com.example.jonat.capstonestage1.ContentProviders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.jonat.capstonestage1.ContentProviders.NewsContract.NewsEntry.CONTENT_URI;


/**
 * Created by jonat on 10/3/2016.
 */
public class NewsContract {
    //it should be unique in system,we use package name because it is unique
    public static final String CONTENT_AUTHORITY = "com.example.jonat.capstonestage1.ContentProviders";

    //base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //URI end points for Content provider
    public static final String PATH_FAV = "news";


    //for favorites
    public static final class NewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        //these are MIME types ,not really but they are similar to MIME types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        // Table name
        public static final String TABLE_NAME = "news";
        //Movie title
        public static final String COLUMN_NEWS_TITLE = "title";
        //Movie release date
        //path for poster ; it's not actual URL, append it with base poster path with size. example
        // http://image.tmdb.org/t/p/{size}/{poster_path}
        public static final String COLUMN_POSTER_PATH = "urlToImage";
        //vote average for Movie
        public static final String COLUMN_AUTHOR = "author";
        //plot synopsis of Movie

        public static final String COLUMN_URL = "url";

        public static final String COLUMN_DESCRIPTION = "description";

        //wallpaper for movies
        public static final String COLUMN_PUBLISHED = "publishedAt";

        public static Uri buildNewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
