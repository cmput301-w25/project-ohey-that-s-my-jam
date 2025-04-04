package com.otmj.otmjapp;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.otmj.otmjapp.Models.FirestoreCollections;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(AndroidJUnit4.class) // Use AndroidJUnit4 for instrumented tests
public class UserManagerTest {

    private UserManager userManager;
    private FirestoreDB<User> firestoreDB;

    @Before
    public void setUp() {
        // Initialize FirestoreDB with the emulator
        firestoreDB = new FirestoreDB<>(FirestoreCollections.Users.name);
        userManager = new UserManager(firestoreDB); // Use the constructor that accepts FirestoreDB
    }

    @After
    private void tearDown() {
        // Use the same method from the lab instructions to clear the emulator's data
        String projectId = "ohey-that-s-my-jam";
        try {
            URL url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            int response = urlConnection.getResponseCode();
            Log.i("Response Code", "Response Code: " + response);
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("Error", "Failed to clear emulator data", e);
        }
    }

    @Test
    public void testLoginSuccess() {
    }
}