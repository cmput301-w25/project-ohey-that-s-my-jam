package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.otmj.otmjapp.Adapters.FollowersListViewAdapter;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

public class FollowersListFragment extends Fragment {

    private final FollowHandler followHandler;

    public FollowersListFragment() {
        followHandler = new FollowHandler();  // Initialize the FollowHandler
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.list_screen, container, false);

        // Find the TextView for the list title
        TextView listTitle = rootView.findViewById(R.id.list_title);

        // Get the arguments passed from the previous fragment
        Bundle arguments = getArguments();
        if (arguments != null) {
            String buttonClicked = arguments.getString("buttonClicked");

            // Check if the followers button was clicked
            if ("followers".equals(buttonClicked)) {
                listTitle.setText("FOLLOWERS");  // Set title for followers

                // Log the button click event for followers
                Log.d("FollowersListFragment", "Followers button clicked");

                // Get the current user ID
                UserManager userManager = UserManager.getInstance();
                String currentUserId = userManager.getCurrentUser().getID();  // Get current user ID

                // Fetch followers using FollowHandler
                followHandler.fetchFollowers(currentUserId, new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> followersList) {
                        // Log the followers list size
                        Log.d("FollowersListFragment", "Followers List Size: " + (followersList != null ? followersList.size() : "null"));

                        // Once followers are fetched, pass them to the adapter
                        setUpFollowersList(rootView, followersList);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error if fetching fails
                        Log.e("FollowersListFragment", "Error fetching followers", e);
                        // Optionally, show an error message
                    }
                });

                // Check if the following button was clicked
            } else if ("following".equals(buttonClicked)) {
                listTitle.setText("FOLLOWING");  // Set title for followers

                // Log the button click event for following
                Log.d("FollowersListFragment", "Following button clicked");

                // Get the current user ID
                UserManager userManager = UserManager.getInstance();
                String currentUserId = userManager.getCurrentUser().getID();  // Get current user ID

                // Fetch following using FollowHandler
                followHandler.fetchFollowing(currentUserId, new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> followingList) {
                        // Log the following list size
                        Log.d("FollowersListFragment", "Following List Size: " + (followingList != null ? followingList.size() : "null"));

                        // Once following are fetched, pass them to the adapter
                        setUpFollowersList(rootView, followingList);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error if fetching fails
                        Log.e("FollowersListFragment", "Error fetching following", e);
                        // Optionally, show an error message
                    }
                });
            }
        }

        return rootView;
    }

    // Set up the ListView with the followers data
    private void setUpFollowersList(View rootView, ArrayList<User> followersorfollowingList) {
        ListView listView = rootView.findViewById(R.id.user_list_view);

        // Populate the list
        FollowersListViewAdapter adapter = new FollowersListViewAdapter(getContext(), followersorfollowingList);
        listView.setAdapter(adapter);

    }
}
