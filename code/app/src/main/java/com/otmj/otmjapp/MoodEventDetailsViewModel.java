package com.otmj.otmjapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.otmj.otmjapp.Models.MoodEvent;

public class MoodEventDetailsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<MoodEvent> moodEvent = new MutableLiveData<>();

    public void setMoodEvent(MoodEvent event){
        moodEvent.setValue(event);
    }

    public LiveData<MoodEvent> getMoodEvent(){
        return moodEvent;
    }
}