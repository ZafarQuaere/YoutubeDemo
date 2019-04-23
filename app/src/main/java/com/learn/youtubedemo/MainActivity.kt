package com.learn.youtubedemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.youtube.player.YouTubeIntents
import com.learn.youtubedemo.utils.AppLoaderFragment

class MainActivity : AppCompatActivity() {

    private lateinit var loader: AppLoaderFragment
    private val SELECT_VIDEO_REQUEST: Int = 1000
    private val EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY"
    private val mContext: Context = this

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

    fun uploadFuncionality(view: View) {
            val intent = Intent(this, UploadVideoActivity::class.java)
        startActivity(intent)
        // This will load a picker view in the users' gallery.
        // The upload activity is started in the function onActivityResult.
//        intent = Intent(Intent.ACTION_PICK, null).setType("video/*")
//        intent.putExtra(EXTRA_LOCAL_ONLY, true)
//        startActivityForResult(intent, SELECT_VIDEO_REQUEST)
       // callApi()
    }

    fun displayVideoList(view: View){
       // startActivity( Intent(this,VidListActivity::class));
        var intent = Intent(this,VidListActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_VIDEO_REQUEST -> {
                    val intent = YouTubeIntents.createUploadIntent(this, data!!.getData()!!)
                    startActivity(intent)
                }
            }
            super.onActivityResult(requestCode, resultCode, data)

        }
    }
}
