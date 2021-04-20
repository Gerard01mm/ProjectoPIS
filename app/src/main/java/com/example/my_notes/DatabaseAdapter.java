package com.example.my_notes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import Notes.NoteFolder;

public class DatabaseAdapter {
    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;


    public static vmInterface listener;
    public static DatabaseAdapter databaseAdapter;

    public DatabaseAdapter(vmInterface listener){
        this.listener = listener;
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        initFirebase();
    }

    public String getCurrentUser(){
        return user.getEmail();
    }

    public interface vmInterface{
        void setCollection(ArrayList<NoteFolder> ac);
        void setToast(String s);
    }

    public void initFirebase(){

        user = mAuth.getCurrentUser();

        if (user == null) {
            mAuth.signInAnonymously()
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            listener.setToast("Authentication successful.");
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            listener.setToast("Authentication failed.");

                        }
                    }
                });
        }
        else{
            listener.setToast("Authentication with current user.");

        }
    }

    public void getCollectionFolders(){
        Log.d(TAG,"updateFolders");
        db.collection("folders")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<NoteFolder> retrieved_ac = new ArrayList<NoteFolder>() ;
                        for (QueryDocumentSnapshot folder : task.getResult()) {
                            Log.d(TAG, folder.getId() + " => " + folder.getData());
                            retrieved_ac.add(new NoteFolder( folder.getString("title"),
                                folder.getString("owner"), folder.getLong("color").intValue(),
                                folder.getString("id")));
                        }
                        listener.setCollection(retrieved_ac);

                    } else {
                        Log.d(TAG, "Error getting folders: ", task.getException());
                    }
                }
            });
    }

    public void getCollectionFoldersByUser(){
        Log.d(TAG,"updateFoldersByUser");
        db.collection("folders")
                .whereEqualTo("owner", getCurrentUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<NoteFolder> retrieved_ac = new ArrayList<NoteFolder>() ;
                            for (QueryDocumentSnapshot folder : task.getResult()) {
                                Log.d(TAG, folder.getId() + " => " + folder.getData());
                                retrieved_ac.add(new NoteFolder( folder.getString("title"),
                                        folder.getString("owner"), folder.getLong("color").intValue(),
                                        folder.getString("id")));
                            }
                            listener.setCollection(retrieved_ac);

                        } else {
                            Log.d(TAG, "Error getting folders: ", task.getException());
                        }
                    }
                });
    }

    public void saveFolder (String title, String id, String owner, int color) {

        // Create a new user with a first and last name
        Map<String, Object> folder = new HashMap<>();
        folder.put("title", title);
        folder.put("id", id);
        folder.put("owner", owner);
        folder.put("color", color);

        Log.d(TAG, "saveFolder");
        // Add a new document with a generated ID
        db.collection("folders")
            .add(folder)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding folder", e);
                }
            });
    }
}
