package io.getstream.example.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.getstream.example.MyApplication;
import io.getstream.example.R;
import io.getstream.example.adapters.NotificationFeedAdapter;
import io.getstream.example.adapters.NotificationFeedItemAdapter;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.models.NotificationFeedItem;

public class MyNotificationsFragment extends Fragment {

    private Context myContext;
    private NotificationFeedAdapter mFeedAdapter;
    private String mUserUUID;

    private List<NotificationFeedItem> feedList;
    public String toastString;
    private Toast toast;

    public MyNotificationsFragment() {
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
        mFeedAdapter = new NotificationFeedAdapter(getActivity(), feedList);
        View rootView = inflater.inflate(R.layout.notification_main_layout, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.list_notifications_feed);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        StreamBackendClient.get(
                myContext,
                "/feed/notifications?myUUID=" + mUserUUID,
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONObject j;
                        ArrayList<NotificationFeedItem> feedArray = new ArrayList<NotificationFeedItem>();
                        NotificationFeedItemAdapter feedAdapter = new NotificationFeedItemAdapter(myContext, feedArray);

                        try {
                            JSONArray data = response.getJSONArray("feed");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    feedAdapter.add(new NotificationFeedItem(data.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (data.length() == 0) {
                                String toastContent = "You have no items in your notification feed yet";
                                Toast toast = Toast.makeText(getActivity(), toastContent, Toast.LENGTH_SHORT);
                                toast.show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listView.setAdapter(feedAdapter);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        // TODO handle failures
                    }
                });

//        listView.setAdapter(mFeedsAdapter);
        return rootView;
    }
}
