package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.otmj.otmjapp.Adapters.TimelineMoodEventAdapter;
import com.otmj.otmjapp.Helper.FilterOptions;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentTimelineBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Displays the timeline of mood events for the logged-in user and their followers.
 */
public class TimelineFragment extends Fragment {
    private FragmentTimelineBinding binding;
    // ListView to display the timeline of mood events.
    private final ArrayList<MoodEvent> allMoodEvents = new ArrayList<>();
    // List of all mood events (current user's events and events from users they follow).
    private TimelineMoodEventAdapter moodEventAdapter;
    private FilterOptions filterOptions = null;

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

        // Link people you may know button to page
        // Navigate to Followers List when Followers Button is clicked
        binding.peopleYouMayKnowButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("buttonClicked", "peopleYouMayKnow");  // Add an argument indicating which button was clicked

            // Navigate to Followers List using Navigation Component
            Navigation.findNavController(v).navigate(R.id.action_timelineFragment_to_peopleYouMayKnowFragment, args);
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
            moodEventsManager.getPublicMoodEvents(null).observe(
                    getViewLifecycleOwner(),
                    this::updateMoodEventsList
            );

            // Show filter popup on click
            binding.filterButton.setOnClickListener(v -> {
                FilterFragment filterPopup = new FilterFragment(filterOptions, (newFilterOptions) -> {
                    // Save filter options
                    filterOptions = newFilterOptions;
                    // Use the new filter options to show mood events
                    moodEventsManager.getPublicMoodEvents(newFilterOptions.buildFilter(ids)).observe(
                            getViewLifecycleOwner(),
                            this::updateMoodEventsList
                    );
                });
                filterPopup.show(getParentFragmentManager(), null);
            });
        });
    }

    private void updateMoodEventsList(List<MoodEvent> moodEvents) {
        allMoodEvents.clear();

        HashMap<String, ArrayList<MoodEvent>> moodEventsPerUser = new HashMap<>();
        for (MoodEvent moodEvent : moodEvents) {
            // If this is the first time this userID is seen
            if (!moodEventsPerUser.containsKey(moodEvent.getUserID())) {
                // Add to map with current mood event
                moodEventsPerUser.put(moodEvent.getUserID(), new ArrayList<>(List.of(moodEvent)));
            } else {
                ArrayList<MoodEvent> userMoodEvents = moodEventsPerUser.get(moodEvent.getUserID());
                // Only add if we don't have up to 3 mood events for the user
                if (userMoodEvents != null && userMoodEvents.size() < 3) {
                    userMoodEvents.add(moodEvent);
                } else {
                    continue;
                }
            }

            // At this point, we're only adding a maximum of 3 mood events per user
            allMoodEvents.add(moodEvent);
        }

        if (moodEventAdapter != null) {
            moodEventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
