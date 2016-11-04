package io.getstream.example.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.getstream.example.R;
import io.getstream.example.models.NotificationFeedItem;

public class NotificationFeedAdapter extends ArrayAdapter<NotificationFeedItem> {
    private final Activity context;

    public NotificationFeedAdapter(Activity context, List<NotificationFeedItem> feedItems) {
        super(context, 0, feedItems);
        this.context = context;
    }

    public class ViewHolder {
        TextView feedID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        NotificationFeedItem feedItem = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.notification_main_layout, parent, false);

            holder = new ViewHolder();
//            holder.feedID = (TextView) convertView.findViewById(R.id.feed_item_author_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.feedID.setText(feedItem.getId());

        return convertView;
    }

}
