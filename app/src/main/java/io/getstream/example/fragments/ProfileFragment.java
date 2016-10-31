package io.getstream.example.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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
        // Required empty public constructor, you don't want this one
    }
    public ProfileFragment(Context context) {
        myContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toast = new Toast(getActivity().getApplicationContext());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
        Log.i("getProfile-onCreate", "mUserUUID from shared prefs: " + mUserUUID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mFeedsAdapter = new FeedsAdapter(getActivity(), feedList);
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        fetchProfile();
        fetchProfileFeed(rootView);

        return rootView;
    }

    private void fetchProfileFeed(View rootView) {
        mFeedsAdapter = new FeedsAdapter(getActivity(), feedList);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_myfeed);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        Log.i("getUserFeed", "prep done to do get() call");

        StreamBackendClient.get(
                myContext,
                "/feed/" + mUserUUID + "?uuid=" + mUserUUID,
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
                                    feedAdapter.add(new FeedItem(data.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (data.length() == 0) {
                                String toastContent = "You have no items in your feed, try following others!";
                                Toast toast = Toast.makeText(getActivity(), toastContent, Toast.LENGTH_LONG);
                                toast.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listView.setAdapter(feedAdapter);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        Log.i("getUserFeed", "onFailure");
                    }
                });
    }
    private void fetchProfile() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        Log.i("getProfile", "prep done to do get() call");
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
                            Log.i("gravatar url", gravatarUrl);
                            Picasso.with(myContext)
                                    .load(gravatarUrl)
                                    .placeholder(pickRandomAnimalAvatar())
                                    .into(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        Log.i("getProfile", "onFailure");
                    }
                });
    }
}
