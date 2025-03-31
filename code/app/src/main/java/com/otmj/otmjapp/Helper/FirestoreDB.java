package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.Query;
import com.otmj.otmjapp.Models.DBSortOption;
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
         * @param result The list of retrieved objects.
         */
        void onSuccess(ArrayList<T> result);

        /**
         * Called when the Firestore operation fails.
         *
         * @param e The exception that caused the failure.
         */
        void onFailure(Exception e);
    }

    /**
     * Allows a class to listen for updates to their collection
     */
    public interface DBListener {
        void onUpdate();
    }

    private final FirebaseFirestore db;
    private final String collection;

    public FirestoreDB(String collection) {
        this.collection = collection;
        this.db = FirebaseFirestore.getInstance();
    }

    public FirestoreDB(String collection, FirebaseFirestore db) { // Constructor with custom Firestore instance for testing
        this.collection = collection;
        this.db = db;
    }

    public void addCollectionListener(DBListener listener) {
        db.collection(collection).addSnapshotListener((snapshot, exception) -> listener.onUpdate());
    }

    /**
     * Retrieves all documents from the Firestore collection without any filters.
     *
     * @param objectClass   The class of the objects to return
     * @param callback The callback to handle the operation result.
     * @param sortOptions   The way to order the returned data
     * @see #getDocuments(Filter, Class, DBCallback, DBSortOption...)
     */
    public void getDocuments(Class<T> objectClass,
                             DBCallback<T> callback,
                             DBSortOption... sortOptions) {
        getDocuments(null, objectClass, callback, sortOptions);
    }

    /**
     * Retrieves documents from the Firestore collection based on the specified filter.
     *
     * @param filter        The filter criteria for querying documents.
     * @param objectClass   The class of the objects to return
     * @param callback      The callback to handle the operation result.
     * @param sortOptions   The way to order the returned data
     */
    public void getDocuments(Filter filter,
                             Class<T> objectClass,
                             DBCallback<T> callback,
                             DBSortOption... sortOptions) {
        CollectionReference collectionRef = db.collection(collection);

        Query ref = collectionRef;
        // Add filter, if it exists
        if (filter != null) {
            ref = collectionRef.where(filter);
        }
        // Order by the given sort options
        if (sortOptions != null) {
            for (DBSortOption sort : sortOptions) {
                ref = sort.getSorting(ref);
            }
        }

        ref.get().addOnCompleteListener(task -> {
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
