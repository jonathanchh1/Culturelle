package com.example.jonat.capstonestage1.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.jonat.capstonestage1.ContentProviders.NewsContract;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonat on 12/20/2016.
 */

public class GossipFeedItems implements Parcelable{
    private String title;
    private String thumbnail;
    private String author;
    private String description;
    private String url;
    private String mPublish;


    public GossipFeedItems() {

    }

    public static final int COL_ID = 0;
    public static final int COL_NEWS_TITLE = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_AUTHOR = 3;
    public static final int COL_DESCRIPTION = 4;
    public static final int COL_URL = 5;
    public static final int COL_PULISHED= 6;



    public GossipFeedItems(Parcel in) {
        title = in.readString();
        thumbnail = in.readString();
        author = in.readString();
        description = in.readString();
        url = in.readString();
        mPublish = in.readString();
    }

    public GossipFeedItems(Cursor cursor){
        this.title = cursor.getString(COL_NEWS_TITLE);
        this.thumbnail = cursor.getString(COL_POSTER_PATH);
        this.author = cursor.getString(COL_AUTHOR);
        this.description = cursor.getString(COL_DESCRIPTION);
        this.url = cursor.getString(COL_URL);
        this.mPublish = cursor.getString(COL_PULISHED);

    }
    public static final Creator<GossipFeedItems> CREATOR = new Creator<GossipFeedItems>() {
        @Override
        public GossipFeedItems createFromParcel(Parcel in) {
            return new GossipFeedItems(in);
        }

        @Override
        public GossipFeedItems[] newArray(int size) {
            return new GossipFeedItems[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getmPublish() {
        return mPublish;
    }

    public void setmPublish(String mPublish) {
        this.mPublish = mPublish;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(thumbnail);
        dest.writeString(author);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(mPublish);
    }
}
