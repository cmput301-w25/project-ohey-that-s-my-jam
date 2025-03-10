package com.otmj.otmjapp.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

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

        // Disable the buttons.
        binding.followersButton.setClickable(false);
        binding.followingButton.setClickable(false);

        // Get UserID
        UserManager user_manager = UserManager.getInstance();
        User user = user_manager.getCurrentUser();

        // Dummy data
        //User user = new User("Kai", "kaiiscool@example.com", "123456", "https://exampleImageLink");
        //Bitmap bitmapProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.dummy_profile_image);
        //int numFollowers = 5;
        //int numFollowing = 10;
        //Timestamp temp_now = new Timestamp(new Date());
        //MoodEvent moodEvent_3 = new MoodEvent(user.getID(),R.color.anger, "not happy", "Alone", false, "life is hard", null);
        //MoodEvent moodEvent_1 = new MoodEvent(user.getID(), temp_now, EmotionalState.Anger, "not happy", SocialSituation.Alone, null, "live is hard",null);
        //MoodEvent moodEvent_2 = new MoodEvent(user.getID(), temp_now, EmotionalState.Fear, "not happy", SocialSituation.With_1_Other, null, "live is hard",null);
        //ArrayList<MoodEvent> temp_moodEvents = new ArrayList<MoodEvent>();
        //temp_moodEvents.add(moodEvent_1);
        //temp_moodEvents.add(moodEvent_2);

        // get MoodEvents
        MoodEventsManager mood_event_controller = new MoodEventsManager(List.of(user.getID()));

        // add dummy data
        //mood_event_controller.addMoodEvent(moodEvent_1);
        //mood_event_controller.addMoodEvent(moodEvent_2);

        moodEventsLiveData = mood_event_controller.getMoodEvents();
        if (moodEventsLiveData != null) {
            getMoodEventFromDB();
        }

        //get follower count and follwee count
        FollowHandler followHandler = new FollowHandler();
        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Followers,
                amount -> binding.numFollowersTextView.setText(String.valueOf(amount)));

        followHandler.getFollowCount(user.getID(), FollowHandler.FollowType.Following,
                amount -> binding.numFollowingTextview.setText(String.valueOf(amount)));

        // Set data to views using binding.
        //binding.profileImage.setImageBitmap(bitmapProfileImage);
        binding.username.setText(user.getUsername());
        moodEventAdapter = new UserProfilePageMoodEventAdapter(
                requireContext(),
                R.layout.my_mood_history_block,
                new ArrayList<>()
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
