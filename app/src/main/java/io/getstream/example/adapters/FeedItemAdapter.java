package io.getstream.example.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import io.getstream.example.R;
import io.getstream.example.models.FeedItem;

import static io.getstream.example.utils.Gravatar.md5;


public class FeedItemAdapter extends ArrayAdapter<FeedItem> {
    Context myContext;
    private static class ViewHolder {
        TextView author_name;
        TextView created_date;
        ImageView photoImage;
        ImageView profileImage;
    }

    public FeedItemAdapter(Context context, ArrayList<FeedItem> feedItems) {
        super(context, R.layout.feed_item, feedItems);
        this.myContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem feed_item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_item, parent, false);

            viewHolder.author_name = (TextView) convertView.findViewById(R.id.feed_item_author_name);
            viewHolder.created_date = (TextView) convertView.findViewById(R.id.feed_item_created_date);
            viewHolder.photoImage = (ImageView) convertView.findViewById(R.id.feed_item_photo_image);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.feed_item_profile_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.author_name.setText(feed_item.getAuthorName());
        viewHolder.created_date.setText(feed_item.getCreatedDate());

        Picasso.with(myContext)
                .load(feed_item.getPhotoUrl())
                .placeholder(R.drawable.no_image)
                .into(viewHolder.photoImage);

        String hash = md5(feed_item.getAuthorEmail());
        String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
        Log.i("gravatar url", gravatarUrl);
        Picasso.with(myContext)
                .load(gravatarUrl)
                .placeholder(R.drawable.artist_placeholder)
                .into(viewHolder.profileImage);

        return convertView;
    }
}