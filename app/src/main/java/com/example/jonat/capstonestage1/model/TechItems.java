package com.example.jonat.capstonestage1.model;

/**
 * Created by jonat on 12/20/2016.
 */

public class TechItems {
    private String title;
    private String thumbnail;
    private String author;
    private String description;
    private String url;
    private String mPublish;

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
}
