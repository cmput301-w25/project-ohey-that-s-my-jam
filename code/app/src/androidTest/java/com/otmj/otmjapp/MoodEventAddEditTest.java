package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.otmj.otmjapp.Models.Privacy.Private;
import static org.hamcrest.CoreMatchers.not;

import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.Privacy;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MoodEventAddEditTest {

    private User user;
    private String userId;
    private MoodEventsManager moodEventsManager;

    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new
            ActivityScenarioRule<InitialActivity>(InitialActivity.class);

    @BeforeClass
    public static void setup() {

        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    private void MockPublicMoodEvent(@NonNull UserManager userManager) {
        user = userManager.getCurrentUser();
        userId = user.getID();
        MoodEvent moodEvent = new MoodEvent(userId, EmotionalState.Happy, SocialSituation.Alone, false, "Got ice Cream!", null, Privacy.Public);
        ArrayList<String> userIdList = new ArrayList<String>();
        userIdList.add(userId);
        moodEventsManager = new MoodEventsManager(userIdList);
        moodEventsManager.addMoodEvent(moodEvent);
    }

    private void MockPrivateMoodEvent(@NonNull UserManager userManager){
        user = userManager.getCurrentUser();
        userId= user.getID();
        MoodEvent moodEvent = new MoodEvent(userId, EmotionalState.Sad, SocialSituation.Alone, false, "Ice Cream melted", null, Private);
        ArrayList<String> userIdList = new ArrayList<String>();
        userIdList.add(userId);
        moodEventsManager = new MoodEventsManager(userIdList);
        moodEventsManager.addMoodEvent(moodEvent);
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

    //On the user's profile page, test that mood events are added
    @Test
    public void addMoodEventTest() throws InterruptedException {
        // Sign up user
        MockUser();

        // Go to profile page
        onView(withId(R.id.userProfileFragment)).perform(click());

        // Add mood event
        onView(withId(R.id.add_mood_event_button)).perform(click());
        onView(withId(R.id.anger_chip)).perform(click());
        onView(withId(R.id.reason_why_edit_text))
                .perform(typeText("Heavy traffic"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.chipAlone)).perform(click());
        onView(withId(R.id.privacy_switch)).perform(click());
        onView(withId(R.id.SubmitPostButton)).perform(click());

        // Check if the mood event is added
        SystemClock.sleep(3000);
        onView(withId(R.id.textView_emotionalState)).check(matches(withText("ANGER while alone")));
        onView(withId(R.id.textview_reason)).check(matches(withText("Heavy traffic")));

    }

    //On the user's profile page, test that a mood event can be edited
    @Test
    public void editMoodEventTest() throws InterruptedException {
        // Sign up
        MockUser();
        UserManager userManager = UserManager.getInstance();
        MockPrivateMoodEvent(userManager);

        // Go to profile page
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.my_mood_edit_button)).perform(click());

        // Edit mood
        onView(withId(R.id.disgust_chip)).perform(click());
        onView(withId(R.id.reason_why_edit_text))
                .perform(replaceText("Ice Cream melted :("))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.SubmitPostButton)).perform(click());

        // Check if the mood event is edited
        SystemClock.sleep(3000);

        onView(withId(R.id.textView_emotionalState)).check(matches(withText("DISGUST while alone")));
        onView(withId(R.id.textview_reason)).check(matches(withText("Ice Cream melted :(")));
    }

    //On the timeline page, test that a private mood event doesn't show when added
    @Test
    public void privateMoodEventTestTimelineTest() throws InterruptedException {
        // Sign up
        MockUser();

        // Add mood event
        onView(withId(R.id.add_mood_event_button)).perform(click());
        onView(withId(R.id.happy_chip)).perform(click());
        onView(withId(R.id.reason_why_edit_text))
                .perform(typeText("Watching Youtube"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.chipAlone)).perform(click());
        onView(withId(R.id.SubmitPostButton)).perform(click());

        // Check MoodEvent does not show in timeline page
        onView(withId(R.id.timeline_mood_event_username)).check(matches(not(withText("Tester feels Happy 😊 while alone"))));
        onView(withId(R.id.timeline_mood_event_desc)).check(matches(not(withText("Watching Youtube"))));
    }

    //On the timeline page, test that a public mood event does show when added
    @Test
    public void publicMoodEventTestTimelineTest() throws InterruptedException {
        // Sign up
        MockUser();

        // Add mood event
        onView(withId(R.id.add_mood_event_button)).perform(click());
        onView(withId(R.id.sad_chip)).perform(click());
        onView(withId(R.id.reason_why_edit_text))
                .perform(typeText("Doing assignments"))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.privacy_switch)).perform(click());
        onView(withId(R.id.chipAlone)).perform(click());
        onView(withId(R.id.SubmitPostButton)).perform(click());

        // Check MoodEvent does  show in timeline page
        onView(withId(R.id.timeline_mood_event_username)).check(matches(withText("Tester feels Sad 😊 while alone")));
        onView(withId(R.id.timeline_mood_event_desc)).check(matches(withText("Doing assignments")));
    }

    //On the timeline page, test that data show in the details page is consistent with the actual data in the database
    @Test
    public void DetailPageDataConsistencyTest() throws InterruptedException {
        // Sign up
        MockUser();
        UserManager userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        userId = user.getID();

        MockPublicMoodEvent(userManager);
        SystemClock.sleep(5000);

        onView(withId(R.id.timeline_mood_event_username)).perform(click());

        // Fetch data
        FirebaseFirestore.getInstance()
                .collection("mood_events")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Log.d("FirestoreDebug", "Fetched Mood Event: " + doc.getData());
                        }
                    } else {
                        Log.e("FirestoreDebug", "Firestore query failed", task.getException());
                    }
                });

        // Check if data is consistent with actual data
        SystemClock.sleep(5000); // Extra wait
        onView(withId(R.id.details_emotion_and_social_situation)).check(matches(withText("Tester feels Happy 😊 while alone")));
        onView(withId(R.id.details_reason_why)).check(matches(withText("Got ice Cream!")));
    }


    @After
    public void tearDown() {
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/ohey-that-s-my-jam/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
        } catch (IOException exception) {
            Log.e("IO Error", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
