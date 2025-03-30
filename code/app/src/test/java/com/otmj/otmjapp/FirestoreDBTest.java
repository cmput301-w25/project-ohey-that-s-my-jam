package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.otmj.otmjapp.Helper.FirestoreDB;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.Privacy;
import com.otmj.otmjapp.Models.SocialSituation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class FirestoreDBTest {
    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockCollectionRef;

    @Mock
    private DocumentReference mockDocRef;

    @Mock
    private Task<QuerySnapshot> mockQueryTask;

    // MoodEvent is used as the generic type but the functionality would be the same for any other permissable generic
    private FirestoreDB<MoodEvent> db;

    @Before
    public void setUp() {
        // initialize FirestoreDB with a mock Firestore instance
        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionRef);
        db = new FirestoreDB<>("moodEvents", mockFirestore);
    }

    private MoodEvent getMockMood() {
        return new MoodEvent(
                "1",
                EmotionalState.Happy,
                SocialSituation.Alone,
                false,
                "Lakers won",
                null,
                null,
                Privacy.Public
        );
    }

    @Test
    public void testGetDocumentsWithoutFilter() {
        MoodEvent mockMood = getMockMood();

        ArrayList<MoodEvent> expectedEvents = new ArrayList<>();
        expectedEvents.add(mockMood);


        when(mockCollectionRef.get()).thenReturn(mockQueryTask);

        FirestoreDB.DBCallback<MoodEvent> callback = new FirestoreDB.DBCallback<MoodEvent>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                assertEquals(expectedEvents.size(), result.size()); // check size of result matches expected size (should be 1)
                assertEquals(expectedEvents.get(0).getUserID(), result.get(0).getUserID()); // check that the user ID matches
            }

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        };

        db.getDocuments(MoodEvent.class, callback);
    }

    @Test
    public void testGetDocumentsWithFilter() {
        MoodEvent mockMoodEvent = getMockMood();

        ArrayList<MoodEvent> expectedEvents = new ArrayList<>();
        expectedEvents.add(mockMoodEvent);

        Filter mockFilter = Filter.equalTo("reason", "Lakers won");

        when(mockCollectionRef.where(mockFilter)).thenReturn(mockCollectionRef);
        when(mockCollectionRef.get()).thenReturn(mockQueryTask);

        FirestoreDB.DBCallback<MoodEvent> callback = new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                // Assert
                assertEquals(expectedEvents.size(), result.size());
                assertEquals(expectedEvents.get(0).getReason(), result.get(0).getReason());
            }

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        };

        db.getDocuments(mockFilter, MoodEvent.class, callback);
    }

    @Test
    public void testAddDocument() {
        MoodEvent mockMoodEvent = getMockMood();

        Task<DocumentReference> mockAddTask = mock(Task.class);
        when(mockCollectionRef.add(mockMoodEvent)).thenReturn(mockAddTask);

        db.addDocument(mockMoodEvent, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                assertEquals(1, result.size());
                assertEquals(EmotionalState.values()[4], result.get(0).getEmotionalState());
            }

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        });
    }

    @Test
    public void testUpdateDocument() {
        // Arrange
        MoodEvent mockMoodEvent = getMockMood();
        mockMoodEvent.setID("abc");

        // add document
        Task<DocumentReference> mockAddTask = mock(Task.class);
        when(mockCollectionRef.add(mockMoodEvent)).thenReturn(mockAddTask);

        db.addDocument(mockMoodEvent, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {/* do nothing */}

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        });

        // change moodEvent's reason
        mockMoodEvent.setReason("Lakers lost");

        when(mockCollectionRef.document("abc")).thenReturn(mockDocRef);
        when(mockCollectionRef.get()).thenReturn(mockQueryTask);

        db.updateDocument(mockMoodEvent);

        // check if 'reason' attribute of document was changed
        db.getDocuments(MoodEvent.class, new FirestoreDB.DBCallback<MoodEvent>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                MoodEvent retrievedEvent = result.get(0);
                assertEquals("Lakers lost", retrievedEvent.getReason()); // Verify the reason was updated
            }

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        });
    }

    @Test
    public void testDeleteDocument() {
        // Arrange
        MoodEvent mockMoodEvent = getMockMood();
        mockMoodEvent.setID("abc");

        // add document
        Task<DocumentReference> mockAddTask = mock(Task.class);
        when(mockCollectionRef.add(mockMoodEvent)).thenReturn(mockAddTask);

        db.addDocument(mockMoodEvent, new FirestoreDB.DBCallback<>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {/* do nothing */}

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        });

        when(mockCollectionRef.document("abc")).thenReturn(mockDocRef);
        when(mockCollectionRef.get()).thenReturn(mockQueryTask);

        db.deleteDocument(mockMoodEvent);

        db.getDocuments(MoodEvent.class, new FirestoreDB.DBCallback<MoodEvent>() {
            @Override
            public void onSuccess(ArrayList<MoodEvent> result) {
                assertTrue(result.isEmpty()); // Document should no longer exist
            }

            @Override
            public void onFailure(Exception e) {
                fail("There shouldn't be an error");
            }
        });

        verify(mockDocRef).delete();
    }
}