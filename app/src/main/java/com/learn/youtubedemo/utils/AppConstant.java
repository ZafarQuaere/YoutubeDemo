package com.learn.youtubedemo.utils;

import com.google.android.gms.common.Scopes;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTubeScopes;

public class AppConstant {

    public static final Integer SUCCESS = 1;
    public static final Integer FAILURE = 0;
    public static final String COMINGFROM = "ComingFrom";


    public static final String TAG = "YoutubeDemo";
    public static final String RUPEES_SYMBOL = "₹";
    public static final String ORDER_STATUS_NEW = "2";
    public static final String ORDER_STATUS_COMPLETED = "1";
    public static final String UPLOAD = "https://www.googleapis.com/auth/youtube.upload";

    public static final String[] SCOPES = {Scopes.PROFILE, YouTubeScopes.YOUTUBE};
   // private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_UPLOAD};



    public static final String baseUrl = "https://www.googleapis.com/youtube/v3/";
    public static final String listUrl = "/api/users?page=";
    public static final String userByIdUrl = "/api/users/2";
    public static final String registerUrl = "/api/register";
    public static final String playlistItem = "playlistItems?part=contentDetails&playlistId="+"{UPLOADS_PLAYLIST_ID}";

    public static GoogleAccountCredential googleCredential = null;

}
