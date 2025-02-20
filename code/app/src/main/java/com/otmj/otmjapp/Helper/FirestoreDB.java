package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.otmj.otmjapp.Models.DatabaseObject;

import java.util.ArrayList;
public class FirestoreDB implements DB {
    private final FirebaseFirestore db;
    public FirestoreDB() {
        db = FirebaseFirestore.getInstance();
    }

    // write javadoc
    public ArrayList<DatabaseObject> getDocuments(String collection) {
        ArrayList<DatabaseObject> documents = new ArrayList<>();

        CollectionReference collectionRef = db.collection(collection);
        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot document : queryDocumentSnapshots) {
                DatabaseObject object = new DatabaseObject(document.getId());

                documents.add(object);
            }
        });

        return documents;
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