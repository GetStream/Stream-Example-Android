package io.getstream.example.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.getstream.example.MyApplication;
import io.getstream.example.R;
import io.getstream.example.adapters.FeedItemAdapter;
import io.getstream.example.adapters.FeedsAdapter;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.models.FeedItem;

import static io.getstream.example.utils.Gravatar.md5;
import static io.getstream.example.utils.Gravatar.pickRandomAnimalAvatar;

public class ProfileFragment extends Fragment {
    private Context myContext;
    private String mUserUUID;
    private Toast toast;
    private FeedsAdapter mFeedsAdapter;
    private List<FeedItem> feedList;

    public ProfileFragment() {
        myContext = MyApplication.getAppContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = new Toast(getActivity().getApplicationContext());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mFeedsAdapter = new FeedsAdapter(getActivity(), feedList);
        final View rootView = inflater.inflate(R.layout.profile_main_layout, container, false);

        fetchProfile();
        fetchProfileFeed(rootView);

        return rootView;
    }

    private void fetchProfileFeed(View rootView) {
        mFeedsAdapter = new FeedsAdapter(getActivity(), feedList);

        final ListView listView = (ListView) rootView.findViewById(R.id.list_myprofile_feed);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        StreamBackendClient.get(
                myContext,
                "/feed/user/" + mUserUUID + "?myUUID=" + mUserUUID,
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONObject j;

                        ArrayList<FeedItem> feedArray = new ArrayList<FeedItem>();
                        FeedItemAdapter feedAdapter = new FeedItemAdapter(myContext, feedArray);

                        try {
                            JSONArray data = response.getJSONArray("feed");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    feedAdapter.add(new FeedItem(data.getJSONObject(i), true));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listView.setAdapter(feedAdapter);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        // TODO handle failure here
                    }
                });

    }
    private void fetchProfile() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        StreamBackendClient.get(
                myContext,
                "/profilestats/" + mUserUUID,
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Integer followers = response.getInt("followers");
                            Integer following = response.getInt("following");
                            Integer photos = response.getInt("photos");
                            String username = response.getString("username");
                            String email = response.getString("email");

                            TextView t;
                            t = (TextView) getActivity().findViewById(R.id.profile_author_name);
                            t.setText(username);
                            t = (TextView) getActivity().findViewById(R.id.profile_follower_count);
                            t.setText(String.format(Locale.US, "%1$d\nFollowers", followers));
                            t = (TextView) getActivity().findViewById(R.id.profile_following_count);
                            t.setText(String.format(Locale.US, "%1$d\nFollowing", following));
                            t = (TextView) getActivity().findViewById(R.id.profile_photo_count);
                            t.setText(String.format(Locale.US, "%1$d\nPosts", photos));

                            ImageView i = (ImageView) getActivity().findViewById(R.id.profile_avatar);
                            String hash = md5(email);
                            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";

                            Picasso.with(myContext)
                                    .load(gravatarUrl)
                                    .placeholder(pickRandomAnimalAvatar())
                                    .into(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        // TODO handle failure here
                    }
                });
    }
}
