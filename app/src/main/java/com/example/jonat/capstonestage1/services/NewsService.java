package com.example.jonat.capstonestage1.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

/**
 * Created by jonat on 1/9/2017.
 */


public class NewsService extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent nowIntent = new Intent(getApplicationContext(), NewsIntentService.class);
        getApplicationContext().startService(nowIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


}
