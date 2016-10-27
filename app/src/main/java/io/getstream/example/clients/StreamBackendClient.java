package io.getstream.example.clients;

import android.content.Context;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class StreamBackendClient {
    private static final String BASE_URL = "http://go.sl.gtstrm.com:3000";

    private static AsyncHttpClient asyncClient = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();

    public static void get(
            Context context,
            String url,
            Header[] headers,
            RequestParams params,
            AsyncHttpResponseHandler responseHandler) {

        asyncClient.get(context, getAbsoluteUrl(url), headers, params, responseHandler);
    }

    public static void post(
            Context context,
            String url,
            RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        syncClient.post(context, getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}