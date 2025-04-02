package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;

import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.Privacy;
import com.otmj.otmjapp.Models.SocialSituation;

import org.junit.Test;

public class MoodEventTest {

    private MoodEvent mockMoodEvent(){
        MoodEvent moodEvent = new MoodEvent("FF1234",
                                            EmotionalState.Surprise,
                                            SocialSituation.Alone,
                                            false,
                                            "I am happy",
                                            "http://example.com/profile.jpg",
                MoodEvent.Privacy.Public);
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
        moodEvent.setEmotionalState(EmotionalState.Sad);
        assertEquals(EmotionalState.Sad, moodEvent.getEmotionalState());
    }

    @Test
    public void TestGetSocialSituation(){
        MoodEvent moodEvent = mockMoodEvent();
        assertEquals(SocialSituation.Alone, moodEvent.getSocialSituation());
    }

    @Test
    public void TestSetSocialSituation(){
        MoodEvent moodEvent = mockMoodEvent();
        moodEvent.setSocialSituation(SocialSituation.With_1_Other);
        assertEquals(SocialSituation.With_1_Other, moodEvent.getSocialSituation());
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
