package com.example.jonat.capstonestage1.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 1/9/2017.
 */

public class NewsReport {
    private String source;
    private List<NewsFeed> articles;

    public NewsReport(String source, List<NewsFeed> articles) {
        this.source = source;
        this.articles = articles;
    }


    public String getSource() {
        return source;
    }


    public List<NewsFeed> getArticles() {
        return articles;
    }

    @Override
    public String toString() {
        List<String> mStrings = new ArrayList<>();
        for (NewsFeed article :
                getArticles()) {
            String item = article.getAuthor() + article.getTitle() + "\n";
            mStrings.add(item);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s :
                mStrings) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}