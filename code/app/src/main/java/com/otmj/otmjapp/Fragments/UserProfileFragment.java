package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.otmj.otmjapp.Adapters.UserProfilePageMoodEventAdapter;
import com.otmj.otmjapp.Helper.CircleTransform;
import com.otmj.otmjapp.Helper.FilterOptions;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.MyProfileBinding;
import com.squareup.picasso.Picasso;

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

        // Collect the argument passed to this fragment
        Bundle args = getArguments();

        // Check if username is passed correctly + if current user is following the username or not
        if (args != null && args.containsKey("username")) {
            // Get the username from the arguments
            String username = args.getString("username");
            //Log.d("UserProfileFragment", "Username from arguments: " + username);  // Log the username

            // Check if the current user is following the username
            UserManager userManager = UserManager.getInstance();
            User currentUser = userManager.getCurrentUser();  // Get the current user
            FollowHandler followHandler = new FollowHandler();

            // Add user's profile picture and username
            userManager.getUser(username, new UserManager.AuthenticationCallback() {@Override
            public void onAuthenticated(ArrayList<User> users) {
                if (users != null && !users.isEmpty()) {
                    User user = users.get(0); // Assuming you get one user from the list
                    Log.d("UserProfileFragment", "Collected username: " + user.getUsername());  // Log the username

                    // Set the username
                    binding.username.setText(user.getUsername());  // Set the username

                    // Get the profile picture URL
                    String profilePicUrl = user.getProfilePictureLink();

                    // Check if the profile picture URL is null or empty
                    if (profilePicUrl == null || profilePicUrl.isEmpty()) {
                        profilePicUrl = "android.resource://com.otmj.otmjapp/drawable/placeholder_image"; // Use a placeholder image
                    }

                    // Load the profile picture dynamically using Picasso
                    Picasso.get()
                            .load(profilePicUrl)  // Load the profile image URL
                            .placeholder(R.drawable.profile_placeholder) // Placeholder image while loading
                            .error(R.drawable.profile_placeholder) // Error image if loading fails
                            .transform(new CircleTransform()) // Apply circular transformation
                            .into(binding.profileImage);  // Bind the image to the ImageView

                    // Now check if current user is following the username
                    followHandler.getFollowIDs(currentUser.getID(), FollowHandler.FollowType.Following, new FollowHandler.FollowIDCallback() {
                        @Override
                        public void result(ArrayList<String> followIDs) {
                            // Check if the user's ID is in the list of followings
                            boolean isFollowing = followIDs.contains(user.getID());

                            if (isFollowing) {
                                Log.d("UserProfileFragment", "User is following the username.");
                                // Update the UI to show "Following" button or other appropriate UI elements
                                binding.sendRequestButton.setVisibility(View.GONE);
                            } else {

                                Log.d("UserProfileFragment", "User is NOT following the username.");
                                // Update the UI
                                binding.followersButton.setVisibility(View.GONE);
                                binding.followingButton.setVisibility(View.GONE);
                                binding.requestsButton.setVisibility(View.GONE);
                                binding.listviewMoodEventList.setVisibility(View.GONE);
                                binding.sendRequestButton.setVisibility(View.VISIBLE);
                                binding.visibilityOff.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    binding.sendRequestButton.setOnClickListener(v -> {
                        if (user != null) {
                            followHandler.sendFollowRequest(user.getID());
                            Log.d("UserProfileFragment", "Follow request sent to: " + user.getUsername());

                            // Optionally, update UI to reflect the request has been sent
                            binding.sendRequestButton.setVisibility(View.GONE);
                        } else {
                            Log.e("UserProfileFragment", "User is null, cannot send follow request.");
                        }
                    });

                } else {
                    Log.d("UserProfileFragment", "No user found with the username: " + username);
                }
            }

            @Override
            public void onAuthenticationFailure(String error) {
                // Handle failure (e.g., show a message to the user)
                Log.e("UserProfileFragment", "Failed to fetch user: " + error);
            }
            });


    } else {
            binding.sendRequestButton.setVisibility(View.GONE);

            // Set up the profile buttons and mood events if not "notfollowing"
            // Enable the buttons
            binding.followersButton.setClickable(true);
            binding.followingButton.setClickable(true);

            // Navigate to Followers List when Followers Button is clicked
            binding.followersButton.setOnClickListener(v -> {
                Bundle buttonArgs = new Bundle();
                buttonArgs.putString("buttonClicked", "followers");
                Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followersListFragment, buttonArgs);
            });

            // Navigate to Following List when Following Button is clicked
            binding.followingButton.setOnClickListener(v -> {
                Bundle buttonArgs = new Bundle();
                buttonArgs.putString("buttonClicked", "following");
                Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followingListFragment, buttonArgs);
            });

            // Navigate to Requests List when Requests Button is clicked
            binding.requestsButton.setOnClickListener(v -> {
                Bundle buttonArgs = new Bundle();
                buttonArgs.putString("buttonClicked", "requests");
                Navigation.findNavController(v).navigate(R.id.action_userProfileFragment_to_followingListFragment, buttonArgs);
            });

            // Get UserID
            UserManager user_manager = UserManager.getInstance();
            User user = user_manager.getCurrentUser();

            // Get MoodEvents
            ArrayList<String> idOfUser = new ArrayList<>(List.of(user.getID()));
            final MoodEventsManager mood_event_controller = new MoodEventsManager(idOfUser);

            moodEventsLiveData = mood_event_controller.getUserMoodEvents(null);
            if (moodEventsLiveData != null) {
                getMoodEventFromDB();
            }

            // Get follower count and following count
            FollowHandler followHandler = new FollowHandler();
            followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Followers,
                    amount -> binding.followersButton.setText(getString(R.string.follower_count, amount)));

            followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Following,
                    amount -> binding.followingButton.setText(getString(R.string.following_count, amount)));

            // Set user data to views
            binding.username.setText(user.getUsername());
            // Optionally set the profile image here

            moodEventAdapter = new UserProfilePageMoodEventAdapter(
                    requireContext(),
                    R.layout.my_mood_history_block,
                    new ArrayList<>(),
                    requireActivity()
            );
            binding.listviewMoodEventList.setAdapter(moodEventAdapter);

            binding.filterButton.setOnClickListener(v -> {
                FilterFragment popup = new FilterFragment(filterOptions, newFilterOptions -> {
                    filterOptions = newFilterOptions;
                    moodEventsLiveData = mood_event_controller.getUserMoodEvents(newFilterOptions.buildFilter(idOfUser));
                    if (moodEventsLiveData != null) {
                        getMoodEventFromDB();
                    }
                });
                popup.show(getParentFragmentManager(), null);
            });
        }
    }

    public void getMoodEventFromDB() {
        moodEventsLiveData.observe(getViewLifecycleOwner(), moodEvents -> {
            // Update the adapter's data
            moodEventAdapter.clear();
            if (moodEvents != null) {
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
