package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Adapters.CommentAdapter;
import com.otmj.otmjapp.Helper.CommentHandler;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.Comment;
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

public class CommentTest {
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


    private void MockMoodEvent(@NonNull UserManager userManager) {
        user = userManager.getCurrentUser();
        userId = user.getID();
        MoodEvent moodEvent = new MoodEvent(userId,
                EmotionalState.Happy,
                SocialSituation.Alone,
                false,
                "Got ice Cream!",
                null,
                null,
                Privacy.Public);
        ArrayList<String> userIdList = new ArrayList<String>();
        userIdList.add(userId);
        moodEventsManager = new MoodEventsManager(userIdList);
        moodEventsManager.addMoodEvent(moodEvent);
    }

    private void MockMoodEventwithComment(@NonNull UserManager userManager, Context context) {
        user = userManager.getCurrentUser();
        userId = user.getID();
        MoodEvent moodEvent = new MoodEvent(userId, EmotionalState.Happy, SocialSituation.Alone, false, "Got ice Cream!", null, null, Privacy.Public);

        // Add mood event
        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        moodEventsManager = new MoodEventsManager(userIdList);
        moodEventsManager.addMoodEvent(moodEvent);

        // Wait for mood event to be added
        SystemClock.sleep(3000);

        // Ensure we have a valid mood event ID
        String moodEventId = moodEvent.getID();
        if (moodEventId == null) {
            moodEventId = "TestID";
            moodEvent.setID(moodEventId);
        }

        // Create a dummy ListView and adapter
        ListView dummyListView = new ListView(context);
        CommentAdapter commentAdapter = new CommentAdapter(context, new ArrayList<>());
        dummyListView.setAdapter(commentAdapter);

        CommentHandler commentHandler = new CommentHandler();
        Comment[] comments = {
                new Comment(userId, moodEventId, "What flavor?"),
                new Comment(userId, moodEventId, "Nice!")
        };

        // Add comments with extended delays
        for (Comment comment : comments) {
            commentHandler.addComment(comment.getCommentText(), moodEventId, userId, commentAdapter);
            SystemClock.sleep(1000); // Small delay between comment additions
        }

        // Load comments after adding
        commentHandler.loadComments(moodEventId, commentAdapter);
    }

    public void MockUser() throws InterruptedException {
        String username = "Tester";
        String userEmail = "MoodEvent@test.com";
        String userPassword = "12345678";
        onView(withId(R.id.welcome_signup_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.signup_edit_username)).perform(ViewActions.typeText(username));
        onView(withId(R.id.signup_edit_email)).perform(ViewActions.typeText(userEmail));
        onView(withId(R.id.signup_edit_password)).perform(ViewActions.typeText(userPassword));
        onView(withId(R.id.signup_button)).perform(click());
        Thread.sleep(2000);
    }

    //Test that comments are added
    @Test
    public void CommentsAddTest() throws InterruptedException {
        // Sign up
        MockUser();
        UserManager userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        userId = user.getID();

        MockMoodEvent(userManager);
        SystemClock.sleep(5000); // Wait for data to load

        // Go to details page
        onView(withId(R.id.timeline_mood_event_username)).perform(click());

        // Add comment
        onView(withId(R.id.comment_input)).perform(typeText("Nice!"));
        onView(withId(R.id.send_comment_button)).perform(click());

        // Wait for comment and user data to load
        SystemClock.sleep(3000);

        // Check comment is added
        onView(withId(R.id.comment_text)).check(matches(withText("Nice!")));
    }

    //Test that existing comments are viewable
    @Test
    public void ExistingCommentsTest() throws InterruptedException {
        // Sign up and create a user
        MockUser();
        UserManager userManager = UserManager.getInstance();

        // Get the test context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Pass context to create mood event with comments
        MockMoodEventwithComment(userManager, context);

        // Increased and  strategic wait time
        for (int i = 0; i < 20; i++) {
            SystemClock.sleep(1000);

            // Go to details page of the mood event
            try {
                onView(withId(R.id.timeline_mood_event_username)).perform(click());
                break;
            } catch (Exception e) {
                Log.d("CommentTest", "Waiting for mood event to be clickable: " + i);
            }
        }

        // Wait for comments to load
        SystemClock.sleep(5000);

        // Verify comments are displayed
        onView(withId(R.id.comments_list_view)).check(matches(hasDescendant(withText(containsString("What flavor?")))));
        onView(withId(R.id.comments_list_view)).check(matches(hasDescendant(withText(containsString("Nice!")))));
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
