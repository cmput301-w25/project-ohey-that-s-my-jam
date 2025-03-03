package com.otmj.otmjapp.Helper;

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
import java.util.Objects;

/**
 * A generic class for managing Firestore database operations for entities extending {@link Entity}.
 *
 * @param <T> The type of entity that extends {@link Entity}.
 */
public class FirestoreDB<T extends Entity> {
    /**
     * Interface for handling asynchronous Firestore operations.
     *
     * @param <T> The type of entity being handled.
     */
    public interface DBCallback<T extends Entity> {
        /**
         * Called when the Firestore operation is successful.
         *
         * @param result The list of retrieved {@link DatabaseObject} instances.
         */
        void onSuccess(ArrayList<DatabaseObject<T>> result);

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
    public void getDocuments(DBCallback<T> callback) {
        getDocuments(null, callback);
    }

    /**
     * Retrieves documents from the Firestore collection based on the specified filter.
     *
     * @param filter   The filter criteria for querying documents.
     * @param callback The callback to handle the operation result.
     */
    public void getDocuments(Filter filter, DBCallback<T> callback) {
        CollectionReference collectionRef = db.collection(collection);

        Task<QuerySnapshot> result;
        if (filter == null) {
            result = collectionRef.get();
        } else {
            result = collectionRef.where(filter).get();
        }

        result.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<DatabaseObject<T>> documents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    DatabaseObject<T> object = new DatabaseObject<>(doc.getId(),
                            (T) T.fromMap(doc.getData()),
                            this);

                    documents.add(object);
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
    public void updateDocument(DatabaseObject<T> document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.set(document.getObject());
    }

    /**
     * Adds a new document to the Firestore collection.
     *
     * @param object   The entity object to be added to Firestore.
     * @param callback The callback to handle the operation result.
     */
    public void addDocument(T object, DBCallback<T> callback) {
        CollectionReference collectionRef = db.collection(collection);

        Task<DocumentReference> returnedRef = null;
        try {
            // Try to use `toMap` function (implemented in `MoodEvent` model class)
            returnedRef = collectionRef.add(object.toMap());
        } catch (UnsupportedOperationException e) {
            // If it is not implemented, just pass in the object
            returnedRef = collectionRef.add(object);
        } finally {
            Objects.requireNonNull(returnedRef).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DatabaseObject<T> dob = new DatabaseObject<>(task.getResult().getId(), object, this);
                    callback.onSuccess((ArrayList<DatabaseObject<T>>) List.of(dob));
                } else {
                    callback.onFailure(task.getException());
                }
            });
        }
    }

    /**
     * Deletes a document from the Firestore collection.
     *
     * @param document The document to be deleted.
     */
    public void deleteDocument(DatabaseObject<T> document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.delete();
    }
}