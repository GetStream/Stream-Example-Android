package io.getstream.example.utils;


import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.view.View;

import io.getstream.example.MyApplication;
import io.getstream.example.R;

public class NavUpdate {

    public static void UpdateNavByUUID(String authorID) {
        View rootView = ((Activity) MyApplication.getAppContext()).findViewById(android.R.id.content);
        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);

        if (authorID == "") {
            navigationView.getMenu().findItem(R.id.nav_my_feed).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_my_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_register).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(false);
        } else {
            navigationView.getMenu().findItem(R.id.nav_my_feed).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_my_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_register).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(true);
        }
    }
}
