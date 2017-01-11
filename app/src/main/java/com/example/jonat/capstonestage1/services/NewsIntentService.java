package com.example.jonat.capstonestage1.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by jonat on 1/9/2017.
 */


public class NewsIntentService extends IntentService {

    public NewsIntentService() {
        super(NewsIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArticleSyncService.getQuotes(getApplicationContext());
    }
}