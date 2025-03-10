package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Controllers.MoodEventController;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.User;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfilePageTest {
    private User user;
    private String userId;
    //private UserManager userManager;
    private MoodEventController moodEventController;
    private String usermane;
    private String userEmail;
    private String userPassword;


    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new ActivityScenarioRule<InitialActivity>(InitialActivity.class);

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
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

    private void addMockMoodEvent(@NonNull UserManager userManager){
        user = userManager.getCurrentUser();
        userId= user.getID();
        MoodEvent moodEvent = new MoodEvent(userId, "Anger", "not happy", "Alone", false, "Tired", null);
        ArrayList<String> userIdList = new ArrayList<String>();
        userIdList.add(userId);
        moodEventController = new MoodEventController(userIdList);
        moodEventController.addMoodEvent(moodEvent);
    }

    private void signupMockUser() throws InterruptedException {
        usermane = "ProfilePageTester";
        userEmail = "ProfilePageTester@test.com";
        userPassword = "12345678";
        onView(withId(R.id.welcome_signup_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.signup_edit_username)).perform(ViewActions.typeText(usermane));
        onView(withId(R.id.signup_edit_email)).perform(ViewActions.typeText(userEmail));
        onView(withId(R.id.signup_edit_password)).perform(ViewActions.typeText(userPassword));
        onView(withId(R.id.signup_button)).perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void showUserProfileMoodEvents() throws InterruptedException {
        signupMockUser();
        UserManager userManger = UserManager.getInstance();
        addMockMoodEvent(userManger);
        Thread.sleep(2000);
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.username)).check(matches(withText(usermane)));
        onView(withId(R.id.textView_emotionalState)).check(matches(withText("Anger")));
        onView(withId(R.id.textview_feeling)).check(matches(withText("Feeling: Anger")));
        onView(withId(R.id.textview_trigger)).check(matches(withText("Trigger: not happy")));
        onView(withId(R.id.textview_socialStatus)).check(matches(withText("Social Situation: Alone")));
    }

    // test for follower and following count display will be implement later



}
