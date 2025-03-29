package com.otmj.otmjapp;


import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Models.FirestoreCollections;
import com.otmj.otmjapp.Helper.CommentHandler;
import com.otmj.otmjapp.Models.Comment;
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
import java.util.ArrayList;
import java.util.Objects;

public class CommentHandlerTest {

    @BeforeClass
    public static void setup() {
        String androidLocalhost = "10.0.2.2";

        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);
    }

    private ArrayList<User> mockUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(
                "u1",
                "u1@ualberta.ca",
                "123",
                null)
        );
        users.add(new User(
                "u1",
                "u2@ualberta.ca",
                "456",
                null)
        );

        return users;
    }

    private ArrayList<Comment> mockComments() {
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("u1", "m1", "Hello there"));
        comments.add(new Comment("u2", "m1", "I love your mood!"));
        comments.add(new Comment("u1", "m2", "Ewww bro"));

        return comments;
    }

    @Before
    public void addMockData() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference users = db.collection(FirestoreCollections.Users.name);
        for (User user : mockUsers()) {
            users.document(user.getUsername()).set(user);
        }

        CollectionReference comments = db.collection(FirestoreCollections.Comments.name);
        for (Comment comment : mockComments()) {
            comments.add(comment);
        }

        Thread.sleep(2000);
    }

    @After
    public void clearDB() {
        String projectId = "ohey-that-s-my-jam";
        URL url = null;
        // Create a URL pointing to our local database's documents
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/(default)/documents");
        } catch (MalformedURLException exception) {
            Log.e("URL Error", Objects.requireNonNull(exception.getMessage()));
        }

        if (url != null) {
            // Open a connection to the path specified by `url` and send a DELETE request
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

    @Test
    public void testLoadingComments() throws InterruptedException {
        CommentHandler commentHandler = new CommentHandler();
        ArrayList<Comment> desiredComments = mockComments(),
                correctComments = new ArrayList<>();

        // Load "m1" comments
        commentHandler.loadComments("m1", comment -> {
            // Check that return comment is equal to a desired comment
            for (Comment desired : desiredComments) {
                if (comment.equals(desired)) {
                    correctComments.add(comment);
                    break;
                }
            }
        });

        Thread.sleep(3000); // Wait for function above to finish

        // Check that the number of correct comments is equal to the number of desired comments
        assertEquals(2, correctComments.size());

        // Reset
        correctComments.clear();

        // Load "m2" comments
        commentHandler.loadComments("m2", comment -> {
            // Check that return comment is equal to a desired comment
            for (Comment desired : desiredComments) {
                if (comment.equals(desired)) {
                    correctComments.add(comment);
                    break;
                }
            }
        });

        Thread.sleep(3000); // Wait for function above to finish

        // Check that the number of correct comments is equal to the number of desired comments
        assertEquals(1, correctComments.size());
    }
}
