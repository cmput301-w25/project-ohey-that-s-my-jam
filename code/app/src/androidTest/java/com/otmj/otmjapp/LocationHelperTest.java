package com.otmj.otmjapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Helper.LocationHelper;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;


@RunWith(AndroidJUnit4.class)
public class LocationHelperTest {
    private String usermane;
    private String userPassword;
    private String userEmail;

    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new ActivityScenarioRule<InitialActivity>(InitialActivity.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

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

    // requestLocationPermission is not testable because it is use for getting permission for location
    // which it will pop out the system message for getting permission. for this class we assume the location
    // permission is granted by using @Rule
    //    public GrantPermissionRule permissionRule =
    //            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION,
    //                    Manifest.permission.ACCESS_COARSE_LOCATION);

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

    public LocationHelper mockLocationHelper(){
        AppCompatActivity mainActivity = (AppCompatActivity) getCurrentActivity();
        return new LocationHelper(mainActivity);
    }

    @Test
    public void TestGetCurrentLocation() throws InterruptedException {
        signupMockUser();
        Thread.sleep(5000);
        LocationHelper mockHelper = mockLocationHelper();
        mockHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                assertNotNull(location);
                assertTrue(location.getLatitude() != 0.0);
                assertTrue(location.getLongitude() != 0.0);
            }

            @Override
            public void onLocationError(String error) {
                fail("Test failed: Location is not found");
            }
        });
    }

    @Test
    public void TestGetAddressFromLocation() throws InterruptedException {
        signupMockUser();
        Thread.sleep(5000);
        LocationHelper mockHelper = mockLocationHelper();
        Location location = new Location("manualInput");
        location.setLatitude(53.52077149338716);
        location.setLongitude(-113.51190523408);
        mockHelper.getAddressFromLocation(location, new LocationHelper.AddressCallback() {
            @Override
            public void onAddressResult(String country, String state, String city) {
                assertEquals("Canada", country);
                assertEquals("Alberta", state);
                assertEquals("Edmonton", city);
            }

            @Override
            public void onAddressError(String error) {
                fail(error);
            }
        });
    }
}
