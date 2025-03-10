package com.otmj.otmjapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class InitialScreenTest {
    @Rule
    public ActivityScenarioRule<InitialActivity> scenario = new
            ActivityScenarioRule<InitialActivity>(InitialActivity.class);

    @BeforeClass
    public static void setup() {

        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        User[] users = {
                new User("username1", "username1@mail.com", "user1password", "n/a"),
        };
        for (User user : users) {
            usersRef.document().set(user);
        }
    }

    // Test for logging in
    @Test
    public void testlogin() {
        // Click login  button
        onView(withId(R.id.welcome_login_button)).perform(click());

        // Enter information
        onView(withId(R.id.login_edit_username )).perform(typeText("username1"));
        onView(withId(R.id.login_edit_password)).perform(typeText("user1password"));

        // Login
        onView(withId(R.id.login_button)).perform(click());

        // Test if successful login leads to for you page
        onView(withText("RECENT EVENTS")).check(matches(isDisplayed()));
    }

    // Test for signing in
    @Test
    public void testsignup() {
        // Click sign up button
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information
        onView(withId(R.id.signup_edit_username)).perform(typeText("username2"));
        onView(withId(R.id.signup_edit_email)).perform(typeText("username2@mail.com"));
        onView(withId(R.id.signup_edit_password)).perform(typeText("user2password"));

        // Signup
        onView(withId(R.id.signup_button)).perform(click());

        // Test if successful login leads to for you page
        onView(withText("RECENT EVENTS")).check(matches(isDisplayed()));
    }

    // Test for invalid input in login: no username
    @Test
    public void testblankusernamelogin() throws Error{
        // Go to login screen
        onView(withId(R.id.welcome_login_button)).perform(click());

        // Enter information with no username
        onView(withId(R.id.login_edit_username )).perform(typeText(""));
        onView(withId(R.id.login_edit_password)).perform(typeText("user1password"));

        // Press login button
        onView(withId(R.id.login_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.login_edit_username)).check(matches(hasErrorText("This field cannot be blank")));
    }

    // Test for invalid input in login: invalid username
    @Test
    public void testinvalidusernamelogin() throws Error{
        // Go to login screen
        onView(withId(R.id.welcome_login_button)).perform(click());

        // Enter information with invalid username
        onView(withId(R.id.login_edit_username )).perform(typeText("user2"));
        onView(withId(R.id.login_edit_password)).perform(typeText("user1password"));

        // Press login button
        onView(withId(R.id.login_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withText("No such user")).check(matches(isDisplayed()));
    }

    // Test for invalid input in login: no password
    @Test
    public void testblankpasswordlogin() throws Error{
        // Go to login screen
        onView(withId(R.id.welcome_login_button)).perform(click());

        // Enter information with no password
        onView(withId(R.id.login_edit_username )).perform(typeText("username1"));
        onView(withId(R.id.login_edit_password)).perform(typeText(""));

        // Press login button
        onView(withId(R.id.login_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.login_edit_password)).check(matches(hasErrorText("This field cannot be blank")));
    }

    // Test for invalid input in login: invalid username
    @Test
    public void testinvalidpasswordlogin() throws Error{
        // Go to login screen
        onView(withId(R.id.welcome_login_button)).perform(click());

        // Enter information with invalid password
        onView(withId(R.id.login_edit_username )).perform(typeText("username1"));
        onView(withId(R.id.login_edit_password)).perform(typeText("abcd"));

        // Press login button
        onView(withId(R.id.login_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withText("Wrong username or password")).check(matches(isDisplayed()));
    }

    // Test for invalid input in signup: no username
    @Test
    public void testblankusernamesignup() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with no username
        onView(withId(R.id.signup_edit_username )).perform(typeText(""));
        onView(withId(R.id.signup_edit_email)).perform(typeText("username2@mail.com"));
        onView(withId(R.id.signup_edit_password)).perform(typeText("user2password"));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.signup_edit_username)).check(matches(hasErrorText("This field cannot be blank")));
    }

    // Test for invalid input in signup: username less than 3 characters
    @Test
    public void testinvalidusernamesignup() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with no username
        onView(withId(R.id.signup_edit_username )).perform(typeText("1"));
        onView(withId(R.id.signup_edit_email)).perform(typeText("username2@mail.com"));
        onView(withId(R.id.signup_edit_password)).perform(typeText("user2password"));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.signup_edit_username)).check(matches(hasErrorText("Username must be at least 3 characters long")));
    }

    // Test for invalid input in signup: no username
    @Test
    public void testexistingusersingup() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with no username
        onView(withId(R.id.signup_edit_username )).perform(typeText("username1"));
        onView(withId(R.id.signup_edit_email)).perform(typeText("username2@mail.com"));
        onView(withId(R.id.signup_edit_password)).perform(typeText("user2password"));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withText("User already exists")).check(matches(isDisplayed()));
    }

    // Test for invalid input in signup: no email
    @Test
    public void testblankemail() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with no email
        onView(withId(R.id.signup_edit_username )).perform(typeText("username2"));
        onView(withId(R.id.signup_edit_email)).perform(typeText(""));
        onView(withId(R.id.signup_edit_password)).perform(typeText("user2password"));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.signup_edit_email)).check(matches(hasErrorText("This field cannot be blank")));
    }

    // Test for invalid input in signup: invalid email
    @Test
    public void testinvalidemail() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with invalid email
        onView(withId(R.id.signup_edit_username )).perform(typeText("username2"));
        onView(withId(R.id.signup_edit_email)).perform(typeText("hello"));
        onView(withId(R.id.signup_edit_password)).perform(typeText("user2password"));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.signup_edit_email)).check(matches(hasErrorText("Invalid email address")));
    }

    // Test for invalid input in signup: no password
    @Test
    public void testnopasswordsignup() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with no password
        onView(withId(R.id.signup_edit_username )).perform(typeText("username2"));
        onView(withId(R.id.signup_edit_email)).perform(typeText("username2@mail.com"));
        onView(withId(R.id.signup_edit_password)).perform(typeText(""));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.signup_edit_password)).check(matches(hasErrorText("This field cannot be blank")));
    }

    // Test for invalid input in signup: password less than 8 characters
    @Test
    public void testinvalidpasswordsignup() throws Error{
        // Go to signup screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Enter information with invalid password
        onView(withId(R.id.signup_edit_username )).perform(typeText("username2"));
        onView(withId(R.id.signup_edit_email)).perform(typeText("username2@mail.com"));
        onView(withId(R.id.signup_edit_password)).perform(typeText("1"));

        // Press sign up button
        onView(withId(R.id.signup_button)).perform(click());

        // Test if it successfully shows an error message
        onView(withId(R.id.signup_edit_password)).check(matches(hasErrorText("Password must be at least 8 characters long")));
    }

    //Test don't have an account leads to signup page
    @Test
    public void testdontaccount () {
        // Go to login screen
        onView(withId(R.id.welcome_login_button)).perform(click());

        // Press don't have an account button
        onView(withId(R.id.login_create_account)).perform(click());

        // Test if it leads to signup screen
        onView(withText("SIGN UP")).check(matches(isDisplayed()));
    }

    //Test already have an account
    @Test
    public void testalradyaccount () {
        // Go to sign up screen
        onView(withId(R.id.welcome_signup_button)).perform(click());

        // Press already have an account button
        onView(withId(R.id.signup_login)).perform(click());

        // Test if it leads to login screen
        onView(withText("LOGIN")).check(matches(isDisplayed()));
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