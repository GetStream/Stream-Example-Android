package io.getstream.example.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import com.squareup.picasso.Picasso;

import io.getstream.example.R;
import io.getstream.example.fragments.AggregatedFeedFragment;
import io.getstream.example.fragments.GlobalFeedFragment;
import io.getstream.example.fragments.MyNotificationsFragment;
import io.getstream.example.fragments.MyTimelineFragment;
import io.getstream.example.fragments.UsersFragment;
import io.getstream.example.fragments.ProfileFragment;

import static io.getstream.example.utils.Gravatar.pickRandomAnimalAvatar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context myContext;
    private static final int CONST_ACTIVITY_REGISTER = 1;
    private static final int CONST_ACTIVITY_PHOTO = 2;

    private String title;
    private Fragment fragment;
    private Intent intent;

    private String mUserUUID;
    private String mUsername;
    private String mEmail;
    private String mGravatar;

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedprefsEditor;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myContext = this.getApplicationContext();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedprefsEditor = sharedPrefs.edit();

        mUserUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setNavByRegistered();
        setFABByRegistered();
        RefreshGlobalFeed();
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store something?
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        RefreshGlobalFeed();
    }

    @Override
    public void onResume() {
        super.onRestart();
        RefreshGlobalFeed();
    }

    private void RefreshGlobalFeed() {
        // relaunch the global feed activity
        getSupportActionBar().setTitle(getString(R.string.menu_global_feed));
        fragment = new GlobalFeedFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_container, fragment);
            ft.commit();
    }

    private void setFABByRegistered() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (mUserUUID != "") {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchActivity("photo", getApplicationContext());
                }
            });
        } else {
            fab.setVisibility(View.GONE);
            fab.setOnClickListener(null);
        }
    }
    private void setNavByRegistered() {
        TextView t1, t2;
        ImageView i;

        mUserUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
        mUsername = sharedPrefs.getString(getString(R.string.pref_author_username), getString(R.string.your_username_here));
        mEmail = sharedPrefs.getString(getString(R.string.pref_author_email), getString(R.string.your_email_address_com));
        mGravatar = sharedPrefs.getString(getString(R.string.pref_author_gravatar), "");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navMenu = navigationView.getMenu();
        View navHeader =  navigationView.getHeaderView(0);

        i = (ImageView) navHeader.findViewById(R.id.nav_profile_image);
        t1 = (TextView) navHeader.findViewById(R.id.nav_profile_email);
        t2 = (TextView) navHeader.findViewById(R.id.nav_profile_username);

        if (mUserUUID.length() == 0) {
            navMenu.findItem(R.id.nav_take_photo).setVisible(false);
            navMenu.findItem(R.id.nav_my_feed).setVisible(false);
            navMenu.findItem(R.id.nav_my_profile).setVisible(false);
            navMenu.findItem(R.id.nav_users).setVisible(false);
            navMenu.findItem(R.id.nav_notification_feed).setVisible(false);
            navMenu.findItem(R.id.nav_aggregated_feed).setVisible(false);
            navMenu.findItem(R.id.nav_register).setVisible(true);
            navMenu.findItem(R.id.nav_sign_out).setVisible(false);
            i.setImageResource(android.R.drawable.sym_def_app_icon);
            t1.setText(getString(R.string.your_username_here));
            t2.setText(getString(R.string.your_email_address_com));
        } else {
            navMenu.findItem(R.id.nav_take_photo).setVisible(true);
            navMenu.findItem(R.id.nav_my_feed).setVisible(true);
            navMenu.findItem(R.id.nav_my_profile).setVisible(true);
            navMenu.findItem(R.id.nav_users).setVisible(true);
            navMenu.findItem(R.id.nav_notification_feed).setVisible(true);
            navMenu.findItem(R.id.nav_aggregated_feed).setVisible(true);
            navMenu.findItem(R.id.nav_register).setVisible(false);
            navMenu.findItem(R.id.nav_sign_out).setVisible(true);
            if (mGravatar.length() > 0) {
                Picasso.with(this)
                        .load(mGravatar)
                        .placeholder(pickRandomAnimalAvatar())
                        .into(i);
            }
            t1.setText(mEmail);
            t2.setText(mUsername);
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
        RefreshGlobalFeed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_take_photo:
                title = getString(R.string.menu_take_photo);
                launchActivity("photo", this);
                break;

//            default:
            case R.id.nav_global_feed:
                // Handle the camera action
                title = getString(R.string.menu_global_feed);
                fragment = new GlobalFeedFragment();
                break;

            case R.id.nav_my_feed:
                title = getString(R.string.menu_my_timeline);
                fragment = new MyTimelineFragment();
                break;

            case R.id.nav_users:
                title = getString(R.string.menu_users);
                fragment = new UsersFragment();
                break;

            case R.id.nav_my_profile:
                title = getString(R.string.menu_my_profile);
                fragment = new ProfileFragment();
                break;

            case R.id.nav_notification_feed:
                title = getString(R.string.menu_notification_feed);
                fragment = new MyNotificationsFragment();
                break;

            case R.id.nav_aggregated_feed:
                title = getString(R.string.menu_aggregated_feed);
                fragment = new AggregatedFeedFragment();
                break;

            case R.id.nav_register:
                title = getString(R.string.menu_register);
                launchActivity("register_main_layout", this);
                break;

            case R.id.nav_sign_out:
                sharedprefsEditor.putString(getString(R.string.pref_authorid), "");
                sharedprefsEditor.commit();
                title = getString(R.string.menu_global_feed);
                fragment = new GlobalFeedFragment();
                setNavByRegistered();
                setFABByRegistered();
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
        intent = null;
        int requestCode = 0;

        if (activity.equals("register_main_layout")) {
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
        switch (requestCode) {
            case CONST_ACTIVITY_PHOTO:
//                Log.i("main", "returned from taking a photo");
                break;
            case CONST_ACTIVITY_REGISTER:
//                Log.i("main", "returned from registering");
                setNavByRegistered();
                setFABByRegistered();
                break;
        }
    }
}
