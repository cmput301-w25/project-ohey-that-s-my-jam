package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.otmj.otmjapp.Adapters.FollowersListViewAdapter;
import com.otmj.otmjapp.Adapters.RequestsListViewAdapter;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

/**
 * Displays a list of a user's follow requests, followers, or following users.
 */
public class FollowListFragment extends Fragment {
    private ArrayList<User> originalList = new ArrayList<>();


    private final FollowHandler followHandler;

    /**
     * Initializes the {@link FollowHandler} instance.
     */
    public FollowListFragment() {
        followHandler = new FollowHandler();  // Initialize the FollowHandler
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.list_screen, container, false);

        // Get current user
        UserManager userManager = UserManager.getInstance();
        String currentUserId = userManager.getCurrentUser().getID();

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
                Log.d("FollowListFragment", "Followers button clicked");

                // Fetch followers using FollowHandler
                followHandler.fetchFollowers(currentUserId, new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> followersList) {
                        // Log the followers list size
                        Log.d("FollowListFragment", "Followers List Size: " + (followersList != null ? followersList.size() : "null"));

                        // Once followers are fetched, pass them to the adapter
                        setUpFollowersList(rootView, followersList);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error if fetching fails
                        Log.e("FollowListFragment", "Error fetching followers", e);
                        // Optionally, show an error message
                    }
                });

                // Hide the SearchView for followers
                searchView.setVisibility(View.GONE);

                // Check if the following button was clicked
            } else if ("following".equals(buttonClicked)) {
                listTitle.setText("FOLLOWING");  // Set title for following

                // Log the button click event for following
                Log.d("FollowListFragment", "Following button clicked");

                // Fetch following using FollowHandler
                followHandler.fetchFollowing(currentUserId, new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> followingList) {
                        // Log the following list size
                        Log.d("FollowListFragment", "Following List Size: " + (followingList != null ? followingList.size() : "null"));

                        // Once following are fetched, pass them to the adapter
                        setUpFollowersList(rootView, followingList);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error if fetching fails
                        Log.e("FollowListFragment", "Error fetching following", e);
                        // Optionally, show an error message
                    }
                });

                // Hide the SearchView for following
                searchView.setVisibility(View.GONE);

                // Check if the "peopleYouMayKnow" button was clicked
            } else if ("peopleYouMayKnow".equals(buttonClicked)) {
                listTitle.setText("PEOPLE YOU MAY KNOW");  // Set title for "People You May Know"

                // Show the SearchView for "People You May Know"
                searchView.setVisibility(View.VISIBLE);

                // Log the button click event for following
                Log.d("FollowListFragment", "peopleYouMayKnow button clicked");

                // Log the button click event for "People You May Know"
                Log.d("FollowersListFragment", "People You May Know button clicked");


                // Fetch users the current user is not following
                followHandler.fetchNotFollowingUsers(new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> notFollowingList) {
                        // Log the following list size
                        Log.d("FollowListFragment", "notFollowingList List Size: " + (notFollowingList != null ? notFollowingList.size() : "null"));

                        // Once not-following users are fetched, pass them to the adapter
                        setUpFollowersList(rootView, notFollowingList);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error if fetching fails
                        Log.e("FollowListFragment", "Error fetching following", e);
                        // Optionally, show an error message
                    }
                });
            } else if("requests".equals(buttonClicked)) {
                listTitle.setText("REQUESTS");  // Set title for followers

                followHandler.getRequests(new FollowHandler.FollowCallback() {
                    @Override
                    public void onSuccess(ArrayList<User> requestsList) {
                        setUpRequestsList(rootView, requestsList);
                    }

                    @Override
                    public void onFailure(Exception e) { /*TODO: Handle failure*/ }
                });


            }
        }

        return rootView;
    }

    /**
     * Sets up the {@link ListView} with the provided list of followers or users.
     *
     * @param rootView                 The root view of the fragment.
     * @param followList The list of followers or users that the current user is following.
     */
    // Set up the ListView with the followers data
    private void setUpFollowersList(View rootView, ArrayList<User> followList) {

        originalList = followList; // Save the original list

        ListView listView = rootView.findViewById(R.id.user_list_view);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            FollowListFragmentDirections.ActionFollowersListFragmentToUserProfileFragment toUserProfile =
                    FollowListFragmentDirections.actionFollowersListFragmentToUserProfileFragment();
            toUserProfile.setUser(followList.get(i));

            NavHostFragment.findNavController(FollowListFragment.this).navigate(toUserProfile);
        });

        // Populate the list
        FollowersListViewAdapter adapter = new FollowersListViewAdapter(getContext(), followList);
        listView.setAdapter(adapter);
    }

    private void setUpRequestsList(View rootView, ArrayList<User> requestList) {
        ListView listView = rootView.findViewById(R.id.user_list_view);

        // Populate the list
        RequestsListViewAdapter adapter = new RequestsListViewAdapter(getContext(), requestList);
        // Set up the ListView with the adapter
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
