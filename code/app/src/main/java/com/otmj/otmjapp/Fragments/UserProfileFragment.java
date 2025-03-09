package com.otmj.otmjapp.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.Timestamp;
import com.otmj.otmjapp.Adapters.UserProfilePageMoodEventAdapter;
import com.otmj.otmjapp.Controllers.MoodEventController;
import com.otmj.otmjapp.Helper.FollowHandler;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.MyProfileBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class UserProfileFragment extends Fragment {
    private MyProfileBinding binding;
    private User user;
    private ArrayList<String> UserIDs;
    private UserProfilePageMoodEventAdapter moodEventAdapter;
    private LiveData<ArrayList<MoodEvent>> moodEventsLiveData;
    private MoodEventController mood_event_controller;
    private Handler handler;
    private Runnable runnable;
    private FollowHandler followHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        user = user_manager.getCurrentUser();

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
        UserIDs = new ArrayList<String>();
        UserIDs.add(user.getID());
        mood_event_controller = new MoodEventController(UserIDs);

        // add dummy data
        //mood_event_controller.addMoodEvent(moodEvent_1);
        //mood_event_controller.addMoodEvent(moodEvent_2);

        // set up a clock that every one second checks if there is MoodEvents in the DB
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                // Place your code to check something every 1 second here
                if (!isMoodEvents()) {
                    // Post this runnable again after 1 second (1000ms)
                    handler.postDelayed(this, 1000);
                }
                else {
                    getMoodEventFromDB();
                    handler.removeCallbacks(runnable);
                }
            }
        };

        if (mood_event_controller.getMoodEvents()!= null) {
            moodEventsLiveData = mood_event_controller.getMoodEvents();
            getMoodEventFromDB();
        }
        else{
            handler.postDelayed(runnable, 1000);
        }

        //get follower count and follwee count
        followHandler = new FollowHandler();
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
        moodEventsLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<MoodEvent>>() {
            @Override
            public void onChanged(ArrayList<MoodEvent> moodEvents) {
                // Update the adapter's data:
                moodEventAdapter.clear();
                if(moodEvents != null && !moodEvents.isEmpty()){
                    moodEventAdapter.addAll(moodEvents);
                }
                moodEventAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * return whether DB MoodEvents is empty
     * @return true if is not empty, false if is empty
     */
    public boolean isMoodEvents(){
        return mood_event_controller.getMoodEvents() != null;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop the periodic task when the fragment is not visible
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks.
    }
}
