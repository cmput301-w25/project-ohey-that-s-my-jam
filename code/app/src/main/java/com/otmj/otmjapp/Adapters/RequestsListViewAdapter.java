package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otmj.otmjapp.Helper.CircleTransform;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestsListViewAdapter extends ArrayAdapter<User> {
    public RequestsListViewAdapter(Context context, ArrayList<User> followersList) {
        super(context, 0, followersList); // Pass context, layout resource, and the data
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // If the list is empty, return an empty view
        if (getCount() == 0) {
            // You can choose to return a placeholder view here if necessary.
            return new View(getContext()); // Empty view if the list is empty
        }

        View listItemView = convertView;

        if(null == listItemView) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.request_block, parent, false);
        }

        User user = getItem(position);
        assert user != null;

        // Set the username
        TextView userNameTextView = listItemView.findViewById(R.id.username);
        userNameTextView.setText(user.getUsername());

        // Get the profile picture URL
        String profilePicUrl = user.getProfilePictureLink();

        // Check if the profile picture URL is null or empty
        if (profilePicUrl == null || profilePicUrl.isEmpty()) {
            profilePicUrl = "android.resource://com.otmj.otmjapp/drawable/placeholder_image"; // Use a placeholder image
        }

        // Set the profile picture using Picasso (with a placeholder)
        ImageView profileImageView = listItemView.findViewById(R.id.profile_image);
        Picasso.get()
                .load(profilePicUrl)
                .placeholder(R.drawable.profile_placeholder) // Placeholder while loading
                .error(R.drawable.profile_placeholder) // Placeholder in case of an error
                .transform(new CircleTransform()) // Apply the circular transformation
                .into(profileImageView);

        return listItemView;
    }
}
