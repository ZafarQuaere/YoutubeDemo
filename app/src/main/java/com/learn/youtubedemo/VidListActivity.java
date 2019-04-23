package com.learn.youtubedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.learn.youtubedemo.utils.AppLoaderFragment;
import com.learn.youtubedemo.utils.LogUtils;
import com.learn.youtubedemo.utils.MyJsonObjectRequest;
import org.json.JSONObject;

public class VidListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
   String DEVELOPER_API_KEY = "AIzaSyBoFwzMwL5wsIbvsmuxONzXNWOlwNzqzYc";
    private Context mContext;
    private AppLoaderFragment loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        mContext = this;
        loader = AppLoaderFragment.getInstance(mContext);

        initUI();
        LogUtils.DEBUG("playlistItems error : "+"callApi()");
        callApi();
    }

    private void callApi() {
        loader.show();
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=PLca5AIG-kWlrDumHud6qMChcPU6HQG6oN&key="+DEVELOPER_API_KEY;
        JSONObject jObj = new JSONObject();
        MyJsonObjectRequest request = new MyJsonObjectRequest(this, Request.Method.GET, url, jObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.DEBUG("playlistItems REsponse : "+response.toString());
                loader.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                LogUtils.DEBUG("playlistItems error : "+error.toString());
            }
        });

    }

    private void initUI() {
        //getting recyclerview from xml
         recyclerView = (RecyclerView)findViewById(R.id.recyclerVidList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

       // mAdapter = new VidListAdapter(movieList);

    }
}
