package com.learn.youtubedemo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.util.Calendar;
import java.util.Date;


public class Util {

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    public static File getFileFromUri(Uri uri, Activity activity) {

        try {
            String filePath = null;

            String[] proj = {MediaStore.Video.VideoColumns.DATA};

            Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);

            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
                filePath = cursor.getString(column_index);
            }

            cursor.close();

            File file = new File(filePath);
            cursor.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean isDeviceOnline(Context mContext) {
        ConnectivityManager connMgr =
                (ConnectivityManager)((AppCompatActivity)mContext). getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isGooglePlayServicesAvailable(Context mContext) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public static void acquireGooglePlayServices(Context mContext) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(mContext,connectionStatusCode);
        }
    }

    public static  void showGooglePlayServicesAvailabilityErrorDialog(Context mContext,final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog((Activity)mContext, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    public static Date getTime(Activity activity){
        Calendar cal = Calendar.getInstance();
        Date time = cal.getTime();
        return time;
    }
}
