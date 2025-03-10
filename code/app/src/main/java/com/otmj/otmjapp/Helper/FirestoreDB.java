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

import java.util.ArrayList;
import java.util.List;

/**
 * A generic class for managing Firestore database operations.
 *
 */
public class FirestoreDB<T extends DatabaseObject> {

    /**
     * Interface for handling asynchronous Firestore operations.
     */
    public interface DBCallback<T> {
        /**
         * Called when the Firestore operation is successful.
         *
         * @param result The list of retrieved {@link Entity} instances.
         */
        void onSuccess(ArrayList<T> result);

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
    public void getDocuments(Class<T> objectClass, DBCallback<T> callback) {
        getDocuments(null, objectClass, callback);
    }

    /**
     * Retrieves documents from the Firestore collection based on the specified filter.
     *
     * @param filter   The filter criteria for querying documents.
     * @param callback The callback to handle the operation result.
     */
    public void getDocuments(Filter filter, Class<T> objectClass, DBCallback<T> callback) {
        CollectionReference collectionRef = db.collection(collection);

        Task<QuerySnapshot> result;
        if (filter == null) {
            result = collectionRef.get();
        } else {
            result = collectionRef.where(filter).get();
        }

        result.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<T> documents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    T obj = doc.toObject(objectClass);
                    assert obj != null;
                    obj.setID(doc.getId());

                    documents.add(obj);
                }

                if (callback != null) {
                    callback.onSuccess(documents);
                }
            } else {
                if (callback != null) {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    /**
     * Updates an existing Firestore document with new data.
     *
     * @param document The document to be updated.
     */
    public void updateDocument(T document) {
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
    public void addDocument(T object, DBCallback<T> callback) {
        CollectionReference collectionRef = db.collection(collection);

        collectionRef.add(object).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                object.setID(task.getResult().getId());
                if (callback != null) {
                    callback.onSuccess(new ArrayList<>(List.of(object)));
                }
            } else {
                if (callback != null) {
                    callback.onFailure(task.getException());
                }
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
