package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Fragments.MapsFragment;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SimpleLocation;
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
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapTest {

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

    public void setUpFollowingList(User currentUser){
        List<User> following = Arrays.asList(
                new User("Following1", "following1@test.com",
                        "12345678", null),
                new User("Following2", "following2@test.com",
                        "12345678", null)
        );

        setUpFollowRelations(currentUser, following, "following");
    }

    private void addMockMoodEvent(@NonNull String dummyId, int numMoodEvent) {
        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(dummyId);
        moodEventsManager = new MoodEventsManager(userIdList);

        for (int i = 0; i<numMoodEvent;i++) {
            // Mood Event 2
            MoodEvent moodEvent = new MoodEvent(
                    userId,
                    EmotionalState.Happy,
                    SocialSituation.With_1_Other,
                    true,
                    "On a date",
                    null,
                    MoodEvent.Privacy.Public
            );
            Activity activity = getCurrentActivity();
            LocationHelper locationHelper = new LocationHelper(activity);
            moodEvent.setLocation(new SimpleLocation(53.52096425661929, -113.51210899712252));
            moodEventsManager.addMoodEvent(moodEvent);
        }
    }

    public static Activity getCurrentActivity() {
        final Activity[] currentActivity = new Activity[1];
        final long startTime = System.currentTimeMillis();
        final long timeout = 5000; // wait up to 5 seconds

        while (currentActivity[0] == null && System.currentTimeMillis() - startTime < timeout) {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                Collection<Activity> resumedActivities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                if (!resumedActivities.isEmpty()) {
                    currentActivity[0] = resumedActivities.iterator().next();
                }
            });
            try {
                Thread.sleep(100); // Sleep a bit before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return currentActivity[0];
    }

    public void getCurrentUser(){
        UserManager userManager = UserManager.getInstance();
        user = userManager.getCurrentUser();
        userId = user.getID();
    }

    public void clickOnAllow() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowButton = device.findObject(new UiSelector().textMatches("(?i)While Using App"));
        if (allowButton.exists() && allowButton.isEnabled()) {
            allowButton.click();
        }
    }

    @Test
    public void testMyMoodEvent() throws InterruptedException, UiObjectNotFoundException {
        signupMockUser();
        getCurrentUser();
        addMockMoodEvent(userId, 3);
        onView(withId(R.id.mapsFragment)).perform(click());
        //clickOnAllow();
        Thread.sleep(5000);
        AppCompatActivity activity = (AppCompatActivity) getCurrentActivity();
        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.mapsFragment);
        System.out.println("Current frgemnt: " + currentFragment.getClass());
        if (currentFragment instanceof MapsFragment) {
            MapsFragment mapsFragment = (MapsFragment) currentFragment;
            ArrayList<Marker> markerList = mapsFragment.getMarkerList();
            assertEquals(3, markerList.size());
        } else {
            fail("Test failed because the condition was not met.");
        }
    }
}
