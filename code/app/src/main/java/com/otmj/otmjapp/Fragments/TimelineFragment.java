package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Adapters.TimelineMoodEventAdapter;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentTimelineBinding;

import java.util.ArrayList;

/**
 * Displays the timeline of mood events for the logged-in user and their followers.
 */
public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;
    /** ListView to display the timeline of mood events. */
    private final ArrayList<MoodEvent> allMoodEvents = new ArrayList<>();
    /** List of all mood events (current user's events and events from users they follow). */
    private TimelineMoodEventAdapter moodEventAdapter;

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

        ListView moodEventListView = binding.timelineList;
        moodEventAdapter = new TimelineMoodEventAdapter(requireContext(), allMoodEvents);
        moodEventListView.setAdapter(moodEventAdapter);

        moodEventListView.setOnItemClickListener(((adapterView, view1, i, l) -> {
            // Add mood event argument
            TimelineFragmentDirections.ActionTimelineFragmentToMoodEventDetailsFragment toDetails =
                    TimelineFragmentDirections.actionTimelineFragmentToMoodEventDetailsFragment();
            toDetails.setMoodEvent(allMoodEvents.get(i));

            // Go to details view
            NavHostFragment.findNavController(TimelineFragment.this).navigate(toDetails);
        }));

        binding.filterButton.setOnClickListener(v -> {
            FilterFragment filterPopup = new FilterFragment();
            filterPopup.show(getParentFragmentManager(), null);
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Populates the timeline with mood events for the current user and the users they follow.
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();

        FollowHandler followHandler = new FollowHandler();
        followHandler.getFollowIDs(currentUser.getID(), FollowHandler.FollowType.Following, ids ->
        {
            ids.add(currentUser.getID()); // Add user's ID to list

            MoodEventsManager moodEventsManager = new MoodEventsManager(ids);
            moodEventsManager.getMoodEvents().observe(getViewLifecycleOwner(), moodEvents -> {
                allMoodEvents.clear();
                allMoodEvents.addAll(moodEvents);

                if (moodEventAdapter != null) {
                    moodEventAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}