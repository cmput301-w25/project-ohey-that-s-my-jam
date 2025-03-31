package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.otmj.otmjapp.Adapters.TimelineMusicEventAdapter;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.MusicEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MusicEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.databinding.FragmentMusicTimelineBinding;

import java.util.ArrayList;

public class MusicTimelineFragment extends Fragment {
    private FragmentMusicTimelineBinding binding;
    private final ArrayList<MusicEvent> allMusicEvents = new ArrayList<>();
    private TimelineMusicEventAdapter musicEventAdapter;

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
            Log.d("MusicTimelineFragment", "Following IDs: " + ids.toString());
            MusicEventsManager musicEventsManager = new MusicEventsManager(ids);
            musicEventsManager.getAllMusicEvents(true, musicEvent -> {
                allMusicEvents.add(musicEvent);
                if (musicEventAdapter != null) {
                    musicEventAdapter.notifyDataSetChanged();
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
