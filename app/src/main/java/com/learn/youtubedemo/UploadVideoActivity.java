package com.learn.youtubedemo;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.learn.youtubedemo.utils.LogUtils;
import com.learn.youtubedemo.utils.Util;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class UploadVideoActivity extends AppCompatActivity {
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    private static final String API_KEY = "AIzaSyBoFwzMwL5wsIbvsmuxONzXNWOlwNzqzYc";
    private static final String TAG = "UploadVideo";
    private static final int CAPTURE_RETURN = 1;
    private static final int GALLERY_RETURN = 2;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_READONLY, YouTubeScopes.YOUTUBE_UPLOAD};
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        mCallApiButton = findViewById(R.id.mCallApiButton);
        mOutputText = findViewById(R.id.tv_outputText);
        mContext = this;

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Permissions.check(UploadVideoActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        "Accounts and Storage permissions are required ", new Permissions.Options()
                                .setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"),
                        new PermissionHandler() {
                            @Override
                            public void onGranted() {
                                mCallApiButton.setEnabled(false);
                                mOutputText.setText("");
                                getResultsFromApi();
                                mCallApiButton.setEnabled(true);
                            }
                        });
            }
        });


    }


    private void getResultsFromApi() {

        if (!Util.isGooglePlayServicesAvailable(mContext)) {
            Util.acquireGooglePlayServices(mContext);
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!Util.isDeviceOnline(mContext)) {
            mOutputText.setText("No network connection available.");
        } else {
//            new SaveTokenAsync().execute();
            mOutputText.setText("Credentials Initialized");
            initVideoPicker();
        }
    }

    private void chooseAccount() {
        String accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);
        if (accountName != null) {
            mCredential.setSelectedAccountName(accountName);
            //  new SaveTokenAsync().execute();
            getResultsFromApi();
        } else {
            // Start a dialog from which the user can choose an account
            startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        }

    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Util.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
            case CAPTURE_RETURN:

            case GALLERY_RETURN:
                if (resultCode == RESULT_OK) {
                    new UploadVideoAsync(data.getData()).execute();
                }
                break;

        }
    }

    private String uploadYoutube(Uri data) {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = new AndroidJsonFactory(); // GsonFactory
       // JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        HttpRequestInitializer initializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                mCredential.initialize(request);
                request.setLoggingEnabled(true);
               request.setIOExceptionHandler(new HttpBackOffIOExceptionHandler(new ExponentialBackOff()));
            }
        };

        YouTube.Builder youtubeBuilder = new YouTube.Builder(transport, jsonFactory, initializer);
        youtubeBuilder.setApplicationName(getString(R.string.app_name));
        youtubeBuilder.setYouTubeRequestInitializer(new YouTubeRequestInitializer());
        YouTube youtube = youtubeBuilder.build();

        String PRIVACY_STATUS = "public"; // or public,private
        String PARTS = "snippet,status,contentDetails";

        String videoId = null;
        try {
            Video videoObjectDefiningMetadata = new Video();
            videoObjectDefiningMetadata.setStatus(new VideoStatus().setPrivacyStatus(PRIVACY_STATUS));

            PlaylistSnippet playlistSnippet = new PlaylistSnippet();
            playlistSnippet.setTitle("Test");

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle("Upload Test " + Util.getTime(this));
            snippet.setDescription("MyDescription");
            snippet.setTags(Arrays.asList(new String[]{"Zafar,Test"}));
            videoObjectDefiningMetadata.setSnippet(snippet);

            YouTube.Videos.Insert videoInsert = youtube.videos().insert(PARTS, videoObjectDefiningMetadata,
                    getMediaContent(Util.getFileFromUri(data, UploadVideoActivity.this)));/*.setOauthToken(token);*/
            // .setKey(API_KEY);

            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                     LogUtils.DEBUG( "progressChanged: " + uploader.getUploadState());
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            break;
                        case INITIATION_COMPLETE:
                            break;
                        case MEDIA_IN_PROGRESS:
                            break;
                        case MEDIA_COMPLETE:
                            break;
                        case NOT_STARTED:
                             LogUtils.DEBUG( "progressChanged: upload_not_started");
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);

            Video returnedVideo = videoInsert.execute();

            LogUtils.DEBUG("Uploading..");
            LogUtils.DEBUG("Video upload completed");
            LogUtils.DEBUG("videoId = "+ returnedVideo.getId());
            LogUtils.DEBUG(" Title: " + returnedVideo.getSnippet().getTitle());
            LogUtils.DEBUG(" Tags: " + returnedVideo.getSnippet().getTags());
            LogUtils.DEBUG(" Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
//            LogUtils.DEBUG(" Video Count: " + returnedVideo.getStatistics().getViewCount());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
             LogUtils.DEBUG( "GooglePlayServicesAvailabilityIOException "+ availabilityException);
        } catch (UserRecoverableAuthIOException userRecoverableException) {
            LogUtils.DEBUG( String.format("UserRecoverableAuthIOException: %s", userRecoverableException));
            this.startActivityForResult(
                    userRecoverableException.getIntent(), UploadVideoActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
             LogUtils.DEBUG ("IOException "+ e);
        }

        return videoId;

    }

    private AbstractInputStreamContent getMediaContent(File file) throws FileNotFoundException {
        InputStreamContent mediaContent = new InputStreamContent("video/*", new BufferedInputStream(new FileInputStream(file)));
        mediaContent.setLength(file.length());
        return mediaContent;
    }

    private void initVideoPicker() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("video/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() <= 0) {
             LogUtils.DEBUG( "no video picker intent on this hardware");
            Toast.makeText(UploadVideoActivity.this, "No video picker found on device", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivityForResult(intent, GALLERY_RETURN);

    }

    public class UploadVideoAsync extends AsyncTask<Void, Void, String> {

        Uri data;

        ProgressDialog progressDialog;


        public UploadVideoAsync(Uri data) {
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UploadVideoActivity.this);
            progressDialog.setMessage("Uploading Video to youtube");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            return uploadYoutube(data);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(UploadVideoActivity.this, "Video Id is " + s, Toast.LENGTH_SHORT).show();
            LogUtils.DEBUG("VidId  "+   s);
            progressDialog.dismiss();
        }
    }

    /*public class SaveTokenAsync extends AsyncTask<Void, Void, String> {
      String token = "";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                 token = mCredential.getToken();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }

            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                SharedPreferences settings =
                        getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString(Constants.TOKEN, token);
                editor.apply();

                Constants.ACCESS_TOKEN = token;
            } else {
                Toast.makeText(UploadVideoActivity.this, "Token empty", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


   /* private void initCaptureButtons() {


        btnCaptureVideo = (Button) findViewById(R.id.btnCaptureVideo);
        btnCaptureVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent i = new Intent();
                i.setAction("android.media.action.VIDEO_CAPTURE");
                startActivityForResult(i, CAPTURE_RETURN);
            }
        });

        btnSelectFromGallery = (Button) findViewById(R.id.btnSelectFromGallery);
        btnSelectFromGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("video/*");

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() <= 0) {
                     LogUtils.DEBUG(TAG, "no video picker intent on this hardware");
                    return;
                }

                startActivityForResult(intent, GALLERY_RETURN);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
        btnSelectFromGallery.setEnabled(true);
        btnCaptureVideo.setEnabled(true);
    }*/


}
