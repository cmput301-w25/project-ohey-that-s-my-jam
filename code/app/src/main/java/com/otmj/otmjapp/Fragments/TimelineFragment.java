package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Adapters.TimelineMoodEventAdapter;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.otmj.otmjapp.MoodEventAddEditDialogFragment;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentTimelineBinding;

import java.util.ArrayList;

/**
 * Displays the timeline of mood events for the logged-in user and their followers.
 */
public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;
    /** ListView to display the timeline of mood events. */
    private ListView moodEventListView;
    /** List of all mood events (current user's events and events from users they follow). */
    private final ArrayList<MoodEvent> allMoodEvents;
    /** List of mood events belonging only to the current user. */
    private final ArrayList<MoodEvent> userMoodEvents;
    /** Adapter for the ListView to display mood events. */
    private TimelineMoodEventAdapter moodEventAdapter;
    /** Firestore database helper for querying mood events. */
    private final FirestoreDB<MoodEvent> db;
    /** The currently logged-in user. */
    private final User currentUser;

    public TimelineFragment() {
        binding = null;
        moodEventListView = null;
        allMoodEvents = new ArrayList<>();
        userMoodEvents = new ArrayList<>();
        moodEventAdapter = null;
        db = new FirestoreDB<>("mood_events");;
        currentUser = UserManager.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        if (container != null) {
            container.removeAllViews();
            container.clearDisappearingChildren();
        }

        binding = FragmentTimelineBinding.inflate(inflater, container, false);

        View view = binding.getRoot();
        moodEventListView = view.findViewById(R.id.timeline_list);;

        if (currentUser != null) {
            populateMoodEvents();
        }

        moodEventAdapter = new TimelineMoodEventAdapter(requireContext(), allMoodEvents);
        moodEventListView.setAdapter(moodEventAdapter);

        moodEventListView.setOnItemClickListener(((adapterView, view1, i, l) -> {
            // TODO: fix MoodEventAddEditDialogFragment because when the 'Add' button is clicked, it returns the user to the homescreen and the data is not updated properly
            // TODO: change the text shown on submit buttom from 'Add' to 'Edit'

            MoodEvent moodEvent = moodEventAdapter.getItem(i);

            if(userMoodEvents.contains(moodEvent)) {
                // TODO: show appropriate dialog fragment
            }

            MoodEventAddEditDialogFragment dialogFragment = MoodEventAddEditDialogFragment.newInstance(moodEvent);
            dialogFragment.show(getParentFragmentManager(), "edit");
        }));

        return binding.getRoot();

    }

    /**
     * Populates the timeline with mood events for the current user and the users they follow.
     */
    public void populateMoodEvents() {
        allMoodEvents.clear(); // clear moodEvents so that we don't get duplicates

        getUserMoodEvents();
        getFollowingMoodEvents();

        if(null != moodEventAdapter && null != moodEventListView) {
            moodEventAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Fetches mood events for the current user and adds them to both the user-specific list
     * and the list of all mood events.
     */
    public void getUserMoodEvents() {
        getMoodEvents(userMoodEvents, Filter.equalTo("userID", currentUser.getID()));
        getMoodEvents(allMoodEvents, Filter.equalTo("userID", currentUser.getID()));
    }

    /**
     * Fetches mood events for the users that the current user is following and adds them to the list of all
     * mood events.
     */
    public void getFollowingMoodEvents() {
        FollowHandler followHandler = new FollowHandler();
        followHandler.fetchFollowing(currentUser.getID(), new FollowHandler.FollowCallback() {
            @Override
            public void onSuccess(ArrayList<User> followList) {
                for(User followee : followList) { // get the moodEvents of each follower
                    getMoodEvents(allMoodEvents, Filter.equalTo("userID", followee.getID()));
                }
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Handle error
            }
        });
    }

    /**
     * Fetches mood events from Firestore based on the provided filter and adds them to the specified list.
     *
     * @param moodEvents The list to which the fetched mood events will be added.
     * @param filter     The Firestore filter to apply when querying mood events.
     */
    public void getMoodEvents(ArrayList<MoodEvent> moodEvents, Filter filter) {
        db.getDocuments(filter, MoodEvent.class, new FirestoreDB.DBCallback<MoodEvent>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                moodEvents.addAll(result);
                moodEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: Handle error
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}