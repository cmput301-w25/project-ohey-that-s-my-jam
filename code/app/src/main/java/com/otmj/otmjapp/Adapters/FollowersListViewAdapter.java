package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;


import com.otmj.otmjapp.Helper.CircleTransform;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import java.util.ArrayList;
import java.util.Set;

import com.squareup.picasso.Picasso;  // Import Picasso

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

        // Set the click listener on the username
        userNameTextView.setOnClickListener(v -> {
            // Handle the click event here
            // You can navigate to a profile fragment or any action you want
            // Get the username text that was clicked
            String clickedUsername = userNameTextView.getText().toString();
            Log.d("UserProfileFragment", "Clicked username: " + clickedUsername); // Log the clicked username

            // Create a Bundle to pass the username
            Bundle args = new Bundle();
            args.putString("username", clickedUsername); // Pass the clicked username to the Bundle
            args.putBoolean("notfollowing", true);  // Add the argument to the Bundle

            // Navigate to the Unfollowed Profile Fragment using Navigation Component
            Navigation.findNavController(v).navigate(R.id.action_followersListFragment_to_unfollowedProfileFragment, args);
        });


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