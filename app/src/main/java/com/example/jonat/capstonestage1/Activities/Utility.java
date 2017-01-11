package com.example.jonat.capstonestage1.Activities;

import android.database.Cursor;
import android.text.format.DateUtils;

import com.example.jonat.capstonestage1.Data.ArticlesContract;
import com.example.jonat.capstonestage1.Model.NewsFeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jonat on 1/9/2017.
 */

public class Utility {

    public static List<NewsFeed> returnListFromCursor(Cursor cursor){
        List<NewsFeed> rowItemList = new ArrayList<>();
        if (cursor.getCount() != 0 && cursor.moveToFirst()){
            do{
                String author = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_AUTHOR));
                String title = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_DESCRIPTION));
                String url = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_URL));
                String urlToImage = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_URLTOIMAGE));
                String publishedAt = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_PUBLISHEDAT));
                String source = cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_SOURCE));
                NewsFeed newsFeed = new NewsFeed();
                newsFeed.setTitle(title);
                newsFeed.setAuthor(author);
                newsFeed.setDescription(description);
                newsFeed.setUrl(url);
                newsFeed.setThumbnail(urlToImage);
                newsFeed.setmPublish(publishedAt);
                newsFeed.setSource(source);
                rowItemList.add(newsFeed);
                // do what ever you want here
            }while(cursor.moveToNext());
        }

        return rowItemList;
    }

    public static String manipulateDateFormat(String post_date){

        SimpleDateFormat existingUTCFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //SimpleDateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Date date = null;
        try {
            date = existingUTCFormat.parse(post_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            // Converting timestamp into x ago format
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(String.valueOf(date.getTime())),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            return timeAgo + "";
        }else {
            return post_date;
        }
    }

}
