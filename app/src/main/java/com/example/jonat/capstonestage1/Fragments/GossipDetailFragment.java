package com.example.jonat.capstonestage1.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonat.capstonestage1.Activities.GossipDetailActivity;
import com.example.jonat.capstonestage1.ContentProviders.NewsContract;
import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.Utility;
import com.example.jonat.capstonestage1.model.GossipFeedItems;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jonat on 12/23/2016.
 */

public class GossipDetailFragment extends Fragment {

    public static final String LOG_TAG = GossipDetailFragment.class.getSimpleName();
    private TextView mTitle, mAuthor, Description, published;
    private ImageView thumbnail;
    private GossipFeedItems items;
    private ArrayList<GossipFeedItems> newsList;
    private View rootView;
    private Toast mToast;
    private LayoutInflater mLayoutInflater;
    private Button mbuttonList;
    private ShareActionProvider mShareActionProvider;

    public GossipDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof GossipDetailActivity) {
            appBarLayout.setTitle(items.getTitle());
        }

        ImageView backdrop = ((ImageView) activity.findViewById(R.id.detail_backdrop));
        if (backdrop != null) {
            String mbackdrop = items.getThumbnail();
            Picasso.with(getActivity()).load(mbackdrop).into(backdrop);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mLayoutInflater = inflater;

        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();

        if (arguments != null || intent != null && intent.hasExtra(GossipDetailActivity.ARG_NEWS)) {
            rootView = mLayoutInflater.inflate(R.layout.detail_fragment, container, false);
            if (arguments != null) {
                items = getArguments().getParcelable(GossipDetailActivity.ARG_NEWS);
            } else {
                items = intent.getParcelableExtra(GossipDetailActivity.ARG_NEWS);
            }
        }
        mAuthor = (TextView) rootView.findViewById(R.id.author_detail);
        mTitle = (TextView) rootView.findViewById(R.id.title_detail);
        thumbnail = (ImageView) rootView.findViewById(R.id.detail_poster);
        Description = (TextView) rootView.findViewById(R.id.descriptionm);
        published = (TextView) rootView.findViewById(R.id.published);
        mbuttonList = (Button) rootView.findViewById(R.id.read_more_b);


        mTitle.setText(items.getTitle());
        mAuthor.setText(items.getAuthor());
        Description.setText(items.getDescription());
        published.setText(items.getmPublish());

        mbuttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(items.getUrl())));
            }
        });

        mbuttonList.setEnabled(!items.getUrl().isEmpty());
        DisplayInfo(rootView);
        return rootView;
    }

    private void DisplayInfo(View v) {
        if (items != null) {
            String posterUrl = items.getThumbnail();
            Picasso.with(getActivity()).load(posterUrl).into(thumbnail);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favorite_icon:
                if (items != null) {
                    // check if movie is favored or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utility.isFavored(getActivity(), items.getTitle());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavored) {
                            // if it is in favorites
                            if (isFavored == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                NewsContract.NewsEntry.CONTENT_URI,
                                                NewsContract.NewsEntry.COLUMN_NEWS_TITLE+ " = ?",
                                                new String[]{items.getTitle()}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.remove_favorite), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(NewsContract.NewsEntry.COLUMN_NEWS_TITLE, items.getTitle());
                                        values.put(NewsContract.NewsEntry.COLUMN_AUTHOR, items.getAuthor());
                                        values.put(NewsContract.NewsEntry.COLUMN_POSTER_PATH, items.getThumbnail());
                                        values.put(NewsContract.NewsEntry.COLUMN_DESCRIPTION, items.getDescription());
                                        values.put(NewsContract.NewsEntry.COLUMN_URL, items.getUrl());
                                        values.put(NewsContract.NewsEntry.COLUMN_PUBLISHED, items.getmPublish());

                                        return getActivity().getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI, values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.ic_favorite_black_24dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(),
                                                getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;

            case R.id.action_share:
                //share movie trailer
                updateShareActionProvider();


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateShareActionProvider() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, items.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, items.getUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
        if (items != null) {
            Log.d(LOG_TAG, "detail Menu created");

            final MenuItem action_fav = menu.findItem(R.id.favorite_icon);
            MenuItem action_share = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

            //set  icon on toolbar for favored movies
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utility.isFavored(getActivity(), items.getTitle());
                }

                @Override
                protected void onPostExecute(Integer isFavored) {
                    action_fav.setIcon(isFavored == 1 ?
                            R.drawable.ic_favorite_black_24dp :
                            R.drawable.ic_favorite_border_black_24dp);
                }
            }.execute();
        }
     }
    }

