package com.learn.youtubedemo.utils;

public class AppConstant {

    public static final Integer SUCCESS = 1;
    public static final Integer FAILURE = 0;
    public static final String COMINGFROM = "ComingFrom";


    public static final String TAG = "YoutubeDemo";
    public static final String RUPEES_SYMBOL = "₹";
    public static final String ORDER_STATUS_NEW = "2";
    public static final String ORDER_STATUS_COMPLETED = "1";
    public static final String UPLOAD = "https://www.googleapis.com/auth/youtube.upload";





    public static final String baseUrl = "https://www.googleapis.com/youtube/v3/";
    public static final String listUrl = "/api/users?page=";
    public static final String userByIdUrl = "/api/users/2";
    public static final String registerUrl = "/api/register";
    public static final String playlistItem = "playlistItems?part=contentDetails&playlistId="+"{UPLOADS_PLAYLIST_ID}";

}
