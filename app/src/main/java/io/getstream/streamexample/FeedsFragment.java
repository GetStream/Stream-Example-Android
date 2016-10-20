package io.getstream.streamexample;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.getstream.streamexample.adapters.FeedItemAdapter;
import io.getstream.streamexample.adapters.FeedsAdapter;
import io.getstream.streamexample.clients.StreamBackendClient;
import io.getstream.streamexample.models.FeedItem;

public class FeedsFragment extends Fragment {
    private Context myContext;
    private FeedsAdapter mFeedsAdapter;

    private List<FeedItem> feedList;
    public String toastString;
    private Toast toast;

    public FeedsFragment() {
        // you don't want this one
    }

    public FeedsFragment(Context context) {
        myContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        toast = new Toast(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFeedsAdapter = new FeedsAdapter(getActivity(), feedList);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.list_globalfeeditems);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        Log.i("getGlobalFeed", "prep done to do get() call");

        StreamBackendClient.get(
                myContext,
                "/testfeed",
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
                                    Log.i("onSuccess", "adding item");
                                    feedAdapter.add(new FeedItem(data.getJSONObject(i)));
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
                        Log.i("getGlobalFeed", "onFailure");
                    }
                });

        listView.setAdapter(mFeedsAdapter);
        return rootView;
    }

}

