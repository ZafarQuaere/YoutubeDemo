package com.learn.youtubedemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.learn.youtubedemo.R;


public class LogUtils {

    public static void showToast(Context context, String message) {
        if (context != null) {
            final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void DEBUG(String sb) {
        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    Log.d(AppConstant.TAG, " >> " + sb.substring(4000 * i));
                } else {
                    Log.d(AppConstant.TAG, " >> " + sb.substring(4000 * i, max));
                }
            }
        } else {
            Log.d(AppConstant.TAG, " >> " + sb.toString());
        }
    }

    public static void ERROR(String message) {
        Log.e(AppConstant.TAG, " >> " + message);
    }

    public static void showSnackBar(Context context, ViewGroup layout, String msg) {
        Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        //snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_app_theme));
        snackbar.show();
    }
    }
