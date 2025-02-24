package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.otmj.otmjapp.Models.DatabaseObject;
import com.otmj.otmjapp.Models.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirestoreDB<T extends Entity> {

    public interface DBCallback<T extends Entity> {
        void onSuccess(ArrayList<DatabaseObject<T>> result);
        void onFailure(Exception e);
    }

    private final FirebaseFirestore db;
    private final String collection;

    public FirestoreDB(String collection) {
        this.collection = collection;
        this.db = FirebaseFirestore.getInstance();
    }

    // TODO: write javadoc
    public void getDocuments(DBCallback<T> callback) {
        CollectionReference collectionRef = db.collection(collection);
        collectionRef.get().addOnCompleteListener(task -> {
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

    // TODO: write javadoc
    public void updateDocument(DatabaseObject<T> document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.set(document.getObject());
    }

    // TODO: write javadoc
    public void addDocument(T object, DBCallback<T> callback) {
        CollectionReference collectionRef = db.collection(collection);
        collectionRef.add(object).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseObject<T> dob = new DatabaseObject<>(task.getResult().getId(), object, this);
                callback.onSuccess((ArrayList<DatabaseObject<T>>) List.of(dob));
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    // TODO: write javadoc
    public void deleteDocument(DatabaseObject<T> document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.delete();
    }
}