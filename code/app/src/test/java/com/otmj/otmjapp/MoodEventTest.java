package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;

import android.location.Location;

import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoodEventTest {

    private MoodEvent mockMoodEvent(){
        MoodEvent moodEvent = new MoodEvent("FF1234",
                                            R.color.surprise,
                                            "The weather is good",
                                            "Alone",
                                            false,
                                            "I am happy",
                                            "http://example.com/profile.jpg");
        return moodEvent;
    }

    @Test
    public void TestGetEmotionalState(){
        MoodEvent moodEvent = mockMoodEvent();
        assertEquals(EmotionalState.Surprise, moodEvent.getEmotionalState());
    }

    @Test
    public void TestSetEmotionalState(){
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setEmotionalState(R.color.sad);
        assertEquals(EmotionalState.Sad, moodEvent.getEmotionalState());
    }

    @Test
    public void TestGetTrigger(){
        MoodEvent moodEvent = mockMoodEvent();
        assertEquals("The weather is good", moodEvent.getTrigger());
    }

    @Test
    public void TestSetTrigger(){
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setTrigger("The weather is bad");
        assertEquals("The weather is bad", moodEvent.getTrigger());
    }

    @Test
    public void TestGetSocialSituation(){
        MoodEvent moodEvent = mockMoodEvent();
        assertEquals("Alone", moodEvent.getSocialSituation());
    }

    @Test
    public void TestSetSocialSituation(){
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setSocialSituation("With One Other Person");
        assertEquals("With One Other Person", moodEvent.getSocialSituation());
    }

    @Test
    public void TestGetReason(){
        MoodEvent moodEvent = mockMoodEvent();
        assertEquals("I am happy", moodEvent.getReason());
    }

    @Test
    public void TestSetReason(){
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setReason("I am sad");
        assertEquals("I am sad", moodEvent.getReason());
    }

    @Test
    public void TestGetImageLink(){
        MoodEvent moodEvent = mockMoodEvent();
        assertEquals("http://example.com/profile.jpg", moodEvent.getImageLink());
    }

    @Test
    public void TestSetImageLink(){
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setImageLink("http://example.com/profile.png");
        assertEquals("http://example.com/profile.png", moodEvent.getImageLink());
    }

    // ToDo implement test for getCreatedDate
    // ToDo implement test for getLocation
    // ToDo implement test for setLocation
    // ToDo implement test for FromMap
    // ToDo implement test for toMap
}
