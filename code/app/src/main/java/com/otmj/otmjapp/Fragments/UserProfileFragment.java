package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.otmj.otmjapp.Adapters.UserProfilePageMoodEventAdapter;
import com.otmj.otmjapp.Helper.FilterOptions;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.MyProfileBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserProfileFragment extends Fragment {
    private MyProfileBinding binding;
    private UserProfilePageMoodEventAdapter moodEventAdapter;
    private LiveData<ArrayList<MoodEvent>> moodEventsLiveData;
    private FilterOptions filterOptions;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using binding and return the root view.
        binding = MyProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enable the buttons
        binding.followersButton.setClickable(true);
        binding.followingButton.setClickable(true);

        // Navigate to Followers List when Followers Button is clicked
        binding.followersButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("buttonClicked", "followers");  // Add an argument indicating which button was clicked

            // Navigate to Followers List using Navigation Component
            Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followersListFragment, args);
        });

        // Navigate to Followers List when Following Button is clicked
        binding.followingButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("buttonClicked", "following");  // Add an argument indicating which button was clicked

            // Navigate to Following List using Navigation Component
            Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followingListFragment, args);
        });

        // Navigate to Requests List when Requests Button is clicked
        binding.requestsButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("buttonClicked", "requests");  // Add an argument indicating which button was clicked

            // Navigate to Requests List using Navigation Component
            Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followingListFragment, args);
        });

        UserProfileFragmentArgs args = UserProfileFragmentArgs.fromBundle(getArguments());

        // Get UserID
        UserManager user_manager = UserManager.getInstance();
        User user = args.getUser();
        if (user == null ) {
            user = user_manager.getCurrentUser();
        }

        // Get follower count and follwee count
        FollowHandler followHandler = new FollowHandler();
        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Followers,
                amount -> binding.followersButton.setText(getString(R.string.follower_count, amount)));

        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Following,
                amount -> binding.followingButton.setText(getString(R.string.following_count, amount)));

        binding.username.setText(user.getUsername());

        // Set up mood events manager
        ArrayList<String> idOfUser = new ArrayList<>(List.of(user.getID()));
        final MoodEventsManager mood_event_controller = new MoodEventsManager(idOfUser);

        // Set up filter button
        binding.filterButton.setOnClickListener(v -> {
            FilterFragment popup = new FilterFragment(filterOptions, newFilterOptions -> {
                // Save filter options
                filterOptions = newFilterOptions;
                // Get new mood events with specified filter
                moodEventsLiveData = mood_event_controller.getUserMoodEvents(
                        newFilterOptions.buildFilter(idOfUser));
                if (moodEventsLiveData != null) {
                    getMoodEventFromDB();
                }
            });
            popup.show(getParentFragmentManager(), null);
        });

        // Set up mood event list adapter
        moodEventAdapter = new UserProfilePageMoodEventAdapter(
                requireContext(),
                R.layout.my_mood_history_block,
                new ArrayList<>(),
                requireActivity()
        );
        binding.listviewMoodEventList.setAdapter(moodEventAdapter);

        // Show mood events

        User loggedInUser = user_manager.getCurrentUser();
        if (user != loggedInUser) {
            // For now, disable all views
            binding.listviewMoodEventList.setVisibility(View.INVISIBLE);
            binding.filterButton.setVisibility(View.INVISIBLE);
            binding.recentEventsTitle.setVisibility(View.INVISIBLE);
            binding.requestsButton.setVisibility(View.INVISIBLE);

            // TODO: Show mood events if logged in user is following current user

            // TODO: Show correct button depending on whether following or not
            followHandler.isFollowing(user.getID(), isFollowing -> {
                if (isFollowing) {
                    binding.requestButton.setVisibility(View.GONE);
                    binding.unfollowButton.setVisibility(View.VISIBLE);
                } else {
                    binding.unfollowButton.setVisibility(View.GONE);
                    binding.requestButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            moodEventsLiveData = mood_event_controller.getUserMoodEvents(null);
            if (moodEventsLiveData != null) {
                getMoodEventFromDB();
            }
        }
    }

    public void getMoodEventFromDB(){
        moodEventsLiveData.observe(getViewLifecycleOwner(), moodEvents -> {
            // Update the adapter's data:
            moodEventAdapter.clear();
            if(moodEvents != null){
                moodEventAdapter.addAll(moodEvents);
            }
            moodEventAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks.
    }
}
