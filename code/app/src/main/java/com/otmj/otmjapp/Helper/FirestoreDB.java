package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.otmj.otmjapp.Models.DatabaseObject;

import java.util.ArrayList;

public class FirestoreDB implements DB {

    public interface DBCallback {
        void onSuccess(DatabaseObject object);
        void onSuccess(ArrayList<DatabaseObject> result);
        void onFailure(Exception e);
    }

    private final FirebaseFirestore db;
    private final String collection;

    public FirestoreDB(String collection) {
        this.collection = collection;
        this.db = FirebaseFirestore.getInstance();
    }

    // TODO: write javadoc
    public void getDocuments(DBCallback callback) {
        CollectionReference collectionRef = db.collection(collection);
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<DatabaseObject> documents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    DatabaseObject object = new DatabaseObject(doc.getId(),
                            Entity.fromMap(doc.getData()),
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
    public void updateDocument(DatabaseObject document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.set(document.getObject());
    }

    // TODO: write javadoc
    public <T extends Entity> void addDocument(T object, DBCallback callback) {
        CollectionReference collectionRef = db.collection(collection);
        collectionRef.add(object).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(new DatabaseObject(task.getResult().getId(), object, this));
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    // TODO: write javadoc
    public void deleteDocument(DatabaseObject document) {
        CollectionReference collectionRef = db.collection(collection);
        DocumentReference docRef = collectionRef.document(document.getID());

        docRef.delete();
    }
}