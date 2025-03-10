package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
        followHandler = new FollowHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.list_screen, container, false);
        // Fetch followers data from Firestore and set up the ListView
        fetchFollowersFromFirestore(rootView);

        return rootView;
    }

    // Queries the DB and creates a list of ids of the followers - followerIDs
    public void fetchFollowersFromFirestore(View rootView) {
        // Get the instance of UserManager (singleton)
        UserManager userManager = UserManager.getInstance();
        // Get the current user using the instance
        String currentUser = userManager.getCurrentUser().getID();
        followHandler.fetchFollowers(currentUser, new FollowHandler.FollowCallback() {
            @Override
            public void onSuccess(ArrayList<User> followList) {
                setUpFollowersList(rootView, followList); // Pass the rootView and followers list to the adapter
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void setUpFollowersList(View rootView, ArrayList<User> followersList) {
        // Initialize the adapter and set it to the ListView
        FollowersListViewAdapter adapter = new FollowersListViewAdapter(getContext(), followersList);
        ListView listView = rootView.findViewById(R.id.user_list_view);
        listView.setAdapter(adapter);
    }

}
