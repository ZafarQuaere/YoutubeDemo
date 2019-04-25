package com.learn.youtubedemo

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.learn.youtubedemo.utils.AppConstant
import com.learn.youtubedemo.utils.AppConstant.SCOPES
import com.learn.youtubedemo.utils.AppLoaderFragment
import com.learn.youtubedemo.utils.LogUtils
import com.learn.youtubedemo.utils.Util
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mCredential: GoogleAccountCredential
    private lateinit var loader: AppLoaderFragment
    private val mContext: Context = this
    private var MOVE_TO_ACTIVITY: String = "Upload"
    private val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_AUTHORIZATION = 1001
    val PREF_ACCOUNT_NAME = "accountName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loader = AppLoaderFragment.getInstance(mContext)

    }

    /* private fun callApi() {
         loader.show()
         val obj : JSONObject = JSONObject()
         val url = AppConstant.baseUrl + AppConstant.commentsUrl+DeveloperKey.DEVELOPER_API_KEY
         LogUtils.DEBUG("URL : $url\nRequest Body ::")
         val objectRequest = MyJsonObjectRequest(mContext, Request.Method.GET, url, obj,
             Response.Listener { response ->
                 LogUtils.DEBUG("${AppConstant.baseUrl + AppConstant.commentsUrl} $response")
                 loader.dismiss()
             }, Response.ErrorListener { error ->
                 LogUtils.DEBUG("${AppConstant.baseUrl + AppConstant.commentsUrl} " + error.message)
                 loader.dismiss()
             })

     }*/

    fun uploadFunctionality(view: View) {
        MOVE_TO_ACTIVITY = "Upload"
        validateAccountCredentials()

    }

    fun displayVideoList(view: View) {
        MOVE_TO_ACTIVITY = "VideoList"
        validateAccountCredentials()

    }


    private fun validateAccountCredentials() {
        val accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null)

        if (accountName == null) {
            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(applicationContext, Arrays.asList(*SCOPES))
                .setBackOff(ExponentialBackOff())
            AppConstant.googleCredential = mCredential
            chooseAccount()
        } else {
            val intent: Intent = if (MOVE_TO_ACTIVITY == "Upload") {
                Intent(this, UploadVideoActivity::class.java)
            } else {
                Intent(this, VidListActivity::class.java)
            }
            startActivity(intent)
        }
    }

    private fun getResultsFromApi() {

        if (!Util.isGooglePlayServicesAvailable(mContext)) {
            Util.acquireGooglePlayServices(mContext)
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!Util.isDeviceOnline(mContext)) {
            LogUtils.showToast(mContext, "No network connection available.")
        } else {
            //TODO navigate to respected screen/upload or list of videos screen.
            val intent: Intent
            if (MOVE_TO_ACTIVITY.equals("Upload")) {
                intent = Intent(this, UploadVideoActivity::class.java)
            } else {
                intent = Intent(this, VidListActivity::class.java)
            }
            startActivity(intent)
        }
    }

    private fun chooseAccount() {
        val accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null)
        if (accountName != null) {
            mCredential.setSelectedAccountName(accountName)
            getResultsFromApi()
        } else {
            // Start a dialog from which the user can choose an account
            startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_ACCOUNT_PICKER -> {
                    if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                        val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                        if (accountName != null) {
                            mCredential.setSelectedAccountName(accountName)
                            val settings = getPreferences(Context.MODE_PRIVATE)
                            val editor = settings.edit()
                            editor.putString(PREF_ACCOUNT_NAME, accountName)
                            editor.apply()
                            getResultsFromApi()
                        }
                    }
                }
                REQUEST_AUTHORIZATION -> {
                    if (resultCode == Activity.RESULT_OK) {
                        getResultsFromApi()
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
