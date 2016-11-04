package io.getstream.example.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.getstream.example.R;
import io.getstream.example.models.AggregatedFeedItem;

public class AggregatedFeedAdapter extends ArrayAdapter<AggregatedFeedItem> {
    private final Activity context;

    public AggregatedFeedAdapter(Activity context, List<AggregatedFeedItem> feedItems) {
        super(context, 0, feedItems);
        this.context = context;
    }

    public class ViewHolder {
        TextView feedID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        AggregatedFeedItem feedItem = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.aggregated_main_layout, parent, false);

            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

}
