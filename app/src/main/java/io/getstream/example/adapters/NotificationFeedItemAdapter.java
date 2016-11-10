package io.getstream.example.adapters;

import android.content.Context;
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
import io.getstream.example.models.NotificationFeedItem;

import static io.getstream.example.utils.Gravatar.md5;


public class NotificationFeedItemAdapter extends ArrayAdapter<NotificationFeedItem> {
    private Context myContext;
    private String myUUID = "" ;
    private Toast toast;

    private static class ViewHolder {
        TextView notification_message;
        ImageView photoImage;
        ImageView profileImage;
    }

    public NotificationFeedItemAdapter(Context context, ArrayList<NotificationFeedItem> notificationFeedItems) {
        super(context, R.layout.notification_listview_feeditem, notificationFeedItems);
        myContext = context;
        toast = new Toast(MyApplication.getAppContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationFeedItem feed_item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_listview_feeditem, parent, false);

            viewHolder.notification_message = (TextView) convertView.findViewById(R.id.feed_item_notification_message);
            viewHolder.photoImage = (ImageView) convertView.findViewById(R.id.feed_item_photo_image);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.feed_item_profile_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String gravatar_url ;
        String notificationMessage ;

        String verb = feed_item.getVerb();
        String photo_url = feed_item.getPhotoUrl();
        String past_tense_verb = " liked your photo" ;

        if (verb.equals("follow")) {
            past_tense_verb = " followed you" ;
        }

        String hash = md5(feed_item.getFirstEmail());
        gravatar_url = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";

        Integer follower_count = feed_item.getUsernames().size();

        notificationMessage = feed_item.getFirstUsername();
        if (follower_count > 1) {
            follower_count--;
            notificationMessage = notificationMessage + " and " + follower_count.toString();
            if (follower_count == 1) {
                notificationMessage = notificationMessage + " other";
            } else if (follower_count <= 3) {
                notificationMessage = notificationMessage + " others";
            } else {
                notificationMessage = notificationMessage + "+ others";
            }
        }
        notificationMessage = notificationMessage + past_tense_verb ;

        viewHolder.notification_message.setText(notificationMessage);

        if (photo_url != null && !photo_url.equals("")) {
            Picasso.with(myContext)
                    .load(photo_url)
                    .placeholder(R.drawable.no_image)
                    .into(viewHolder.photoImage);
        } else {
            viewHolder.photoImage.setVisibility(View.GONE);
        }

        Picasso.with(myContext)
                .load(gravatar_url)
                .placeholder(R.drawable.artist_placeholder)
                .into(viewHolder.profileImage);

        /*
        mBox = new TextView(context);
        mBox.setText(Html.fromHtml("<b>" + title + "</b>" +  "<br />" +
                    "<small>" + description + "</small>" + "<br />" +
                    "<small>" + DateAdded + "</small>"));
         */

        return convertView;
    }

    public int dpToPx(int dps) {
        final float scale = myContext.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);

        return pixels;
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
                                    toast = Toast.makeText(MyApplication.getAppContext(), toastPrefix+finalUsername, Toast.LENGTH_SHORT);
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
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    likeButton.setOnClickListener(new LikeClickListener("unlike", UUID));
                    break;
                case "unlike":
                    likePhoto(v, "unlike", this.UUID);
                    likeButton.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
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
                                    toast = Toast.makeText(MyApplication.getAppContext(), finalAction+"d", Toast.LENGTH_SHORT);
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