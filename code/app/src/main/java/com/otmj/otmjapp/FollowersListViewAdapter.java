package com.otmj.otmjapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.otmj.otmjapp.Models.Follow;

import java.util.ArrayList;

public class FollowersListViewAdapter extends ArrayAdapter<Follow> {

    // Variables
    private ArrayList<Follow> followersList;
    private Context context; // Context to user for 'inflating' layout

    // Constructor
    public FollowersListViewAdapter(@NonNull Context context, ArrayList<Follow> followersList) {
        super(context, 0, followersList); // Call the parent constructor
        this.context = context;
        this.followersList = followersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse old views if possible for performance
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_block, parent, false);
        }

        // Get the current follower object
        Follow currentFollower = followersList.get(position);

        // Find views in user_block.xml
        ImageView profilePicture = convertView.findViewById(R.id.profile_image);
        TextView userName = convertView.findViewById(R.id.username);

        // Set data (Update with real data fetching logic)
        userName.setText(currentFollower.getFollowerID()); // TODO: Replace with actual username

        // TODO: If you have profile pictures, load them using Glide/Picasso
        // Example: Glide.with(context).load(currentFollower.getProfilePictureUrl()).into(profilePicture);

        return convertView;
    }

    // Helper method to update the list dynamically
    public void updateData(ArrayList<Follow> newFollowers) {
        this.followersList.clear();
        this.followersList.addAll(newFollowers);
        notifyDataSetChanged(); // Refresh the ListView
    }

}

