package com.example.jonat.capstonestage1.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonat.capstonestage1.R;
import com.example.jonat.capstonestage1.Model.NewsFeed;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jonat on 12/9/2016.
 */
public class TechAdapter extends RecyclerView.Adapter<TechAdapter.CustomViewHolder> {
    private List<NewsFeed> feedItemList;
    private Context mContext;
    private  final Callbacks mCallbacks;


    public interface Callbacks{
        void onTaskCompleted(NewsFeed items, int position);
    }

    public TechAdapter(Context context, List<NewsFeed> feedItemList, Callbacks callbacks) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.mCallbacks = callbacks;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_items, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final NewsFeed feedItem = feedItemList.get(i);
        customViewHolder.items = feedItem;
        //Render image using Picasso library
        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
            Picasso.with(mContext).load(feedItem.getThumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    if(customViewHolder.imageView != null){
                                        customViewHolder.imageView.setVisibility(View.VISIBLE);
                                    }else {
                                        customViewHolder.imageView.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError() {
                                    customViewHolder.textView.setVisibility(View.VISIBLE);
                                }
                            });
        }

        customViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onTaskCompleted(feedItem, customViewHolder.getAdapterPosition());
            }
        });
        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
    }


    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;
        public NewsFeed items;
        View mView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
            mView = view;
        }

    }

}