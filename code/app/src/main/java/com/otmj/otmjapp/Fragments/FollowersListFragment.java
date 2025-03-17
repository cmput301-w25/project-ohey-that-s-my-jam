package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import com.otmj.otmjapp.Adapters.FollowersListViewAdapter;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

/**
 * Displays a list of the user's followers.
 */
public class FollowersListFragment extends Fragment {
    private ArrayList<User> originalList = new ArrayList<>();


    private final FollowHandler followHandler;

    /**
     * Initializes the {@link FollowHandler} instance.
     */
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

        // Find the SearchView
        SearchView searchView = rootView.findViewById(R.id.search_view);

        // Set up the SearchView listener (for text changes)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // You can handle the search submit event here (if needed)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list as the user types
                filterList(newText, originalList);
                return false;
            }
        });

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
                    }
                });

                // Hide the SearchView for followers
                searchView.setVisibility(View.GONE);

                // Check if the following button was clicked
            } else if ("following".equals(buttonClicked)) {
                listTitle.setText("FOLLOWING");  // Set title for following

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
                    }
                });

                // Hide the SearchView for following
                searchView.setVisibility(View.GONE);

                // Check if the "peopleYouMayKnow" button was clicked
            } else if ("peopleYouMayKnow".equals(buttonClicked)) {
                listTitle.setText("PEOPLE YOU MAY KNOW");  // Set title for "People You May Know"

                // Show the SearchView for "People You May Know"
                searchView.setVisibility(View.VISIBLE);


                // Log the button click event for "People You May Know"
                Log.d("FollowersListFragment", "People You May Know button clicked");

                // Get the current user ID
                UserManager userManager = UserManager.getInstance();
                String currentUserId = userManager.getCurrentUser().getID();  // Get current user ID

                // Fetch users the current user is not following
                followHandler.fetchNotFollowingUsers(new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> notFollowingList) {
                        // Log the list size
                        Log.d("FollowersListFragment", "Not Following List Size: " + (notFollowingList != null ? notFollowingList.size() : "null"));

                        // Once not-following users are fetched, pass them to the adapter
                        setUpFollowersList(rootView, notFollowingList);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error if fetching fails
                        Log.e("FollowersListFragment", "Error fetching not following users", e);
                    }
                });


            }

        }


        return rootView;
    }

    /**
     * Sets up the {@link ListView} with the provided list of followers or users.
     *
     * @param rootView                 The root view of the fragment.
     * @param followersorfollowingList The list of followers or users.
     */
    private void setUpFollowersList(View rootView, ArrayList<User> followersorfollowingList) {

        originalList = followersorfollowingList; // Save the original list

        ListView listView = rootView.findViewById(R.id.user_list_view);

        // Set up the ListView with the adapter
        FollowersListViewAdapter adapter = new FollowersListViewAdapter(getContext(), originalList);
        listView.setAdapter(adapter);
    }

    public ArrayList<User> getOriginalList() {
        return originalList;
    }

    public void setOriginalList(ArrayList<User> originalList) {
        this.originalList = originalList;
    }

    // Method to filter the list based on search query
    private void filterList(String query, ArrayList<User> originalList) {
        ArrayList<User> filteredList = new ArrayList<>();

        // Check if query is not empty
        if (query != null && !query.isEmpty()) {
            for (User user : originalList) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        } else {
            filteredList = originalList;  // If no search query, return the full list
        }

        // Once filtered, update the list view
        updateListView(filteredList);
    }

    // Method to update the ListView with the filtered list
    private void updateListView(ArrayList<User> filteredList) {
        // Ensure rootView is not null when accessing the list view
        View rootView = getView();
        if (rootView != null) {
            ListView listView = rootView.findViewById(R.id.user_list_view);
            FollowersListViewAdapter adapter = new FollowersListViewAdapter(getContext(), filteredList);
            listView.setAdapter(adapter);
        }
    }


}
