package com.otmj.otmjapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.otmj.otmjapp.Adapters.TimelineMoodEventAdapter;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.TimelineViewBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TimelineFragment extends Fragment {

    private TimelineViewBinding binding;
    private TimelineMoodEventAdapter timeline;
    private ArrayList<MoodEvent> moodEvents = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = TimelineViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addDummyData();

        timeline = new TimelineMoodEventAdapter(view.getContext(), moodEvents);
        ListView timelineLV = binding.timelineList;
        timelineLV.setAdapter(timeline);
    }

    private void addDummyData() {
        LocalDateTime now = LocalDateTime.now();

        moodEvents.addAll(Arrays.asList(
                new MoodEvent("user1", now, EmotionalState.Disgust,
                        null, SocialSituation.Alone, null, "", ""),
                new MoodEvent("user2", now.plusDays(1), EmotionalState.Anger,
                        null, SocialSituation.With_A_Crowd, null, "", ""),
                new MoodEvent("user3", now.plusWeeks(2), EmotionalState.Confusion,
                        null, SocialSituation.With_1_Other, null, "", ""),
                new MoodEvent("user4", now.plusYears(1), EmotionalState.Sad,
                        "cat died", SocialSituation.With_2_Others, null, "", ""),
                new MoodEvent("user5", now.plusDays(2), EmotionalState.Happy,
                        "100% on quiz", SocialSituation.Alone, null, "", "")
        ));
    }
}