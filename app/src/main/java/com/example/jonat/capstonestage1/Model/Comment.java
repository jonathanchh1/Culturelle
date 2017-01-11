package com.example.jonat.capstonestage1.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jonat on 1/2/2017.
 */

public class Comment implements Parcelable{

    public String uid;
    public String author;
    public String text;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }

    protected Comment(Parcel in) {
        uid = in.readString();
        author = in.readString();
        text = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(author);
        dest.writeString(text);
    }
}
