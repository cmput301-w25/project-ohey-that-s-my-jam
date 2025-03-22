package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.otmj.otmjapp.Adapters.UserProfilePageMoodEventAdapter;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.MyProfileBinding;

import java.util.ArrayList;
import java.util.List;


public class UserProfileFragment extends Fragment {
    private MyProfileBinding binding;
    private UserProfilePageMoodEventAdapter moodEventAdapter;
    private LiveData<ArrayList<MoodEvent>> moodEventsLiveData;


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

        // Get UserID
        UserManager user_manager = UserManager.getInstance();
        User user = user_manager.getCurrentUser();

        // get MoodEvents
        MoodEventsManager mood_event_controller = new MoodEventsManager(List.of(user.getID()));

        moodEventsLiveData = mood_event_controller.getUserMoodEvents(null);
        if (moodEventsLiveData != null) {
            getMoodEventFromDB();
        }

        //get follower count and follwee count
        FollowHandler followHandler = new FollowHandler();
        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Followers,
                amount -> binding.followersButton.setText(getString(R.string.follower_count, amount)));

        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Following,
                amount -> binding.followingButton.setText(getString(R.string.following_count, amount)));

        // Set data to views using binding.
        //binding.profileImage.setImageBitmap(bitmapProfileImage);
        binding.username.setText(user.getUsername());
        moodEventAdapter = new UserProfilePageMoodEventAdapter(
                requireContext(),
                R.layout.my_mood_history_block,
                new ArrayList<>(),
                requireActivity()
        );
        binding.listviewMoodEventList.setAdapter(moodEventAdapter);
    }

    public void getMoodEventFromDB(){
        moodEventsLiveData.observe(getViewLifecycleOwner(), moodEvents -> {
            // Update the adapter's data:
            moodEventAdapter.clear();
            if(moodEvents != null && !moodEvents.isEmpty()){
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
