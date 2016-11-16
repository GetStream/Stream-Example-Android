package io.getstream.example.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import io.getstream.example.adapters.FeedItemAdapter;
import io.getstream.example.adapters.FeedsAdapter;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.models.FeedItem;

public class MyTimelineFragment extends Fragment {
    private Context myContext;
    private FeedsAdapter mFeedsAdapter;
    private String mUserUUID;

    private List<FeedItem> feedList;
    public String toastString;
    private Toast toast;

    public MyTimelineFragment() {
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
        mFeedsAdapter = new FeedsAdapter(getActivity(), feedList);
        View rootView = inflater.inflate(R.layout.timeline_main_layout, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.list_myfeed);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        StreamBackendClient.get(
                myContext,
                "/feed/timeline/" + mUserUUID,
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
                        // TODO handle failure here
                    }
                });

//        listView.setAdapter(mFeedsAdapter);
        return rootView;
    }
}
