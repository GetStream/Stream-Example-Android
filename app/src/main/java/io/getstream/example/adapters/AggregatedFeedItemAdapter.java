package io.getstream.example.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.getstream.example.MyApplication;
import io.getstream.example.R;
import io.getstream.example.models.AggregatedFeedItem;

import static io.getstream.example.utils.Gravatar.md5;


public class AggregatedFeedItemAdapter extends ArrayAdapter<AggregatedFeedItem> {
    private Context myContext;
    private Toast toast;

    private static class ViewHolder {
        TextView notification_message;
        TextView created_date ;
        GridView gridView;
        ImageView profileImage;
    }

    public AggregatedFeedItemAdapter(Context context, ArrayList<AggregatedFeedItem> aggregatedFeedItems) {
        super(context, R.layout.notification_listview_feeditem, aggregatedFeedItems);
        myContext = context;
        toast = new Toast(MyApplication.getAppContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String gravatar_url ;
        String notificationMessage ;
        ViewHolder viewHolder;

        AggregatedFeedItem feed_item = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.aggregated_listview_feeditem, parent, false);

            viewHolder.created_date = (TextView) convertView.findViewById(R.id.aggregated_date);
            viewHolder.notification_message = (TextView) convertView.findViewById(R.id.feed_item_aggregated_message);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.aggfeed_item_profile_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<String> photo_urls = feed_item.getPhotoURLs();
        viewHolder.gridView = (GridView) convertView.findViewById(R.id.aggregated_feed_gridview);
        GridViewAdapter mAdapter = new GridViewAdapter(myContext, photo_urls);
        viewHolder.gridView.setAdapter(mAdapter);

        Integer photo_count = feed_item.getPhotoURLs().size();
        notificationMessage = feed_item.getAuthorName() + " added " + photo_count + " photo";
        if (photo_count > 1) {
            notificationMessage = notificationMessage + "s";
        }
        viewHolder.notification_message.setText(notificationMessage);

        String createdStr = (String) DateUtils.getRelativeDateTimeString(myContext, (long) feed_item.getCreatedDate()*1000, DateUtils.HOUR_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0);
        viewHolder.created_date.setText(createdStr);

        String hash = md5(feed_item.getAuthorEmail());
        gravatar_url = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
        Picasso.with(myContext)
                .load(gravatar_url)
                .placeholder(R.drawable.artist_placeholder)
                .into(viewHolder.profileImage);

        return convertView;
    }

    public class GridViewAdapter extends BaseAdapter {
        private Context myContext;
        private ArrayList<String> data;
        private final List<String> urls = new ArrayList<String>();

        public GridViewAdapter(Context context, ArrayList<String> inData) {
            myContext = context;
            data = inData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(myContext);
            } else {
                imageView = (ImageView) convertView;
            }

            String photo_url = data.get(position);

            if (photo_url != null) {
                Picasso.with(this.myContext)
                        .load(photo_url)
                        .placeholder(R.drawable.no_image)
                        .resize(dpToPx(128), dpToPx(128))
                        .centerInside()
                        .into(imageView);
            }
            return imageView;
        }

        class ViewHolder {
            ImageView image;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // Convert DP to PX
        // Source: http://stackoverflow.com/a/8490361
        public int dpToPx(int dps) {
            final float scale = myContext.getResources().getDisplayMetrics().density;
            int pixels = (int) (dps * scale + 0.5f);

            return pixels;
        }

    }
}