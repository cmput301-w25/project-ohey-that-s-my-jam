package com.otmj.otmjapp.Helper;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.otmj.otmjapp.Models.DatabaseObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FirestoreDB implements DB {
    private final FirebaseFirestore db;
    public FirestoreDB() {
        db = FirebaseFirestore.getInstance();
    }

    // write javadoc
    public <T extends DatabaseObject> ArrayList<T> getDocuments(String collection, Class<T> type)  {
        ArrayList<T> documents = new ArrayList<>();

        CollectionReference collectionRef = db.collection(collection);
        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot document : queryDocumentSnapshots) {
                T object = null;

                try {
                    object = type.getDeclaredConstructor(String.class, FirestoreDB.class).newInstance(document.getId(), this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                documents.add(object);
            }
        });

        return documents;
    }

    // write javadoc
    public DocumentSnapshot getDocument(String collection, String id) {
        DocumentReference docRef = db.collection(collection).document(id);

        Task<DocumentSnapshot> task = docRef.get();

        try {
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    // write javadoc
    public void updateDocument(String collection, String id, DatabaseObject document) {
        CollectionReference collectionRef = db.collection(collection);

        DocumentReference docRef = collectionRef.document(id);
        docRef.set(document);
    }

    // write javadoc
    public void addDocument(String collection, DatabaseObject document) {
        CollectionReference collectionRef = db.collection(collection);
        String id = collectionRef.document().getId();

        DocumentReference docRef = collectionRef.document(id);
        docRef.set(document);
    }

    // write javadoc
    public void deleteDocument(String collection, String id) {
        CollectionReference collectionRef = db.collection(collection);

        DocumentReference docRef = collectionRef.document(id);
        docRef.delete();
    }
}