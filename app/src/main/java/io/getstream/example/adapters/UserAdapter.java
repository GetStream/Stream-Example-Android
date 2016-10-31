package io.getstream.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.getstream.example.MainActivity;
import io.getstream.example.MyApplication;
import io.getstream.example.PhotoIntentActivity;
import io.getstream.example.R;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.models.User;

import static io.getstream.example.utils.Gravatar.md5;

public class UserAdapter extends ArrayAdapter<User> {
    Context myContext;
    private SharedPreferences sharedPrefs;
    private String myUUID = "" ;
    private Toast toast;

    private static class ViewHolder {
        TextView username;
        ImageView profileImage;
        Button followButton;
    }

    public UserAdapter(Context context, List<User> users) {
        super(context, R.layout.userlist_user, users);
        this.myContext = context;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        myUUID = sharedPrefs.getString(context.getString(R.string.pref_authorid), "");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        UserAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new UserAdapter.ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.userlist_user, parent, false);

            viewHolder.username = (TextView) convertView.findViewById(R.id.userlist_author_name);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.userlist_profile_image);
            viewHolder.followButton = (Button) convertView.findViewById(R.id.user_follow_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UserAdapter.ViewHolder) convertView.getTag();
        }

        Log.i("userlist_user object", user.getUsername());
        String isnull = "true";
        if (viewHolder.username != null) {
            isnull = "false";
        }
        Log.i("is viewholder null", isnull);
        viewHolder.username.setText(user.getUsername());

        String hash = md5(user.getEmail());
        String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
        Log.i("gravatar url", gravatarUrl);
        Picasso.with(myContext)
                .load(gravatarUrl)
                .placeholder(R.drawable.artist_placeholder)
                .into(viewHolder.profileImage);

        if (user.getUUID().equals(myUUID)) {
            // hide follow button completely
            viewHolder.followButton.setVisibility(View.GONE);
        } else {
            Log.i("user doifollow", user.getDoIFollow() ? "true" : "false");
            if (user.getDoIFollow()) {
                // set follow button text to 'unfollow' and set click handler appropriately
                viewHolder.followButton.setText(R.string.user_unfollow);
                viewHolder.followButton.setOnClickListener(
                        new FollowClickListener(R.string.user_unfollow, user.getUsername(), user.getUUID())
                );
            } else {
                // set follow button text to 'follow' and set click handler appropriately
                viewHolder.followButton.setText(R.string.user_follow);
                viewHolder.followButton.setOnClickListener(
                        new FollowClickListener(R.string.user_follow, user.getUsername(), user.getUUID()));
            }
        }
        return convertView;
    }

    public class FollowClickListener implements View.OnClickListener {
        int Action;
        String Username;
        String UUID;

        public FollowClickListener(int resActionString, String username, String UUID) {
            this.Action = resActionString;
            this.Username = username;
            this.UUID = UUID;
        }

        @Override
        public void onClick(View v) {
            Boolean success;

            Log.i("follow-click", "onClick called");
            Log.i("follow-click", Integer.toString(this.Action));
            Button followButton = (Button) v.findViewById(R.id.user_follow_button);

            switch (this.Action) {
                default:
                    Log.i("followBtn.click", "no idea what you're doing with " + this.Username);
                    break;
                case R.string.user_follow:
                    Log.i("followBtn.click", "following " + this.Username);
                    followUser(v, "follow", this.Username, this.UUID);
                    followButton.setText(MyApplication.getAppContext().getString(R.string.user_unfollow));
                    followButton.setOnClickListener(
                            new FollowClickListener(R.string.user_unfollow, this.Username, this.UUID));
                    break;
                case R.string.user_unfollow:
                    Log.i("followBtn.click", "unfollowing " + this.Username);
                    followUser(v, "unfollow", this.Username, this.UUID);
                    followButton.setText(MyApplication.getAppContext().getString(R.string.user_follow));
                    followButton.setOnClickListener(
                            new FollowClickListener(R.string.user_follow, this.Username, this.UUID));
                    break;
            }
        }

        private void followUser(View v, String action, String username, String uuid) {
            final String finalAction = action;
            final String finalUsername = username;
            final String finalUUID = uuid;

            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Accept", "application/json"));

            StreamBackendClient.get(
                    myContext,
                    "/" + action + "/" + uuid + "?uuid=" + myUUID,
                    headers.toArray(new Header[headers.size()]),
                    null,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String data = response.getString("status");
                                Log.i("user " + finalAction, "data: " + data);
                                if (data.equals("success")) {
                                    Log.i("returnstatus", "true");

                                    String toastPrefix = "now following ";
                                    if (finalAction.equals("unfollow")) {
                                        toastPrefix = "no longer following ";
                                    }
                                    toast = Toast.makeText(MyApplication.getAppContext(), toastPrefix+finalUsername, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                            Log.i("getGlobalFeed", "onFailure");
                            // TODO should handle error conditions
                            Log.i("follow", "failure");
                        }
                    });
        }
    }
}