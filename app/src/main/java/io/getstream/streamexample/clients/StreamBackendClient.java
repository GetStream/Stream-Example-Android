package io.getstream.streamexample.clients;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class StreamBackendClient {
    private static final String BASE_URL = "http://192.168.86.154:3000";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(
            Context context,
            String url,
            Header[] headers,
            RequestParams params,
            AsyncHttpResponseHandler responseHandler) {

        client.get(context, getAbsoluteUrl(url), headers, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}