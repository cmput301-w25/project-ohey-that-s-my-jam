package com.otmj.otmjapp;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.FirestoreCollections;
import com.otmj.otmjapp.Models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserManagerTests {

    private FirestoreDB<User> firestoreDB;
    private UserManager userManager;

    private User existUser;


    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    @Before
    public void setUp() throws InterruptedException {
        // Initialize FirestoreDB with the emulator
        firestoreDB = new FirestoreDB<>(FirestoreCollections.Users.name);
        userManager = new UserManager(firestoreDB); // Use the constructor that accepts FirestoreDB
        firestoreDB.addDocument(new User("testUser", "test@example.com", "123", null), new FirestoreDB.DBCallback<User>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                existUser = result.get(0);
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("fail");
            }
        });
        Thread.sleep(5000);
    }

    @After
    public void tearDown() {
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
    public void testLoginSuccess(){
        userManager.login("testUser", "123", new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                assertEquals("testUser", authenticatedUsers.get(0).getUsername());
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                fail(reason);
            }
        });
    }

    @Test
    public void testLoginFailWithWrongPassword(){
        userManager.login("testUser", "12", new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                fail("wrong password should result in login fail");
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                assertEquals("Wrong username or password", reason);
            }
        });
    }

    @Test
    public void testLoginFailWithWrongUsername(){
        userManager.login("test", "123", new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                fail("wrong username should result in login fail");
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                assertEquals("No such user", reason);
            }
        });
    }

    @Test
    public void testSignUpSuccess(){
        User user = new User("Test1", "test1@example.com","123", null);
        userManager.signup(user, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                assertEquals(user, authenticatedUsers.get(0));
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                fail(reason);
            }
        });
    }

    @Test
    public void testSignUpFail(){
        User user = new User("testUser", "test1@example.com","123", null);
        userManager.signup(user, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                fail("exist username should result signup fail");
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                assertEquals("User already exists", reason);
            }
        });
    }

    @Test
    public void testGetUsersByIdSuccess(){
        ArrayList<String> userIds = new ArrayList<>(List.of(existUser.getID()));
        userManager.getUsers(userIds, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                assertEquals(existUser, authenticatedUsers.get(0));
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                fail(reason);
            }
        });
    }

    @Test
    public void testGetUsersByIdFail(){
        ArrayList<String> userIds = new ArrayList<>(List.of("5635"));
        userManager.getUsers(userIds, new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                fail("not exist userId should be failed");
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                assertEquals("Users not found", reason);
            }
        });
    }

    @Test
    public void testGetUserByNameSuccess(){
        userManager.getUser("testUser", new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                assertEquals(existUser, authenticatedUsers.get(0));
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                fail(reason);
            }
        });
    }

    @Test
    public void testCheckIfUserExistExist(){
        userManager.checkIfUserExists(existUser, new UserManager.CheckCallback() {
            @Override
            public void answer(boolean correct) {
                assertTrue(correct);
            }
        });
    }

    @Test
    public void testCheckIfUserExistNotExist(){
        userManager.checkIfUserExists(new User("user", "user@email.com", "123", null), new UserManager.CheckCallback() {
            @Override
            public void answer(boolean correct) {
                assertFalse(correct);
            }
        });
    }

    @Test
    public void testGetCurrentUser() throws InterruptedException {
        String[] currentUser = new String[1];
        userManager.login("testUser", "123", new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                currentUser[0] = authenticatedUsers.get(0).getUsername();
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                fail(reason);
            }
        });
        Thread.sleep(5000);
        assertEquals(currentUser[0], userManager.getCurrentUser().getUsername());
    }

    @Test
    public void testGetAllUsers() throws InterruptedException {
        firestoreDB.addDocument(new User("testUser1", "test@example.com", "123", null), new FirestoreDB.DBCallback<User>() {
            @Override
            public void onSuccess(ArrayList<User> result) {
                System.out.println("test user add");
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("fail");
            }
        });
        Thread.sleep(5000);

        userManager.getAllUsers(new UserManager.AuthenticationCallback() {
            @Override
            public void onAuthenticated(ArrayList<User> authenticatedUsers) {
                assertEquals("testUser", authenticatedUsers.get(0).getUsername());
                assertEquals("testUser1", authenticatedUsers.get(1).getUsername());
            }

            @Override
            public void onAuthenticationFailure(String reason) {
                fail(reason);
            }
        });
    }







}
