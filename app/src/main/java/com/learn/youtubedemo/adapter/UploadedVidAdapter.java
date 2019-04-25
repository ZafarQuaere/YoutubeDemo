package com.learn.youtubedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.learn.youtubedemo.R;
import com.learn.youtubedemo.model.VideoData;

import java.util.List;

public class UploadedVidAdapter extends BaseAdapter {
    private List<VideoData> mVideos;
    private Context mContext;

    public UploadedVidAdapter(Context context, List<VideoData> videos) {
        mContext = context;
        mVideos = videos;
    }

    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Object getItem(int i) {
        return mVideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mVideos.get(i).getYouTubeId().hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, container, false);
        }

        VideoData video = mVideos.get(position);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(video.getTitle());
        // ((NetworkImageView) convertView.findViewById(R.id.thumbnail)).setImageUrl(video.getThumbUri(), mImageLoader);
        /*if (mGoogleApiClient.isConnected()) {
            ((PlusOneButton) convertView.findViewById(R.id.plus_button)).initialize(video.getWatchUri(), null);
        }
        convertView.findViewById(R.id.main_target).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallbacks.onVideoSelected(mVideos.get(position));
                    }
                });*/
        return convertView;
    }
}