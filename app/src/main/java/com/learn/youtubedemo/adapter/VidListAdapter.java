package com.learn.youtubedemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusOneButton;
import com.learn.youtubedemo.R;
import com.learn.youtubedemo.VidListActivity;
import com.learn.youtubedemo.model.VideoData;

import java.util.List;

public class VidListAdapter extends RecyclerView.Adapter<VidListAdapter.MyViewHolder> {

    private final GoogleApiClient mGoogleApiClient;
    private final ImageLoader mImageLoader;
    VidListActivity.Callbacks mCallback;
    private List<VideoData> mVideoList;
    private Context mContext;

    public VidListAdapter(Context context, List<VideoData> videData, VidListActivity.Callbacks mCallbacks, GoogleApiClient mGoogleApiClient, ImageLoader mImageLoader) {
        mContext = context;
        this.mVideoList = videData;
        this.mGoogleApiClient = mGoogleApiClient;
        this.mCallback = mCallbacks;
        this.mImageLoader = mImageLoader;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vidlist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final VideoData vidData = mVideoList.get(position);
        holder.title.setText(vidData.getTitle());
        holder.thumbnail.setImageUrl(vidData.getThumbUri(), mImageLoader);
        if (mGoogleApiClient.isConnected()) {
            holder.plus_button.initialize(vidData.getWatchUri(), null);
        }
        holder.main_target.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onVideoSelected(mVideoList.get(position));
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final NetworkImageView thumbnail;
        private final PlusOneButton plus_button;
        private final LinearLayout main_target;
        public TextView title, genre;

        public MyViewHolder(View view) {
            super(view);
            main_target = (LinearLayout) view.findViewById(R.id.main_target);
            thumbnail = (NetworkImageView) view.findViewById(R.id.thumbnail);
            plus_button = (PlusOneButton) view.findViewById(R.id.plus_button);
        }
    }
}