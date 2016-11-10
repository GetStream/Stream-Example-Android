package io.getstream.example.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
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
        FeedItem feedItem;
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
            viewHolder.feedItem = feed_item;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.author_name.setText(feed_item.getAuthorName());

        String createdStr = (String) DateUtils.getRelativeDateTimeString(myContext, (long) feed_item.getCreatedDate()*1000, DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0);
        viewHolder.created_date.setText(createdStr);

        viewHolder.photoLikeCount.setText(Integer.toString(feed_item.getPhotoLikes()) + " likes");

        authorUUID = feed_item.getAuthorId();

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

        Boolean iFollowAuthor = feed_item.getIFollowAuthor();
        Boolean iLikePhoto = feed_item.getILikePhoto();

        if (myUUID.equals("")) {
            // no stars lit up if you're not logged in
            viewHolder.btnLikePhoto.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            // no click handler
            viewHolder.btnLikePhoto.setOnClickListener(null);
        } else {
            if (authorUUID.equals(myUUID)) {
                // cannot like your own photos
                viewHolder.btnLikePhoto.setOnClickListener(null);
            } else {
                if (iLikePhoto) {
                    // turn star on since we like this already
                    viewHolder.btnLikePhoto.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    // set click handler to unlike
                    viewHolder.btnLikePhoto.setOnClickListener(
                            new LikeClickListener("unlike", viewHolder)
                    );
                } else {
                    // turn star off since we don't like this yet
                    viewHolder.btnLikePhoto.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    // set click handler to like
                    viewHolder.btnLikePhoto.setOnClickListener(
                            new LikeClickListener("like", viewHolder)
                    );
                }
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

        convertView.setTag(viewHolder);
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
        ViewHolder viewHolder;

        public LikeClickListener(String action, ViewHolder viewHolder) {
            this.Action = action;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            Integer alterCountView = 0;
            String UUID = this.viewHolder.feedItem.getPhotoUUID();

//            Button likeButton = (Button) this.viewHolder.btnLikePhoto; //findViewById(R.id.feeditem_like_button);
//            TextView likeCount = (TextView) v.findViewById(R.id.feed_item_likes);
//            Boolean tvnull = likeCount == null;
//            Log.i("clk-tvnull", tvnull.toString());

            Boolean iLikePhotoNow = true ;

            switch (this.Action) {
                default:
                    // should never get here
                    break;
                case "like":
                    likePhoto(v, "like", UUID);
                    this.viewHolder.btnLikePhoto.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    this.viewHolder.btnLikePhoto.setOnClickListener(new LikeClickListener("unlike", this.viewHolder));
                    alterCountView = 1;
                    break;
                case "unlike":
                    likePhoto(v, "unlike", UUID);
                    this.viewHolder.btnLikePhoto.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    this.viewHolder.btnLikePhoto.setOnClickListener(new LikeClickListener("like", this.viewHolder));
                    alterCountView = -1;
                    iLikePhotoNow = false;
                    break;
            }

            if (alterCountView != 0) {
                Integer newLikes = this.viewHolder.feedItem.getPhotoLikes() + alterCountView ;
                if (newLikes < 0) {
                    newLikes = 0;
                }
                String likesString = newLikes.toString() + " like";
                if (newLikes != 1) {
                    likesString += "s" ;
                }
                this.viewHolder.feedItem.setPhotoLikes(newLikes);
                this.viewHolder.feedItem.setILikePhoto(iLikePhotoNow);
                this.viewHolder.photoLikeCount.setText(likesString);
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
                            toast = Toast.makeText(MyApplication.getAppContext(), "there was an error, try again or contact support", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
        }
    }
}