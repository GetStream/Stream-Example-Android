package io.getstream.example.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import io.getstream.example.MyApplication;
import io.getstream.example.R;
import io.getstream.example.clients.StreamBackendClient;
import io.getstream.example.models.FeedItem;

import static io.getstream.example.utils.Gravatar.md5;
import static io.getstream.example.utils.Gravatar.pickRandomAnimalAvatar;


public class FeedItemAdapter extends ArrayAdapter<FeedItem> {
    private Context myContext;
    private String myUUID = "" ;
    private String authorUUID;
    private SharedPreferences sharedPrefs;
    private Toast toast;

    private static class ViewHolder {
        TextView author_name;
        TextView created_date;
        ImageView photoImage;
        ImageView profileImage;
        TextView photoLikeCount;
        Button btnLikePhoto;
        Button btnFollowAuthor;
    }

    public FeedItemAdapter(Context context, ArrayList<FeedItem> feedItems) {
        super(context, R.layout.primary_feed_item, feedItems);
        myContext = context;
        toast = new Toast(MyApplication.getAppContext());
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        myUUID = sharedPrefs.getString(myContext.getString(R.string.pref_authorid), "");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem feed_item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.primary_feed_item, parent, false);

            viewHolder.author_name = (TextView) convertView.findViewById(R.id.feed_item_author_name);
            viewHolder.created_date = (TextView) convertView.findViewById(R.id.feed_item_created_date);
            viewHolder.photoImage = (ImageView) convertView.findViewById(R.id.feed_item_photo_image);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.feed_item_profile_image);
            viewHolder.photoLikeCount = (TextView) convertView.findViewById(R.id.feed_item_likes);
            viewHolder.btnLikePhoto = (Button) convertView.findViewById(R.id.feeditem_like_button);
            viewHolder.btnFollowAuthor = (Button) convertView.findViewById(R.id.feed_follow_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.author_name.setText(feed_item.getAuthorName());
        viewHolder.created_date.setText(feed_item.getCreatedDate());
        viewHolder.photoLikeCount.setText(Integer.toString(feed_item.getPhotoLikes()) + " likes");

        authorUUID = feed_item.getAuthorId();

        Boolean iFollowAuthor = feed_item.getIFollowAuthor();
        Boolean iLikePhoto = feed_item.getILikePhoto();

        if (myUUID.equals("")) {
            // no stars lit up if you're not logged in
            viewHolder.btnLikePhoto.setBackgroundResource(android.R.drawable.btn_star_big_off);
        } else {
            if (iLikePhoto) {
                // turn star on since we like this already
                viewHolder.btnLikePhoto.setBackgroundResource(android.R.drawable.btn_star_big_on);
                // set click handler to unlike
                viewHolder.btnLikePhoto.setOnClickListener(
                        new LikeClickListener("unlike", feed_item.getPhotoUUID())
                );
            } else {
                // turn star off since we don't like this yet
                viewHolder.btnLikePhoto.setBackgroundResource(android.R.drawable.btn_star_big_off);
                // set click handler to like
                viewHolder.btnLikePhoto.setOnClickListener(
                        new LikeClickListener("like", feed_item.getPhotoUUID())
                );
            }
        }

        if (myUUID.equals("")) {
            // no follow/unfollow buttons if you're not logged in
            viewHolder.btnFollowAuthor.setVisibility(View.GONE);
        } else {
            if (authorUUID.equals(myUUID)) {
                viewHolder.btnFollowAuthor.setVisibility(View.GONE);
            } else {
                if (iFollowAuthor) {
                    viewHolder.btnFollowAuthor.setText(R.string.user_unfollow);
                    viewHolder.btnFollowAuthor.setOnClickListener(
                            new FollowClickListener(R.string.user_unfollow, feed_item.getAuthorName(), feed_item.getAuthorId())
                    );
                } else {
                    viewHolder.btnFollowAuthor.setText(R.string.user_follow);
                    viewHolder.btnFollowAuthor.setOnClickListener(
                            new FollowClickListener(R.string.user_follow, feed_item.getAuthorName(), feed_item.getAuthorId())
                    );
                }
            }
        }

        Picasso.with(myContext)
                .load(feed_item.getPhotoUrl())
                .placeholder(R.drawable.no_image)
                .into(viewHolder.photoImage);

        if (!feed_item.getSupressGravatar()) {
            String hash = md5(feed_item.getAuthorEmail());
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(myContext)
                    .load(gravatarUrl)
                    .placeholder(pickRandomAnimalAvatar())
                    .into(viewHolder.profileImage);
        } else {
            viewHolder.profileImage.setVisibility(View.GONE);
            viewHolder.author_name.setVisibility(View.GONE);
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

            Button followButton = (Button) v.findViewById(R.id.feed_follow_button);

            switch (this.Action) {
                default:
                    // should never get here
                    break;
                case R.string.user_follow:
                    followUser(v, "follow", this.Username, this.UUID);
                    followButton.setText(MyApplication.getAppContext().getString(R.string.user_unfollow));
                    followButton.setOnClickListener(
                            new FollowClickListener(R.string.user_unfollow, this.Username, this.UUID));
                    break;
                case R.string.user_unfollow:
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
                    "/" + action + "/" + uuid + "?myUUID=" + myUUID,
                    headers.toArray(new Header[headers.size()]),
                    null,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String data = response.getString("status");
                                if (data.equals("success")) {
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
                            // TODO should handle error conditions
                        }
                    });
        }
    }
    public class LikeClickListener implements View.OnClickListener {
        String Action;
        String UUID;

        public LikeClickListener(String action, String UUID) {
            this.Action = action;
            this.UUID = UUID;
        }

        @Override
        public void onClick(View v) {
            Boolean success;

            Button likeButton = (Button) v.findViewById(R.id.feeditem_like_button);

            switch (this.Action) {
                default:
                    // should never get here
                    break;
                case "like":
                    likePhoto(v, "like", this.UUID);
                    likeButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    likeButton.setOnClickListener(new LikeClickListener("unlike", UUID));
                    break;
                case "unlike":
                    likePhoto(v, "unlike", this.UUID);
                    likeButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    likeButton.setOnClickListener(new LikeClickListener("like", UUID));
                    break;
            }
        }

        private void likePhoto(View v, String action, String uuid) {
            final String finalAction = action;
            final String finalUUID = uuid;

            List<Header> headers = new ArrayList<Header>();
            headers.add(new BasicHeader("Accept", "application/json"));

            StreamBackendClient.get(
                    myContext,
                    "/" + action + "photo/" + uuid + "?myUUID=" + myUUID,
                    headers.toArray(new Header[headers.size()]),
                    null,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String data = response.getString("status");
                                if (data.equals("success")) {
                                    toast = Toast.makeText(MyApplication.getAppContext(), finalAction+"d", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onFailure(int statusCode, Header[] headers, JSONArray response) {
                            // TODO should handle error conditions
                        }
                    });
        }
    }
}