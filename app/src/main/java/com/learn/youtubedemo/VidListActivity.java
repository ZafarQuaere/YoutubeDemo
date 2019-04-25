package com.learn.youtubedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.learn.youtubedemo.adapter.VidListAdapter;
import com.learn.youtubedemo.model.VideoData;
import com.learn.youtubedemo.utils.AppConstant;
import com.learn.youtubedemo.utils.AppLoaderFragment;
import com.learn.youtubedemo.utils.LogUtils;
import com.learn.youtubedemo.utils.Util;

import java.io.IOException;
import java.util.*;

import static com.learn.youtubedemo.UploadVideoActivity.PREF_ACCOUNT_NAME;
import static com.learn.youtubedemo.UploadVideoActivity.REQUEST_AUTHORIZATION;

public class VidListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    String DEVELOPER_API_KEY = "AIzaSyBoFwzMwL5wsIbvsmuxONzXNWOlwNzqzYc";
    private RecyclerView recyclerView;
    private Context mContext;
    private AppLoaderFragment loader;
    private VidListAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Callbacks mCallbacks;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        mContext = this;
        loader = AppLoaderFragment.getInstance(mContext);
        String accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);


        initUI();
        LogUtils.DEBUG("playlistItems error : " + "callApi()");
        //callApi();
        getUPloadedVideos();
    }

    @SuppressLint("StaticFieldLeak")
    private void getUPloadedVideos() {
        new AsyncTask<Void, Void, List<VideoData>>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loader.show();
            }

            @Override
            protected List<VideoData> doInBackground(Void... voids) {
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = new AndroidJsonFactory(); // GsonFactory

                YouTube youtube = new YouTube.Builder(transport, jsonFactory, AppConstant.googleCredential).
                        setApplicationName(getString(R.string.app_name))
                        .build();

                try {
                    /*
                     * Now that the user is authenticated, the app makes a
                     * channels list request to get the authenticated user's
                     * channel. Returned with that data is the playlist id for
                     * the uploaded videos.
                     * https://developers.google.com/youtube
                     * /v3/docs/channels/list
                     */
                    ChannelListResponse clr = youtube.channels()
                            .list("snippet,contentDetails").setMine(true).execute();

                    // Get the user's uploads playlist's id from channel list
                    // response
                    String uploadsPlaylistId = clr.getItems().get(0)
                            .getContentDetails().getRelatedPlaylists()
                            .getUploads();

                    List<VideoData> videos = new ArrayList<VideoData>();

                    // Get videos from user's upload playlist with a playlist
                    // items list request
                    PlaylistItemListResponse pilr = youtube.playlistItems()
                            .list("id,contentDetails")
                            .setPlaylistId(uploadsPlaylistId)
                            .setMaxResults(20l).execute();
                    List<String> videoIds = new ArrayList<String>();

                    // Iterate over playlist item list response to get uploaded
                    // videos' ids.
                    for (PlaylistItem item : pilr.getItems()) {
                        videoIds.add(item.getContentDetails().getVideoId());
                    }

                    // Get details of uploaded videos with a videos list
                    // request.
                    VideoListResponse vlr = youtube.videos()
                            .list("id,snippet,status")
                            //.list(YoutubeConfig.VIDO_SNIPPET_TAG)
                            .setId(TextUtils.join(",", videoIds)).execute();

                    // Add only the public videos to the local videos list.
                    for (Video video : vlr.getItems()) {
                        if ("public".equals(video.getStatus()
                                .getPrivacyStatus())) {
                            VideoData videoData = new VideoData();
                            VideoSnippet videoSnippet = video.getSnippet();
                            List<String> videoTagList = videoSnippet.getTags();
                            List<String> tagsList = Arrays.asList(new String[]{"Zafar,Test"});
                            for (String tag : tagsList) {
                                if (videoTagList.contains(tag)) {
                                    videoData.setVideo(video);
                                    videos.add(videoData);
                                }
                            }
                        }
                    }

                    // Sort videos by title
                    Collections.sort(videos, new Comparator<VideoData>() {
                        @Override
                        public int compare(VideoData videoData,
                                           VideoData videoData2) {
                            return videoData.getTitle().compareTo(
                                    videoData2.getTitle());
                        }
                    });

                    return videos;

                } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                    Util.showGooglePlayServicesAvailabilityErrorDialog(VidListActivity.this, availabilityException.getConnectionStatusCode());
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    startActivityForResult(userRecoverableException.getIntent(),
                            REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    Util.logAndShow(VidListActivity.this, getString(R.string.app_name), e);
                }
                loader.dismiss();
                return null;
            }

            @Override
            protected void onPostExecute(List<VideoData> videos) {
                setProgressBarIndeterminateVisibility(false);

                if (videos == null) {
                    return;
                }
                loader.dismiss();

                updateList(videos);
            }

        }.execute((Void) null);

    }

    private void updateList(List<VideoData> videos) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerVidList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new VidListAdapter(mContext, videos, mCallbacks, mGoogleApiClient, mImageLoader);
        recyclerView.setAdapter(mAdapter);
    }

   /* private void callApi() {
       // loader.show();
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=PLca5AIG-kWlrDumHud6qMChcPU6HQG6oN&key="+DEVELOPER_API_KEY;
        JSONObject jObj = new JSONObject();
        MyJsonObjectRequest request = new MyJsonObjectRequest(this, Request.Method.GET, url, jObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.DEBUG("playlistItems REsponse : "+response.toString());
            //    loader.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            //    loader.dismiss();
                LogUtils.DEBUG("playlistItems error : "+error.toString());
            }
        });

    }*/

    private void initUI() {

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        mCallbacks = new Callbacks() {
            @Override
            public ImageLoader onGetImageLoader() {
                return null;
            }

            @Override
            public void onVideoSelected(VideoData video) {

            }

            @Override
            public void onConnected(String connectedAccountName) {

            }
        };
        mImageLoader = mCallbacks.onGetImageLoader();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public interface Callbacks {
         ImageLoader onGetImageLoader();
         void onVideoSelected(VideoData video);
         void onConnected(String connectedAccountName);
    }
}
