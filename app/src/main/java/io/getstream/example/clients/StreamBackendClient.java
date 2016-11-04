package io.getstream.example.clients;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import io.getstream.example.MyApplication;
import io.getstream.example.R;


public class StreamBackendClient {
    private static final String BASE_URL = MyApplication.getAppContext().getString(R.string.backend_url);

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

    public static void putImage(
            Context context,
            String userUUID,
            String photoUri,
            JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        File photoFile = new File(photoUri);
        try {
            params.put("upload", photoFile);
            params.put("myUUID", userUUID);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        asyncClient.post(context, getAbsoluteUrl("/upload"), params, responseHandler);
    }

    public static void post(
            Context context,
            String url,
            RequestParams params,
            JsonHttpResponseHandler responseHandler) {

        syncClient.post(context, getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}