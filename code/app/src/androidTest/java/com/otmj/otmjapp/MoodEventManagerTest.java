package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.util.Log;


import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import com.otmj.otmjapp.Helper.MoodEventsManager;

import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.MoodHistoryFilter;
import com.otmj.otmjapp.Models.SimpleLocation;

import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class MoodEventManagerTest {

    private MoodEventsManager moodEventsManager;
    private ArrayList<String> userIds = new ArrayList<String>();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void setUp() throws InterruptedException {
        userIds.add("1");
        moodEventsManager = new MoodEventsManager(userIds);
        MoodEvent moodEvent = new MoodEvent("1", "Anger", "Alone", true, "testing", null, MoodEvent.Privacy.Private);
        moodEvent.setLocation(new SimpleLocation(53.52083331444507, -113.51186385625829));
        MoodEvent moodEvent1 = new MoodEvent("2", "Anger", "Alone", true, "testGetAll", null, MoodEvent.Privacy.Private);
        moodEvent.setLocation(new SimpleLocation(53.52083331444507, -113.51186385625829));
        moodEventsManager.addMoodEvent(moodEvent);
        moodEventsManager.addMoodEvent(moodEvent1);
        Thread.sleep(5000);
    }

    @After
    public void tearDown() {
        // Use the same method from the lab instructions to clear the emulator's data
        String projectId = "ohey-that-s-my-jam";
        try {
            URL url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("Error", "Failed to clear emulator data", e);
        }
    }

    @UiThreadTest
    @Test
    public void testGetMoodEvents(){
        LiveData<ArrayList<MoodEvent>> allMoodEvents = moodEventsManager.getAllMoodEvents(MoodHistoryFilter.Default(userIds));
        allMoodEvents.observeForever(moodEvents -> {
            assertEquals("testing", moodEvents.getFirst().getReason());
        });
    }

    @UiThreadTest
    @Test
    public void testGetPublicMoodEvents(){
        LiveData<ArrayList<MoodEvent>> allMoodEvents = moodEventsManager.getPublicMoodEvents(null);
        allMoodEvents.observeForever(moodEvents -> {
            assertEquals("testing", moodEvents.getFirst().getReason());
        });
    }

    @UiThreadTest
    @Test
    public void testGetAllMoodEvents() throws InterruptedException {
        LiveData<ArrayList<MoodEvent>> allMoodEvents = moodEventsManager.getAllMoodEvents(MoodHistoryFilter.Default(userIds));
        allMoodEvents.observeForever(moodEvents -> {
            assertEquals("testing", moodEvents.getFirst().getReason());
            assertEquals("testGetAll", moodEvents.get(1).getReason());
        });
    }


}
