package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfilePageTest {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private String userId;
    private MoodEventsManager moodEventsManager;
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

    private void addMockMoodEvent(@NonNull UserManager userManager) {
        user = userManager.getCurrentUser();
        userId = user.getID();

        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        moodEventsManager = new MoodEventsManager(userIdList);


        // Mood Event 2
        MoodEvent moodEvent = new MoodEvent(
                userId,
                EmotionalState.Happy,
                SocialSituation.With_1_Other,
                false,
                "On a date",
                null,
                MoodEvent.Privacy.Public
        );


        // Add them all
        moodEventsManager.addMoodEvent(moodEvent);
    }


    public void setUpFollowRelations(User sourceUser, List<User> targetUsers, String relationshipType) {

        for (int i = 0; i < targetUsers.size(); i++) {
            User target = targetUsers.get(i);
            target.setID("mockUser" + i);
            db.collection("users").document(target.getID()).set(target);

            switch (relationshipType) {
                case "followers":
                    Follow follower = new Follow(target.getID(), sourceUser.getID());
                    db.collection("follows").add(follower);
                    break;
                case "following":
                    Follow following = new Follow(sourceUser.getID(), target.getID());
                    db.collection("follows").add(following);
                    break;
                case "requests":
                    FollowRequest followRequest = new FollowRequest(target.getID(), sourceUser.getID());
                    db.collection("follow_requests").add(followRequest);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown relationship type: " + relationshipType);
            }
        }
    }

    public void setUpFollowerList(User currentUser) {
        List<User> followers = Arrays.asList(
                new User("Follower1", "follower1@test.com",
                        "12345678", null),
                new User("Follower2", "follower2@test.com",
                        "12345678", null),
                new User("Follower3", "follower3@test.com",
                        "12345678", null),
                new User("Follower4", "follower4@test.com",
                        "12345678", null)
        );

        setUpFollowRelations(currentUser, followers, "followers");
    }

    public void setUpFollowingList(User currentUser){
        List<User> following = Arrays.asList(
                new User("Following1", "following1@test.com",
                        "12345678", null),
                new User("Following2", "following2@test.com",
                        "12345678", null),
                new User("Following3", "following3@test.com",
                        "12345678", null),
                new User("Following4", "following4@test.com",
                        "12345678", null)
        );

        setUpFollowRelations(currentUser, following, "following");
    }

    public void setUpRequestsList(User currentUser){
        List<User> requests = Arrays.asList(
                new User("Requester1", "requester1@test.com",
                        "12345678", null),
                new User("Requester2", "requester2@test.com",
                        "12345678", null),
                new User("Requester3", "requester3@test.com",
                        "12345678", null),
                new User("Requester4", "requester4@test.com",
                        "12345678", null)
        );
        setUpFollowRelations(currentUser, requests, "requests");
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
    public void showUserProfilePage() throws InterruptedException {
        signupMockUser();
        UserManager userManager = UserManager.getInstance();
        Thread.sleep(2000);
        addMockMoodEvent(userManager);
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.username)).check(matches(withText(usermane)));
        Thread.sleep(2000);
        onView(withText("HAPPY with 1 other")).check(matches(isDisplayed()));
        onView(withText("On a date")).check(matches(isDisplayed()));

    }

    // test for follower and following count display will be implement later
    @Test
    public void showFollowingList() throws InterruptedException {
        signupMockUser();
        UserManager userManager = UserManager.getInstance();
        setUpFollowingList(userManager.getCurrentUser());
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.following_button)).check(matches(withText("4 FOLLOWING")));
        onView(withId(R.id.following_button)).perform(click());
        onView(withText("Following1")).check(matches(isDisplayed()));
        onView(withText("Following2")).check(matches(isDisplayed()));
        onView(withText("Following3")).check(matches(isDisplayed()));
        onView(withText("Following4")).check(matches(isDisplayed()));
    }

    @Test
    public void showFollowersList() throws InterruptedException {
        signupMockUser();
        UserManager userManager = UserManager.getInstance();
        setUpFollowerList(userManager.getCurrentUser());
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.followers_button)).check(matches(withText("4 FOLLOWERS")));
        onView(withId(R.id.followers_button)).perform(click());
        onView(withText("Follower1")).check(matches(isDisplayed()));
        onView(withText("Follower2")).check(matches(isDisplayed()));
        onView(withText("Follower3")).check(matches(isDisplayed()));
        onView(withText("Follower4")).check(matches(isDisplayed()));
    }

    @Test
    public void showRequestsList() throws InterruptedException {
        signupMockUser();
        UserManager userManager = UserManager.getInstance();
        setUpRequestsList(userManager.getCurrentUser());
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.view_requests_button)).check(matches(withText("4 REQUESTS")));
        Thread.sleep(2000);
        onView(withId(R.id.view_requests_button)).perform(click());
        Thread.sleep(5000);
        onView(withText("Requester1")).check(matches(isDisplayed()));
        onView(withText("Requester2")).check(matches(isDisplayed()));
        onView(withText("Requester3")).check(matches(isDisplayed()));
        onView(withText("Requester4")).check(matches(isDisplayed()));
    }

    @Test
    public void logOutTest() throws InterruptedException {
        signupMockUser();
        UserManager userManager = UserManager.getInstance();
        setUpRequestsList(userManager.getCurrentUser());
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
        onView(withText("LOGIN")).check(matches(isDisplayed()));
        onView(withText("SIGN UP")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteMoodEventTest() throws InterruptedException {
        signupMockUser();
        UserManager userManager = UserManager.getInstance();
        addMockMoodEvent(userManager);
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.username)).check(matches(withText(usermane)));
        onView(withId(R.id.my_mood_edit_button)).perform(click());
        onView(withId(R.id.DeletePostButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(1000);
        onView(withText("HAPPY with 1 other")).check(doesNotExist());
    }
}
