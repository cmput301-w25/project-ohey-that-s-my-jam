package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.Follow;
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

public class FollowInteractionsTest {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User mockUser, followedUser, unfollowedUser;

    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new ActivityScenarioRule<InitialActivity>(InitialActivity.class);

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

    public void addMoodEventsData(User user) throws InterruptedException {
        String userId = user.getID();
        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        MoodEventsManager moodEventsManager = new MoodEventsManager(userIdList);
        MoodEvent moodEvent1 = new MoodEvent(
                userId,
                EmotionalState.Fear,
                SocialSituation.With_A_Crowd,
                false,
                "Watching a scary movie",
                null,
                null,
                Privacy.Public
        );
        MoodEvent moodEvent2 = new MoodEvent(
                userId,
                EmotionalState.Disgust,
                null,
                false,
                "My cat puked out a huge hairball in front of me",
                null,
                null,
                Privacy.Public
        );
        MoodEvent moodEvent3 = new MoodEvent(
                userId,
                EmotionalState.Shame,
                null,
                false,
                "I can't function without energy drinks",
                null,
                null,
                Privacy.Public
        );
        moodEventsManager.addMoodEvent(moodEvent1);
        Thread.sleep(1000);
        moodEventsManager.addMoodEvent(moodEvent2);
        Thread.sleep(1000);
        moodEventsManager.addMoodEvent(moodEvent3);
    }
    public void insertMockUserToFirestore() {
        mockUser = new User("Tester", "FollowInteractions@test.com", "12345678", null);
        mockUser.setID("mockUser123");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mockUser.getID()).set(mockUser);
    }

    public void setUpMockUserWithUnfollowedUser() throws InterruptedException {
        unfollowedUser = new User("UnfollowedUser", "unfollowedUser@test.com",
                "12345678", null);
        unfollowedUser.setID("unfollowedUserTester");
        db.collection("users").document(unfollowedUser.getID()).set(unfollowedUser);
        addMoodEventsData(unfollowedUser);
        Thread.sleep(3000); // Wait a little to ensure writes are committed
    }

    public void sendFollowRequestFromMockUserToUnfollowedUser() throws InterruptedException {
        insertMockUserToFirestore();
        setUpMockUserWithUnfollowedUser();
        loginAsUser(mockUser);
        onView(withId(R.id.peopleYouMayKnowButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.user_list_view)).atPosition(0).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.requestButton)).perform(click());
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
    }

    public void loginAsUser(User user) throws InterruptedException {
        onView(withId(R.id.welcome_login_button)).perform(click());
        onView(withId(R.id.login_edit_username)).perform(ViewActions.typeText(user.getUsername()));
        onView(withId(R.id.login_edit_password)).perform(ViewActions.typeText(user.getPassword()));
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);
    }

    public void setUpMockUserWithFollowedUser() throws InterruptedException {
        followedUser = new User("FollowedUser", "followedUser@test.com",
                "12345678", null);
        followedUser.setID("followedUserTester");
        db.collection("users").document(followedUser.getID()).set(followedUser);
        Follow follow = new Follow(mockUser.getID(), followedUser.getID());
        db.collection("follows").add(follow);
        addMoodEventsData(followedUser);
        Thread.sleep(3000);
    }

    @Test
    public void followedUserMoodEvents_shouldAppearInTimeline_andInTheirProfileView()
            throws InterruptedException {
        insertMockUserToFirestore();
        setUpMockUserWithFollowedUser();
        loginAsUser(mockUser);
        // check if FollowedUser's mood events appears in the timeline
        Thread.sleep(1000);
        onView(withText("FollowedUser feels Shame 😊")).check(matches(isDisplayed()));
        onView(withText("FollowedUser feels Disgust 😊")).check(matches(isDisplayed()));
        onView(withText("FollowedUser feels Fear 😊 with a crowd")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.timeline_list)).atPosition(0).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.details_profile_picture)).perform(click());
        onView(withId(R.id.username)).check(matches(withText("FollowedUser")));
        // check correct user profile is viewed &unfollow button is present
        Thread.sleep(1000);
        onView(withId(R.id.unfollowButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.blurOverlay)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        // check if FollowedUser's public mood events are visible to the mockUser
        Thread.sleep(1000);
        onData(anything()).inAdapterView(withId(R.id.listview_moodEventList)).atPosition(0)
                .onChildView(withId(R.id.textview_reason)).
                check(matches(withText("I can't function without energy drinks")));
        onData(anything()).inAdapterView(withId(R.id.listview_moodEventList)).atPosition(1)
                .onChildView(withId(R.id.textview_reason)).
                check(matches(withText("My cat puked out a huge hairball in front of me")));
        onData(anything()).inAdapterView(withId(R.id.listview_moodEventList)).atPosition(2)
                .onChildView(withId(R.id.textview_reason)).
                check(matches(withText("Watching a scary movie")));
    }

    @Test
    public void unfollowingAUserShouldRemoveTheirMoodEventsFromTimeline() throws InterruptedException {
        insertMockUserToFirestore();
        setUpMockUserWithFollowedUser();
        loginAsUser(mockUser);
        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.timeline_list)).atPosition(0).perform(click());
        onView(withId(R.id.details_profile_picture)).perform(click());
        onView(withId(R.id.unfollowButton)).perform(click());
        onView(withId(R.id.username)).check(matches(withText("FollowedUser")));
        onView(withId(R.id.unfollowButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.requestButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.timelineFragment)).perform(click());
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withContentDescription("Navigate up")).perform(click());
        Thread.sleep(2000);
        onView(withText("FollowedUser feels Shame 😊")).check(doesNotExist());
        onView(withText("FollowedUser feels Disgust 😊")).check(doesNotExist());
        onView(withText("FollowedUser feels Fear 😊 with a crowd")).check(doesNotExist());
    }

    @Test
    public void viewUnfollowedUserProfile_ShowsBlurredMoodEvents_AndPreventsAccess()
            throws InterruptedException {
        insertMockUserToFirestore();
        setUpMockUserWithUnfollowedUser();
        loginAsUser(mockUser);
        onView(withId(R.id.peopleYouMayKnowButton)).perform(click());
        // check that the unfollowed user is in People You May Know
        onData(anything()).inAdapterView(withId(R.id.user_list_view)).atPosition(0)
                .onChildView(withId(R.id.username)).check(matches(withText("UnfollowedUser")));
        onData(anything()).inAdapterView(withId(R.id.user_list_view)).atPosition(0).perform(click());
        Thread.sleep(1000);
        // check correct user profile is viewed, request button is present & mood event list is blurred
        onView(withId(R.id.username)).check(matches(withText("UnfollowedUser")));
        onView(withId(R.id.requestButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.blurOverlay)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        // check that the mood events in the list are un-clickable
        onData(anything()).inAdapterView(withId(R.id.listview_moodEventList)).atPosition(0).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.details_profile_picture)).check(doesNotExist());
        Thread.sleep(500);
    }

    @Test
    public void sendFollowRequest_shouldAddToTargetUserRequestList() throws InterruptedException {
        sendFollowRequestFromMockUserToUnfollowedUser();
        loginAsUser(unfollowedUser);
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.view_requests_button)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.user_list_view)).atPosition(0)
                .onChildView(withId(R.id.username)).check(matches(withText("Tester")));
    }

    @Test
    public void confirmFollowRequest_shouldGrantAccessToMoodEvents() throws InterruptedException {
        sendFollowRequestFromMockUserToUnfollowedUser();
        loginAsUser(unfollowedUser);
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.view_requests_button)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.user_list_view)).atPosition(0)
                .onChildView(withId(R.id.confirm_request_button)).perform(click());
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withId(R.id.userProfileFragment)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
        loginAsUser(mockUser);
        onView(withId(R.id.timelineFragment)).perform(click());
        // Assert that previously unfollowed user's mood events are now visible
        onView(withText("UnfollowedUser feels Shame 😊")).check(matches(isDisplayed()));
        onView(withText("UnfollowedUser feels Disgust 😊")).check(matches(isDisplayed()));
        onView(withText("UnfollowedUser feels Fear 😊 with a crowd")).check(matches(isDisplayed()));
    }
}
