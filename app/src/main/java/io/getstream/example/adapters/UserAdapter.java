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
import java.util.List;

import io.getstream.example.R;
import io.getstream.example.models.User;

import static io.getstream.example.utils.Gravatar.md5;

public class UserAdapter extends ArrayAdapter<User> {
    Context myContext;
    private static class ViewHolder {
        TextView username;
        ImageView profileImage;
    }

    public UserAdapter(Context context, List<User> users) {
        super(context, R.layout.userlist_user, users);
        this.myContext = context;
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

        return convertView;
    }
}