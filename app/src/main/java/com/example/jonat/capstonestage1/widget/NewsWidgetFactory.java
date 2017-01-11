package com.example.jonat.capstonestage1.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.jonat.capstonestage1.Activities.Utility;
import com.example.jonat.capstonestage1.Data.ArticlesContract;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.Model.NewsFeed;

import java.util.List;

/**
 * Created by jonat on 1/10/2017.
 */public class NewsWidgetFactory extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor;
            private List<NewsFeed> mDataSet;


            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null) {
                    cursor.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                // get data from content provider
                String selection = ArticlesContract.ArticleEntry.COLUMN_SOURCE + "=?";
                String[] selectionArgs = {new NewsArticlePreference(getApplicationContext()).getSourceValue()};
                cursor = getContentResolver().query(
                        ArticlesContract.ArticleEntry.CONTENT_URI,
                        ArticlesContract.ArticleEntry.ARTICLE_COLUMNS,
                        selection, selectionArgs, ArticlesContract.ArticleEntry.DEFAULT_SORT);
                mDataSet = Utility.returnListFromCursor(cursor);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                    mDataSet = null;
                }
            }

            @Override
            public int getCount() {
                return mDataSet == null ? 0 : mDataSet.size();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        cursor == null || mDataSet.size() < 1) {
                    return null;
                }
                RemoteViews remoteViews = new RemoteViews(NewsWidgetFactory.this.getPackageName(),
                        R.layout.widget_list_item);

                String title = mDataSet.get(i).getTitle().equalsIgnoreCase("null")? "":
                        mDataSet.get(i).getTitle();

                String author = "";
                if (mDataSet.get(i) != null && mDataSet.get(i).getAuthor() != null) {
                    author = mDataSet.get(i).getAuthor().equalsIgnoreCase("null")?"":
                            mDataSet.get(i).getAuthor();
                    author = author.isEmpty()? "" : " by " + author;
                }

                remoteViews.setTextViewText(R.id.widget_title, title);
                remoteViews.setTextViewText(R.id.widget_author, author);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}