package io.getstream.streamexample;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// skip
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.getstream.streamexample.adapters.FeedItemAdapter;
import io.getstream.streamexample.adapters.FeedsAdapter;
import io.getstream.streamexample.clients.StreamBackendClient;
import io.getstream.streamexample.models.FeedItem;

import static io.getstream.streamexample.Utils.md5;
// skip


public class MainActivity extends AppCompatActivity {
    private ListView feedList;

    private Toast toast;
    public String toastString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // is user registered already?
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();


        String whichScreen;
        // global feed
//        whichScreen = "";
        // registration
//        whichScreen = "register";
        // empty tab layout
//        whichScreen = "tabs";
        // single feed item for the list
//        whichScreen = "feed_item";
        // viewing a user's photos
        whichScreen = "user_profile";

//        String authorID = preferences.getString(getString(R.string.pref_authorid), "");
//        String authorEmail = preferences.getString(getString(R.string.pref_authoremail), "");
//        String authorUsername = preferences.getString(getString(R.string.pref_authorname), "");

        if (whichScreen == "register") {
            Log.i("MainActivity", "registration");
            setContentView(R.layout.register);
        } else if (whichScreen == "feed_item") {
            Log.i("MainActivity", "single feed item");
            setContentView(R.layout.feed_item);
        } else if (whichScreen == "user_profile") {
            Log.i("MainActivity", "user profile");
            setContentView(R.layout.activity_user);
            oldGetProfileFeed();

            String hash = md5("ian.douglas@iandouglas.com");
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Log.i("gravatar url", gravatarUrl);
            Picasso.with(MainActivity.this)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.artist_placeholder)
                    .into((ImageView) this.findViewById(R.id.profile_profile_image));
            TextView pName = (TextView)this.findViewById(R.id.profile_author_name);
            pName.setText("iandouglas736");

        } else if (whichScreen == "tabs") {
            Log.i("MainActivity", "tab layout");
            setContentView(R.layout.fragment_feed);
        } else if (whichScreen == "" || whichScreen == "global") {
            Log.i("MainActivity", "global feed");
            // if so, show the feeds
            setContentView(R.layout.activity_loadfeed);
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.container, new FeedsFragment(this))
//                    .commit();
            oldGetGlobalFeed();
        } else {
            Log.i("MainActivity", "else");
            setContentView(R.layout.activity_loadfeed);
            oldGetGlobalFeed();
        }
    }


    private void oldGetGlobalFeed() {

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        Log.i("getGlobalFeed", "prep done to do get() call");

        StreamBackendClient.get(
                this,
                "/testfeed",
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONObject j;
                        ArrayList<FeedItem> feedArray = new ArrayList<FeedItem>();
                        FeedItemAdapter feedAdapter = new FeedItemAdapter(MainActivity.this, feedArray);

                        try {
                            JSONArray data = response.getJSONArray("feed");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    Log.i("onSuccess", "adding item");
                                    feedAdapter.add(new FeedItem(data.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        feedList = (ListView) findViewById(R.id.list_globalfeeditems);
                        feedList.setAdapter(feedAdapter);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        Log.i("getGlobalFeed", "onFailure");
                    }
                });
    }
    private void oldGetProfileFeed() {

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        Log.i("getGlobalFeed", "prep done to do get() call");

        StreamBackendClient.get(
                this,
                "/testfeed",
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.i("onSuccess", "!");
                        JSONObject j;
                        ArrayList<FeedItem> feedArray = new ArrayList<FeedItem>();
                        FeedItemAdapter feedAdapter = new FeedItemAdapter(MainActivity.this, feedArray);

                        try {
                            JSONArray data = response.getJSONArray("feed");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    Log.i("onSuccess", "adding item");
                                    FeedItem item = new FeedItem(data.getJSONObject(i));
                                    item.setAuthorEmail("");
                                    item.setAuthorName("");
                                    item.setCreatedDate("");
                                    feedAdapter.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        feedList = (ListView) findViewById(R.id.list_profile_feed);
                        feedList.setAdapter(feedAdapter);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        Log.i("getGlobalFeed", "onFailure");
                    }
                });
    }
}
