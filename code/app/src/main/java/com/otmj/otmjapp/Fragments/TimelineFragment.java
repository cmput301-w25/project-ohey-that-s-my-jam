package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private ArrayList<MoodEvent> allMoodEvents = new ArrayList<>();
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

        moodEventListView.setOnItemClickListener(((adapterView, view1, i, l) ->
                NavHostFragment.findNavController(TimelineFragment.this)
                        .navigate(R.id.action_timelineFragment_to_moodEventDetailsFragment)));

        return binding.getRoot();

    }

    /**
     * Populates the timeline with mood events for the current user and the users they follow.
     */
    public void populateMoodEvents() {
        allMoodEvents.clear(); // clear moodEvents so that we don't get duplicates

        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();

        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(currentUser.getID());


        if (moodEventAdapter != null) {
            moodEventAdapter.notifyDataSetChanged();
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateMoodEvents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}