package io.getstream.example;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.getstream.example.adapters.FeedItemAdapter;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.fragments.GlobalFeedFragment;
import io.getstream.example.fragments.MyFeedFragment;
import io.getstream.example.fragments.UsersFragment;
import io.getstream.example.models.FeedItem;

import static io.getstream.example.utils.Gravatar.md5;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CONST_ACTIVITY_REGISTER = 1;
    private static final int CONST_ACTIVITY_PHOTO = 2;

    private ListView feedList;
    private String title;
    private Fragment fragment;
    private Intent intent;
    private String mUserUUID;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedprefsEditor;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedprefsEditor = sharedPrefs.edit();

        mUserUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
        Log.i("main-onCreate", "mUserUUID from shared prefs: " + mUserUUID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), PhotoIntentActivity.class);
                startActivityForResult(intent, CONST_ACTIVITY_PHOTO);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setNavByRegistered();

        // default view is global feed
        title = getString(R.string.menu_global_feed);
        fragment = new GlobalFeedFragment(getApplicationContext());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment);
        ft.commit();
        getSupportActionBar().setTitle(title);
    }

    private void setNavByRegistered() {
        mUserUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
        Log.i("setNav", "mUserUUID from shared prefs: " + mUserUUID);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navMenu = navigationView.getMenu();

        if (mUserUUID.length() == 0) {
            navMenu.findItem(R.id.nav_take_photo).setVisible(false);
            navMenu.findItem(R.id.nav_my_feed).setVisible(false);
            navMenu.findItem(R.id.nav_my_profile).setVisible(false);
            navMenu.findItem(R.id.nav_register).setVisible(true);
            navMenu.findItem(R.id.nav_sign_out).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_take_photo).setVisible(true);
            navMenu.findItem(R.id.nav_my_feed).setVisible(true);
            navMenu.findItem(R.id.nav_my_profile).setVisible(true);
            navMenu.findItem(R.id.nav_register).setVisible(false);
            navMenu.findItem(R.id.nav_sign_out).setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Log.i("main", "************* nav create");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            default:
            case R.id.nav_global_feed:
                // Handle the camera action
                Log.i("Main-onNavSelected", getString(R.string.menu_global_feed));
                title = getString(R.string.menu_global_feed);
                fragment = new GlobalFeedFragment(getApplicationContext());
                break;

            case R.id.nav_my_feed:
                Log.i("Main-onNavSelected", getString(R.string.menu_my_feed));
                title = getString(R.string.menu_my_feed);
                fragment = new MyFeedFragment(getApplicationContext());
                break;

            case R.id.nav_users:
                Log.i("Main-onNavSelected", getString(R.string.menu_users));
                title = getString(R.string.menu_users);
                fragment = new UsersFragment(getApplicationContext());
                break;

            case R.id.nav_my_profile:
                Log.i("Main-onNavSelected", getString(R.string.menu_my_profile));
                title = getString(R.string.menu_my_profile);
                break;

            case R.id.nav_register:
                Log.i("Main-onNavSelected", getString(R.string.menu_register));
                title = getString(R.string.menu_register);
                launchActivity("register", this);
                Log.i("main", "finished register activity");
                break;

            case R.id.nav_take_photo:
                Log.i("Main-onNavSelected", getString(R.string.menu_takephoto));
                title = getString(R.string.menu_takephoto);
                launchActivity("photo", this);
                Log.i("main", "finished register activity");
                break;

            case R.id.nav_sign_out:
                Log.i("Main-onNavSelected", getString(R.string.menu_sign_out));
                sharedprefsEditor.putString(getString(R.string.pref_authorid), "");
                sharedprefsEditor.commit();
                title = getString(R.string.menu_global_feed);
                fragment = new GlobalFeedFragment(getApplicationContext());
                setNavByRegistered();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_container, fragment);
            ft.commit();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void launchActivity(String activity, Context context) {
        Intent intent = null;
        int requestCode = 0;

        if (activity.equals("register")) {
            requestCode = CONST_ACTIVITY_REGISTER;
            intent = new Intent(context, RegisterActivity.class);
        }
        if (activity.equals("photo")) {
            requestCode = CONST_ACTIVITY_PHOTO;
            intent = new Intent(context, PhotoIntentActivity.class);
        }

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);

        if (intent != null) {
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("main", "main activity is resuming");
        Log.i("main-req", Integer.toString(requestCode));
        Log.i("main-res", Integer.toString(resultCode));

        switch (requestCode) {
            case CONST_ACTIVITY_PHOTO:
                Log.i("main", "returned from taking a photo");
                break;
            case CONST_ACTIVITY_REGISTER:
                Log.i("main", "returned from registering");
                setNavByRegistered();
                break;
        }
    }

    private void whichScreen(Bundle savedInstanceState) {
        String whichScreen;

        // is userlist_user registered already?
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        // global feed
        whichScreen = "";
//        whichScreen = "register";
//        whichScreen = "tabs";
//        whichScreen = "feed_item";
//        whichScreen = "user_profile";

//        String authorID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
//        String authorEmail = sharedPrefs.getString(getString(R.string.pref_authoremail), "");
//        String authorUsername = sharedPrefs.getString(getString(R.string.pref_authorname), "");

        if (whichScreen == "register") {
            setContentView(R.layout.register);
        } else if (whichScreen == "activity_listusers") {
            setContentView(R.layout.activity_listusers);
        } else if (whichScreen == "feed_item") {
            getSupportActionBar().setTitle("one feed item");
            setContentView(R.layout.feed_item);
        } else if (whichScreen == "user_profile") {
            getSupportActionBar().setTitle(getString(R.string.menu_my_profile));
            setContentView(R.layout.activity_user);
            oldGetProfileFeed();

            String hash = md5("ian.douglas@iandouglas.com");
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Log.i("gravatar url", gravatarUrl);
            Picasso.with(MainActivity.this)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.artist_placeholder)
                    .into((ImageView) this.findViewById(R.id.profile_profile_image));
            TextView pName = (TextView) this.findViewById(R.id.profile_author_name);
            pName.setText("iandouglas736");

        } else if (whichScreen == "" || whichScreen == "global") {
            // if so, show the feeds
            getSupportActionBar().setTitle(getString(R.string.menu_global_feed));
            setContentView(R.layout.activity_loadfeed);
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.container, new GlobalFeedFragment(this))
//                    .commit();
            oldGetGlobalFeed();
        } else {
            getSupportActionBar().setTitle(getString(R.string.menu_global_feed));
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
                "/feed/global?uuid=" + mUserUUID,
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONObject j;
                        ArrayList<FeedItem> feedArray = new ArrayList<FeedItem>();
                        FeedItemAdapter feedAdapter = new FeedItemAdapter(MainActivity.this, feedArray);

                        try {
                            JSONArray data = response.getJSONObject("feed").getJSONArray("results");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    feedAdapter.add(new FeedItem(data.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        feedList = (ListView) findViewById(R.id.list_globalfeed);
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
                "/feed/global?uuid=" + mUserUUID,
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
                            JSONArray data = response.getJSONObject("feed").getJSONArray("results");

                            for (int i = 0; i < data.length(); i++) {
                                try {
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
