package com.otmj.otmjapp.Helper;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;
import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A generic class for managing Firestore database operations.
 *
 */
public class FirestoreDB {

    /**
     * Interface for handling asynchronous Firestore operations.
     */
    public interface DBCallback {
        /**
         * Called when the Firestore operation is successful.
         *
         * @param result The list of retrieved {@link Entity} instances.
         */
        void onSuccess(ArrayList<Entity> result);

        /**
         * Called when the Firestore operation fails.
         *
         * @param e The exception that caused the failure.
         */
        void onFailure(Exception e);
    }

    private final FirebaseFirestore db;
    private final String collection;

    public FirestoreDB(String collection) {
        this.collection = collection;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves all documents from the Firestore collection without any filters.
     *
     * @param callback The callback to handle the operation result.
     */
    public void getDocuments(DBCallback callback) {
        getDocuments(null, callback);
    }

    /**
     * Retrieves documents from the Firestore collection based on the specified filter.
     *
     * @param filter   The filter criteria for querying documents.
     * @param callback The callback to handle the operation result.
     */
    public void getDocuments(Filter filter, DBCallback callback) {
        CollectionReference collectionRef = db.collection(collection);

        Task<QuerySnapshot> result;
        if (filter == null) {
            result = collectionRef.get();
        } else {
            result = collectionRef.where(filter).get();
        }

        result.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Entity> documents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    documents.add(new Entity(doc.getId(), doc.getData()));
                }

                callback.onSuccess(documents);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Updates an existing Firestore document with new data.
     *
     * @param document The document to be updated.
     */
    public <T extends DatabaseObject> void updateDocument(T document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.set(document);
    }

    /**
     * Adds a new document to the Firestore collection.
     *
     * @param object   The entity object to be added to Firestore.
     * @param callback The callback to handle the operation result.
     */
    public <T extends DatabaseObject> void addDocument(T object, DBCallback callback) {
        CollectionReference collectionRef = db.collection(collection);

        collectionRef.add(object).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Entity entity = new Entity(task.getResult().getId(), object.toMap());
                callback.onSuccess(new ArrayList<>(List.of(entity)));
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Deletes a document from the Firestore collection.
     *
     * @param document The document to be deleted.
     */
    public void deleteDocument(DatabaseObject document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.delete();
    }
}