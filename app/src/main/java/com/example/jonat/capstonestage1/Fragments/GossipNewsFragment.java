package com.example.jonat.capstonestage1.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Activities.GossipDetailActivity;
import com.example.jonat.capstonestage1.Activities.Utility;
import com.example.jonat.capstonestage1.Adapters.GossipNewsAdapter;
import com.example.jonat.capstonestage1.BuildConfig;
import com.example.jonat.capstonestage1.Data.ArticlesContract;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.services.ArticleSyncService;
import com.example.jonat.capstonestage1.Model.NewsFeed;
import com.example.jonat.capstonestage1.widget.NewsArticlePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jonat on 12/9/2016.
 */

public class GossipNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = GossipNewsFragment.class.getSimpleName();
    public static final String NEWS_KEY = "news";
    private GossipNewsAdapter mAdapter;
     ArrayList<NewsFeed> feedList;
    private static final String LATEST = "latest";
    private String sortOrder = LATEST;
    private NewsFeed newsFeed = new NewsFeed();
    private RecyclerView recyclerView;
    private CoordinatorLayout mcoordinatorlayout;
    private ProgressBar mProgressbar;




    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.recyclerview, container, false);

        ArticleSyncService.initialize(getContext());
        ArticleSyncService.syncImmediately(getContext());
        getLoaderManager().initLoader(0, null, this);
        recyclerView =  (RecyclerView) rootView.findViewById(R.id.mrecyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.grid_column)));
        mProgressbar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mcoordinatorlayout = (CoordinatorLayout) rootView.findViewById(R.id.main_content);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LATEST)) {
                sortOrder = savedInstanceState.getString(LATEST);
            }

            if (savedInstanceState.containsKey(NEWS_KEY)) {
                feedList = savedInstanceState.getParcelableArrayList(NEWS_KEY);
                mAdapter.setData(feedList);
            } else {
                updateNews(sortOrder);
            }
        } else {
            updateNews(sortOrder);
        }

        return rootView;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        feedList = new ArrayList<>();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!sortOrder.contentEquals(LATEST)) {
            outState.putString(NEWS_KEY, sortOrder);
        }
        if (newsFeed != null) {
            outState.putParcelableArrayList(LATEST, feedList);
        }
        super.onSaveInstanceState(outState);
    }

    private void updateNews(String choice) {
        if (!choice.isEmpty()) {
            if (isNetworkAvailable(getContext())) {
                new DownloadTask().execute(choice);
                //onRefresh();
                getLoaderManager().restartLoader(0, null, this);
            }
        } else {
            Snackbar.make(mcoordinatorlayout, getString(R.string.noNetwork), Snackbar.LENGTH_SHORT).show();
        }
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = ArticlesContract.ArticleEntry.COLUMN_SOURCE + "=?";
        String[] selectionArgs = {new NewsArticlePreference(getContext()).getSourceValue()};

        return new CursorLoader(getActivity(),
                ArticlesContract.ArticleEntry.CONTENT_URI,
                ArticlesContract.ArticleEntry.ARTICLE_COLUMNS,
                selection, selectionArgs, ArticlesContract.ArticleEntry.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            feedList.addAll(Utility.returnListFromCursor(data));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // clear data set
        clearDataSet();
    }


    public class DownloadTask extends AsyncTask<String, Void, Integer> implements GossipNewsAdapter.Callbacks {


        @Override
        protected void onPreExecute() {
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                String choice = params[0];
                URL url = new URL("https://newsapi.org/v1/articles?source=mtv-news" +
                        "&sortBy=" + choice + "&apiKey=" +
                BuildConfig.NEWS_API);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            mProgressbar.setVisibility(View.GONE);
            if (result == 1) {
                mAdapter = new GossipNewsAdapter(getActivity(),
                        feedList, this);
                recyclerView.setAdapter(mAdapter);
            }
        }



        @Override
        public void onTaskCompleted(NewsFeed items, int position) {
            Intent intent = new Intent(getActivity(), GossipDetailActivity.class);
            intent.putExtra(GossipDetailActivity.ARG_NEWS, items);
            startActivity(intent);

        }

    }
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("articles");

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                NewsFeed item = new NewsFeed();
                item.setTitle(post.optString("title"));
                item.setThumbnail(post.optString("urlToImage"));
                item.setmPublish(post.optString("publishedAt"));
                item.setUrl(post.optString("url"));
                item.setAuthor(post.optString("author"));
                item.setDescription(post.optString("description"));

                feedList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);

    }

    private void clearDataSet() {
        if (feedList != null) {
            feedList.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetWorkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetWorkInfo == null) {
                Toast.makeText(getActivity(), getString(R.string.noNetwork), Toast.LENGTH_SHORT).show();
            }
            return activeNetWorkInfo != null && activeNetWorkInfo.isConnected();
        }

        public interface Callbacks{
            void onTaskCompleted(List<NewsFeed> items);
        }
    }


