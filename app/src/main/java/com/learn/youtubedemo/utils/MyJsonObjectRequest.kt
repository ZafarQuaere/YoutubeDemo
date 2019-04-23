package com.learn.youtubedemo.utils


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class MyJsonObjectRequest(
    val mContext: Context, method: Int, url: String, requestBody: JSONObject,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
) : JsonObjectRequest(method, url, requestBody, listener, errorListener) {

    private val preferences: SharedPreferences


    init {
        //if server is not working pick the default response from local storage.
        retryPolicy = DefaultRetryPolicy(
            1000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
    }


    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        LogUtils.DEBUG(" parseNetworkResponse statusCode >> " + response.statusCode)

        //LogUtils.DEBUG(" [raw json]: " + (new String(response.data)));
        var jsonString: String? = null
        try {
            jsonString =
                String(response.data, HttpHeaderParser.parseCharset(response.headers, JsonRequest.PROTOCOL_CHARSET))
            return if (jsonString == null || jsonString.length < 3) Response.error(ParseError(NullPointerException())) else Response.success(
                JSONObject(jsonString),
                HttpHeaderParser.parseCacheHeaders(response)
            )

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            return Response.error(ParseError(e))
        }

        return Response.error(ParseError())
    }

    private fun String(bytes: ByteArray?, charset: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun parseNetworkError(volleyError: VolleyError): VolleyError {
        return super.parseNetworkError(volleyError)
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return super.getHeaders()
    }

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return super.getParams()
    }
}
