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
import androidx.navigation.fragment.NavHostFragment;

import com.otmj.otmjapp.Adapters.UserProfilePageMoodEventAdapter;
import com.otmj.otmjapp.Helper.FilterOptions;
import com.otmj.otmjapp.Helper.ImageHandler;
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
        binding.viewRequestsButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("buttonClicked", "requests");  // Add an argument indicating which button was clicked

            // Navigate to Requests List using Navigation Component
            Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followingListFragment, args);
        });

        UserProfileFragmentArgs args = UserProfileFragmentArgs.fromBundle(getArguments());

        // Get UserID
        UserManager user_manager = UserManager.getInstance();
        User tempUser = args.getUser();  // Store initial value in a temporary variable

        if (tempUser == null) {
            tempUser = user_manager.getCurrentUser();
        }

        final User user = tempUser; // Make user final AFTER deciding which user to use


        // TODO: load the profile image if available in binding.profileImage
        // Load the profile image if available
        if (user.getProfilePictureLink() != null && !user.getProfilePictureLink().isEmpty()) {
            ImageHandler.loadCircularImage(requireContext(), user.getProfilePictureLink(), binding.profileImage);
        } else {
            binding.profileImage.setImageResource(R.drawable.profile_placeholder); // fallback image
        }


        // Get follower count and follwee count
        FollowHandler followHandler = new FollowHandler();
        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Followers,
                amount -> binding.followersButton.setText(getString(R.string.follower_count, amount)));

        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Following,
                amount -> binding.followingButton.setText(getString(R.string.following_count, amount)));

        followHandler.getRequestCount(amount ->
                binding.viewRequestsButton.setText(getString(R.string.requests_count, amount)));

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
        binding.listviewMoodEventList.setOnItemClickListener(
                (adapterView, view1, i, l) -> {
                    UserProfileFragmentDirections.ActionUserProfileFragmentToMoodEventDetailsFragment toDetails =
                            UserProfileFragmentDirections.actionUserProfileFragmentToMoodEventDetailsFragment();
                    toDetails.setMoodEvent(moodEventAdapter.getItem(i));

                    NavHostFragment.findNavController(UserProfileFragment.this).navigate(toDetails);
                });

        boolean isCurrentUserProfile = user.getID().equals(user_manager.getCurrentUser().getID());
        moodEventAdapter.setIsCurrentUserProfile(isCurrentUserProfile);

        // Show mood events

        User loggedInUser = user_manager.getCurrentUser();
        if (user != loggedInUser) {
            binding.filterButton.setVisibility(View.GONE);
            binding.viewRequestsButton.setVisibility(View.INVISIBLE);

            moodEventsLiveData = mood_event_controller.getPublicMoodEvents(null);
            if (moodEventsLiveData != null) {
                getMoodEventFromDB();
            }

            followHandler.isFollowing(user.getID(), isFollowing -> {
                if (isFollowing) {
                    moodEventAdapter.setBlurText(false);
                    binding.blurOverlay.setVisibility(View.GONE);
                    binding.requestButton.setVisibility(View.GONE);
                    binding.unfollowButton.setVisibility(View.VISIBLE);

                } else {
                    moodEventAdapter.setBlurText(true);
                    // Don't allow it to be clickable
                    binding.listviewMoodEventList.setOnItemClickListener(null);
                    binding.blurOverlay.setVisibility(View.VISIBLE);
                    binding.unfollowButton.setVisibility(View.GONE);
                    binding.requestButton.setVisibility(View.VISIBLE);
                }
            });

            // onClickListener for the unfollow button
            binding.unfollowButton.setOnClickListener(v -> {
                followHandler.unfollowUser(user.getID());

                // Replace the unfollow button with the request button
                binding.unfollowButton.setVisibility(View.GONE);
                binding.requestButton.setVisibility(View.VISIBLE);

            });


            followHandler.hasFollowRequestBeenSent(user.getID(), requestExists -> {
                if (requestExists) {
                    // Set button to "Requested" and make it unclickable
                    binding.requestButton.setText(R.string.requested_text);
                    binding.requestButton.setEnabled(false); // Disable clicks
                    binding.requestButton.setAlpha(0.5f); // Make it look disabled
                    binding.requestButton.setVisibility(View.VISIBLE);
                } else {
                    // Check if already following
                    followHandler.isFollowing(user.getID(), isFollowing -> {
                        if (isFollowing) {
                            binding.requestButton.setVisibility(View.GONE);
                        } else {
                            // Show request button
                            binding.requestButton.setEnabled(true);
                            binding.requestButton.setAlpha(1.0f);

                        }
                    });
                }
            });

            binding.requestButton.setOnClickListener(v -> {
                followHandler.sendFollowRequest(user.getID());
                Log.e("UserProfileFragment", "user.getID() =" + user.getID());

                // Set button to "Requested" and make it unclickable
                binding.requestButton.setText(R.string.requested_text);
                binding.requestButton.setEnabled(false); // Disable clicks
                binding.requestButton.setAlpha(0.5f); // Make it look disabled
                binding.requestButton.setVisibility(View.VISIBLE); // Show the button
            });

        } else {
            moodEventsLiveData = mood_event_controller.getUserMoodEvents(null);
            binding.logoutButton.setVisibility(View.VISIBLE);
            binding.logoutButton.setOnClickListener(v -> user_manager.logout(this));
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
