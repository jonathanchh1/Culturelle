package com.example.jonat.capstonestage1.widget;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jonat on 1/10/2017.
 */

public class NewsArticlePreference {
    private static final String SOURCE_KEY = "articles_source";
    private static final String DEFAULT_SOURCE_VALUE = "mtv-news";
    private static final String DEFAULT_SOURCE_NAME = "MTV NEWS";

    private static final String PREFKEY = "NewsArticlePreference";
    private SharedPreferences mArticlesPreferences;

    public NewsArticlePreference(Context context){
        mArticlesPreferences = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }

    public void clearPreference(){
        SharedPreferences.Editor editor = mArticlesPreferences.edit();
        editor.clear().apply();
    }

    public void addSourceValue(String value){
        SharedPreferences.Editor editor = mArticlesPreferences.edit();
        editor.putString(SOURCE_KEY, value);
        editor.apply();
    }

    public void addSourceName(String value, String sourceName){
        SharedPreferences.Editor editor = mArticlesPreferences.edit();
        editor.putString(value, sourceName);
        editor.apply();
    }

    public String getSourceValue(){
        return mArticlesPreferences.getString(SOURCE_KEY, DEFAULT_SOURCE_VALUE);
    }

    public String getSourceName(String sourceValue){
        return mArticlesPreferences.getString(sourceValue, DEFAULT_SOURCE_NAME);
    }
}
