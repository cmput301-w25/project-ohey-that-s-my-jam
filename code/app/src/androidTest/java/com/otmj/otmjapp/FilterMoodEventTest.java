package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;
import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FilterMoodEventTest {

    private User user;
    private String userId;
    private MoodEventsManager moodEventsManager;
    private List<MoodEvent> testMoodEvents;

    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new
            ActivityScenarioRule<>(InitialActivity.class);

    @BeforeClass
    public static void setup() {

        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void setupMoodEvent() throws InterruptedException {

        // Create test user and mood events
        MockUser();
        UserManager userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        userId = user.getID();

        // Initialize MoodEventsManager
        moodEventsManager = new MoodEventsManager(List.of(userId));

        // Create test mood events
        testMoodEvents = new ArrayList<>();

        // Recent happy event
        MoodEvent recentHappy = new MoodEvent(
                userId,
                EmotionalState.Happy,
                SocialSituation.With_1_Other,
                false,
                "Got promoted at work!",
                null,
                MoodEvent.Privacy.Public
        );
        recentHappy.setCreatedDate(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L));
        testMoodEvents.add(recentHappy);

        // Older sad event
        MoodEvent olderSad = new MoodEvent(
                userId,
                EmotionalState.Sad,
                SocialSituation.Alone,
                false,
                "Missed my flight",
                null,
                MoodEvent.Privacy.Public
        );
        olderSad.setCreatedDate(new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L));
        testMoodEvents.add(olderSad);

        // Recent anger event
        MoodEvent recentAnger = new MoodEvent(
                userId,
                EmotionalState.Anger,
                SocialSituation.With_2_Others,
                false,
                "Traffic was terrible today",
                null,
                MoodEvent.Privacy.Public
        );
        recentAnger.setCreatedDate(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L));
        testMoodEvents.add(recentAnger);

        // Add test data to Firestore
        addTestDataToFirestore();
    }

    private void addTestDataToFirestore() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(testMoodEvents.size());

        for (MoodEvent event : testMoodEvents) {
            moodEventsManager.addMoodEvent(event);
            latch.countDown();
        }

        latch.await(5, TimeUnit.SECONDS);
        SystemClock.sleep(2000); // Additional delay for UI to update
    }

    public void MockUser() throws InterruptedException {
        String username = "Tester";
        String userEmail = "MoodEvent@test.com";
        String userPassword = "12345678";
        onView(withId(R.id.welcome_signup_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.signup_edit_username)).perform(ViewActions.typeText(username));
        onView(withId(R.id.signup_edit_email)).perform(ViewActions.typeText(userEmail));
        onView(withId(R.id.signup_edit_password))
                .perform(ViewActions.typeText(userPassword))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.signup_button)).perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void testRecentWeekFilter() throws InterruptedException {
        SystemClock.sleep(3000);

        // Open filter dialog
        onView(withId(R.id.filter_button)).perform(click());

        // Select Recent Week chip
        onView(withId(R.id.recent_week_chip)).perform(click());

        // Apply filters
        onView(withId(R.id.close_fragment_button)).perform(click());

        // Verify only recent events are shown (Happy and Anger)
        onView(withText("Traffic was terrible today")).check(matches(isDisplayed()));
        onView(withText("Got promoted at work!")).check(matches(isDisplayed()));

        // Verify older event is not shown
        onView(withText("Missed my flight")).check(doesNotExist());
    }

    @Test
    public void testEmotionalStateFilter() throws InterruptedException {
        SystemClock.sleep(3000);

        // Open filter dialog
        onView(withId(R.id.filter_button)).perform(click());

        // Select emotional states (Happy and Anger)
        onView(withId(R.id.happy_chip)).perform(click());
        onView(withId(R.id.anger_chip)).perform(click());

        // Apply filters
        onView(withId(R.id.close_fragment_button)).perform(click());

        // Verify correct events are shown
        onView(withText("Got promoted at work!")).check(matches(isDisplayed()));
        onView(withText("Traffic was terrible today")).check(matches(isDisplayed()));

        // Verify other events are not shown
        onView(withText("Missed my flight")).check(doesNotExist());
    }

    @Test
    public void testReasonTextFilter() throws InterruptedException {
        SystemClock.sleep(3000);

        // Open filter dialog
        onView(withId(R.id.filter_button)).perform(click());

        // Enter search text
        onView(withId(R.id.filter_reason_text))
                .perform(typeText("promoted"))
                .perform(closeSoftKeyboard());

        // Apply filters
        onView(withId(R.id.close_fragment_button)).perform(click());

        // Verify matching event is shown
        onView(withText("Got promoted at work!")).check(matches(isDisplayed()));

        // Verify non-matching events are not shown
        onView(withText("Missed my flight")).check(doesNotExist());
        onView(withText("Traffic was terrible today")).check(doesNotExist());
    }

    @After
    public void tearDown() {
        try {
            URL url = new URL("http://10.0.2.2:8080/emulator/v1/projects/ohey-that-s-my-jam/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
            urlConnection.disconnect();
        } catch (Exception exception) {
            Log.e("Cleanup Error", Objects.requireNonNull(exception.getMessage()));
        }
    }
}