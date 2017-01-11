package com.example.jonat.capstonestage1.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jonat on 12/20/2016.
 */

public class NewsFeed implements Parcelable{
    private int id;
    private String title;
    private String thumbnail;
    private String author;
    private String description;
    private String url;
    private String mPublish;
    private String source;


    public NewsFeed(Parcel in) {
        id = in.readInt();
        title = in.readString();
        thumbnail = in.readString();
        author = in.readString();
        description = in.readString();
        url = in.readString();
        mPublish = in.readString();
        source = in.readString();
    }



    public static final Creator<NewsFeed> CREATOR = new Creator<NewsFeed>() {
        @Override
        public NewsFeed createFromParcel(Parcel in) {
            return new NewsFeed(in);
        }

        @Override
        public NewsFeed[] newArray(int size) {
            return new NewsFeed[size];
        }
    };

    public NewsFeed() {

    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(thumbnail);
        dest.writeString(author);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(mPublish);
        dest.writeString(source);
    }
}
