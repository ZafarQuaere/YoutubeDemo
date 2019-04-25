package com.learn.youtubedemo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.learn.youtubedemo.R;

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

    public static void logAndShow(Activity activity, String tag, Throwable t) {
        Log.e(tag, "Error", t);
        String message = t.getMessage();
        if (t instanceof GoogleJsonResponseException) {
            GoogleJsonError details = ((GoogleJsonResponseException) t).getDetails();
            if (details != null) {
                message = details.getMessage();
            }
        } /*else if (t.getCause() instanceof GoogleAuthException) {
            message = ((GoogleAuthException) t.getCause()).getMessage();
        }*/
        showError(activity, message);
    }
    public static void showError(Activity activity, String message) {
        String errorMessage = getErrorMessage(activity, message);
        showErrorInternal(activity, errorMessage);
    }

    private static void showErrorInternal(final Activity activity, final String errorMessage) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String getErrorMessage(Activity activity, String message) {
        Resources resources = activity.getResources();
        if (message == null) {
            return resources.getString(R.string.error);
        }
        return resources.getString(R.string.error_format, message);
    }

}
