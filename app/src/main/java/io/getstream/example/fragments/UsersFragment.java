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
import io.getstream.example.adapters.UserAdapter;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.models.User;

public class UsersFragment extends Fragment {
    public Context myContext;
    private UserAdapter mUserAdapter;
    private String myUUID = "" ;

    private List<User> userList;
    public String toastString;
    private Toast toast;
    private SharedPreferences sharedPrefs;


    public UsersFragment() {
        myContext = MyApplication.getAppContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = new Toast(getActivity().getApplicationContext());
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        myUUID = sharedPrefs.getString(getString(R.string.pref_authorid), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUserAdapter = new UserAdapter(getActivity(), userList);
        View rootView = inflater.inflate(R.layout.users_main_layout, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.list_users);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));

        StreamBackendClient.get(
                myContext,
                "/users?myUUID=" + myUUID,
                headers.toArray(new Header[headers.size()]),
                null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONObject j;
                        ArrayList<User> userArray = new ArrayList<User>();
                        UserAdapter userAdapter = new UserAdapter(myContext, userArray);

                        try {
                            JSONArray data = response.getJSONArray("users");

                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    userAdapter.add(new User(data.getJSONObject(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listView.setAdapter(userAdapter);
                    }

                    public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                        // TODO handle failure here
                    }
                });

//        listView.setAdapter(mUserAdapter);
        return rootView;
    }
}

