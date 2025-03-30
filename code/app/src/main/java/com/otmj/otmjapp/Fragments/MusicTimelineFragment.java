package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.otmj.otmjapp.Adapters.TimelineMusicEventAdapter;
import com.otmj.otmjapp.Helper.FilterOptions;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.MusicEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MusicEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.databinding.FragmentMusicTimelineBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicTimelineFragment extends Fragment {
    private FragmentMusicTimelineBinding binding;
    private final ArrayList<MusicEvent> allMusicEvents = new ArrayList<>();
    private TimelineMusicEventAdapter musicEventAdapter;
    private FilterOptions filterOptions = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null != container) {
            container.removeAllViews();
            container.clearDisappearingChildren();
        }

        binding = FragmentMusicTimelineBinding.inflate(inflater, container, false);

        ListView musicEventListView = binding.timelineList; // TODO: find where to define this field
        musicEventAdapter = new TimelineMusicEventAdapter(requireActivity(), allMusicEvents);

        musicEventListView.setAdapter(musicEventAdapter);
        musicEventListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            // TODO: set behaviour when event is clicked
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Populates the timeline with music events for the current user and the users they follow.

        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();

        FollowHandler followHandler = new FollowHandler();
        followHandler.getFollowIDs(currentUser.getID(), FollowHandler.FollowType.Following, ids -> {
            ids.add(currentUser.getID()); // Add user's ID to list

            MusicEventsManager musicEventsManager = new MusicEventsManager(ids);
            musicEventsManager.getPublicMusicEvents(null).observe(
                    getViewLifecycleOwner(),
                    this::updateMusicEventsList
            );

            // Show filter popup on click
            binding.filterButton.setOnClickListener(v -> {
                FilterFragment filterPopup = new FilterFragment(filterOptions, (newFilterOptions) -> {
                    // save filter options
                    filterOptions = newFilterOptions;
                    // use the new filter options to show music events
                    musicEventsManager.getPublicMusicEvents(newFilterOptions.buildFilter(ids, null)).observe(
                            getViewLifecycleOwner(),
                            this::updateMusicEventsList
                    );
                });
            });
        });

    }

    private void updateMusicEventsList(List<MusicEvent> musicEvents) {
        allMusicEvents.clear();

        HashMap<String, ArrayList<MusicEvent>> musicEventsPerUser = new HashMap<>();
        for (MusicEvent musicEvent : musicEvents) {
            // If this is the first time this userID is seen
            if (!musicEventsPerUser.containsKey(musicEvent.getUser().getID())) {
                // Add to map with current mood event
                musicEventsPerUser.put(musicEvent.getUser().getID(), new ArrayList<>(List.of(musicEvent)));
            } else {
                ArrayList<MusicEvent> userMusicEvents = musicEventsPerUser.get(musicEvent.getUser().getID());
                // Only add if we don't have up to 3 mood events for the user
                if (userMusicEvents != null && userMusicEvents.size() < 3) {
                    userMusicEvents.add(musicEvent);
                } else {
                    continue;
                }
            }

            // At this point, we're only adding a maximum of 3 mood events per user
            allMusicEvents.add(musicEvent);
        }

        if (musicEventAdapter != null) {
            musicEventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
