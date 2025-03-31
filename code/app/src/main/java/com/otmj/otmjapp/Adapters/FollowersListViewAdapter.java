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

import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import java.util.ArrayList;

public class FollowersListViewAdapter extends ArrayAdapter<User> {

    public FollowersListViewAdapter(Context context, ArrayList<User> followersList) {
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
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_block, parent, false);
        }

        // Get the User object for the current position
        User user = getItem(position);
        assert user != null;

        // Set the username
        TextView userNameTextView = listItemView.findViewById(R.id.username);
        userNameTextView.setText(user.getUsername());  // Assuming `getUsername()` is a method in the User class

        // Get the profile picture URL
        String profilePicUrl = user.getProfilePictureLink();

        ImageView profileImageView = listItemView.findViewById(R.id.profile_image);
        ImageHandler.loadCircularImage(
                getContext(),
                (profilePicUrl == null || profilePicUrl.isEmpty()) ? "" : profilePicUrl,
                profileImageView
        );

        return listItemView;
    }
}