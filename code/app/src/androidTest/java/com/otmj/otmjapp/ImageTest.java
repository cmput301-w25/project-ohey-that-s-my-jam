package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.google.firebase.firestore.util.Assert.fail;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.MoodEvent;
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

public class ImageTest {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User mockUser, follower;
    private int imageIndex;

    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new
            ActivityScenarioRule<InitialActivity>(InitialActivity.class);

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        com.google.firebase.firestore.FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @After
    public void tearDown() {
        String projectId = "ohey-that-s-my-jam";
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
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

    private void addMockMoodEventWithoutImage() {
        String userId = mockUser.getID();
        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        MoodEventsManager moodEventsManager = new MoodEventsManager(userIdList);
        // Mood Event
        MoodEvent mockMoodEvent = new MoodEvent(
                userId,
                EmotionalState.Happy,
                SocialSituation.With_1_Other,
                false,
                "On a date",
                null,
                MoodEvent.Privacy.Public
        );
        // Add mood event
        moodEventsManager.addMoodEvent(mockMoodEvent);
    }

    public void insertMockUserToFirestore() {
        mockUser = new User("Tester", "FollowInteractions@test.com", "12345678", null);
        mockUser.setID("mockUser123");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mockUser.getID()).set(mockUser);
    }

    public void loginAsUser(User user) throws InterruptedException {
        onView(withId(R.id.welcome_login_button)).perform(click());
        onView(withId(R.id.login_edit_username)).perform(ViewActions.typeText(user.getUsername()));
        onView(withId(R.id.login_edit_password)).perform(ViewActions.typeText(user.getPassword()));
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);
    }

    public void setUpFollower() {
        follower = new User("Follower1", "follower1@test.com",
                "12345678", null);
        follower.setID("follower123");
        db.collection("users").document(follower.getID()).set(follower);
        Follow followerToMockUser = new Follow(follower.getID(), mockUser.getID());
        db.collection("follows").add(followerToMockUser);
    }

    private void selectImageFromGallery() throws UiObjectNotFoundException {
        onView(withId(R.id.add_image_button)).perform(click());
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject imageThumb = device.findObject(new UiSelector()
                .className("android.widget.ImageView")
                .instance(imageIndex)
        );

        if (!imageThumb.waitForExists(5000)) {
            fail("No image found in gallery to select");
        } else {
            imageThumb.click();
        }
    }

    private void initializeTest() throws InterruptedException{
        insertMockUserToFirestore();
        addMockMoodEventWithoutImage();
        setUpFollower();
        loginAsUser(mockUser);

        onView(withId(R.id.userProfileFragment)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.my_mood_edit_button)).perform(click());
    }

    @Test
    public void givenImageSelected_whenUploadingImage_thenImageIsDisplayedAndUrlIsSaved()
            throws InterruptedException, UiObjectNotFoundException {
        initializeTest();
        imageIndex = 1;
        selectImageFromGallery();

        // give time for the image to upload and be displayed
        Thread.sleep(3000);

        // verify the image is displayed across the app
        onView(withId(R.id.image_container)).check(matches(isDisplayed()));
        onView(withId(R.id.SubmitPostButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.reason_why_image)).check(matches(isDisplayed()));
        onView(withId(R.id.logoutButton)).perform(click());

        // check that the image is visible for the user's followers
        loginAsUser(follower);
        Thread.sleep(1000);
        onView(withId(R.id.mood_image)).check(matches(isDisplayed()));
    }

    @Test
    public void givenAttachedImage_whenUserRemovesImage_thenImageIsDetachedFromMoodEvent()
            throws InterruptedException, UiObjectNotFoundException {
        initializeTest();
        imageIndex = 1;
        selectImageFromGallery();

        // give time for the image to upload and be displayed
        Thread.sleep(3000);

        onView(withId(R.id.SubmitPostButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.my_mood_edit_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.remove_image_button)).perform(click());

        // check that the image is detached
        onView(withId(R.id.image_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        Thread.sleep(2000);
        onView(withId(R.id.SubmitPostButton)).perform(click());
        onView(withId(R.id.reason_why_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        Thread.sleep(2000);
        onView(withId(R.id.logoutButton)).perform(click());

        // check that the image is also detached for the user's followers POV
        loginAsUser(follower);
        Thread.sleep(2000);
        onView(withId(R.id.mood_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void givenImageExceedsSizeLimit_whenUploading_thenUploadFails()
            throws InterruptedException, UiObjectNotFoundException {
        initializeTest();
        imageIndex = 2;
        selectImageFromGallery();

        // check that the image did not upload anywhere for the current user's POV
        onView(withId(R.id.image_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.SubmitPostButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.reason_why_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.logoutButton)).perform(click());

        // check that the image did not upload for the user's followers POV
        loginAsUser(follower);
        Thread.sleep(2000);
        onView(withId(R.id.mood_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
