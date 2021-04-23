package com.example.my_notes;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.my_notes.ArrayListComparator.DateSorter;
import com.example.my_notes.Reminders.Reminder;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import Notes.ImageNote;
import Notes.Note;
import Notes.NoteFolder;
import Notes.TextNote;

public class DatabaseAdapter {
    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;


    public static vmInterface listener;
    public static DatabaseAdapter databaseAdapter;

    private ArrayList<Note> notesInFolder;

    public DatabaseAdapter(vmInterface listener){
        this.listener = listener;
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        initFirebase();
        notesInFolder = new ArrayList<>();
    }

    public interface vmInterface{
        void setCollection(ArrayList ac);
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

    public String getCurrentUser(){
        return user.getEmail();
    }

    public void getCollectionFolders(){
        Log.d(TAG,"getFolders");
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
        Log.d(TAG,"getFoldersByUser");
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
                    String f_id = documentReference.getId();
                    Log.d(TAG, "DocumentSnapshot added with ID: " + f_id);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding folder", e);
                }
            });
    }



    public void updateFolder (String title, String id, String owner, int color) {
        final DocumentReference[] docRef = new DocumentReference[1];
        // Create a new hashmap
        Map<String, Object> newfolder = new HashMap<>();
        newfolder.put("title", title);
        newfolder.put("id", id);
        newfolder.put("owner", owner);
        newfolder.put("color", color);
        System.out.println(id);
        Log.d(TAG, "updateFolder");
        db.collection("folders")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot folder : task.getResult()) {
                                docRef[0] = folder.getReference();
                                docRef[0].update(newfolder)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Document updated correctly");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Error updating document ");
                                            }
                                        });
                            }

                        } else {
                            Log.d(TAG, "Error: No folder found ", task.getException());
                        }
                    }
                });

    }

    public void deleteFolder (String id) {
        db.collection("folders")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot folder : task.getResult()) {
                            folder.getReference().delete();
                        }

                    } else {
                        Log.d(TAG, "Error deleting folder: ", task.getException());
                    }
                }
                });
    }


    public void saveReminder(String title, String id, String owner, String date, String description){
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);
        reminder.put("id", id);
        reminder.put("owner", owner);
        reminder.put("description", description);

        Log.d(TAG, "saveReminder");

        db.collection("reminders")
            .add(reminder)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String f_id = documentReference.getId();
                    Log.d(TAG, "Document Snapshot added with ID: " + f_id);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding remidner", e);
                }
            });
    }


    public void updateReminder(String title, String id, String owner, String date, String description){
        final DocumentReference [] docRef = new DocumentReference[1];

        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);
        reminder.put("id", id);
        reminder.put("owner", owner);
        reminder.put("description", description);

        Log.d(TAG, "updateReminder");

        db.collection("reminders")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot folder : task.getResult()){
                                docRef[0] = folder.getReference();
                                docRef[0].update(reminder)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "Reminder updated correctly");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Error pdating reminder");
                                            }
                                        });
                            }
                        }
                        else{
                            Log.d(TAG, "Error: No reminder found", task.getException());
                        }
                    }
                });
    }


    public void deleteReminder(String id){
        db.collection("reminders")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot reminder : task.getResult()) {
                                reminder.getReference().delete();
                            }
                        }
                        else{
                            Log.d(TAG, "Error deleting reminder: ", task.getException());
                        }
                    }
                });
    }


    public void getCollectionReminderByUserAndDay(String date){
        Log.d(TAG, "getRemindersByUserAndDay");
        db.collection("reminders")
                .whereEqualTo("owner", getCurrentUser())
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<Reminder> retrieved_ac = new ArrayList<>();
                            for (QueryDocumentSnapshot reminder : task.getResult()){
                                Log.d(TAG, reminder.getId() + " => " + reminder.getData());
                                retrieved_ac.add(new Reminder(reminder.getString("title"),
                                        reminder.getString("description"),
                                        reminder.getString("alert"),
                                        reminder.getString("date"),
                                        reminder.getString("id"),
                                        reminder.getString("owner")));
                            }

                            listener.setCollection(retrieved_ac);
                        }
                        else{
                            Log.d(TAG,"Error getting reminders: ", task.getException());
                        }
                    }
                });
    }

    public void getCollectionNotesByFolderAndUser(String folderId){

        System.out.println(folderId);

        System.out.println(getCurrentUser());
        Log.d(TAG,"getNotesByFolderAndUser");
        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .whereEqualTo("owner", getCurrentUser())
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot note : task.getResult()) {
                                System.out.println(note);
                                Log.d(TAG, note.getId() + " => " + note.getData());
                                notesInFolder.add(new ImageNote( note.getString("title"),
                                        note.getString("id"), note.getString("folderId"),
                                        note.getString("owner"), note.getDate("creation"),
                                        note.getDate("modify"), note.getString("imagepath")));
                                        notesInFolder.sort(new DateSorter());
                                        System.out.println(notesInFolder);
                                        listener.setCollection(notesInFolder);
                            }

                        } else {
                            Log.d(TAG, "Error getting notes: ", task.getException());
                        }
                    }
                });

        db.collection("notes")
                .document("roomAudioNotes")
                .collection("audioNotes")
                .whereEqualTo("owner", getCurrentUser())
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot note : task.getResult()) {
                                Log.d(TAG, note.getId() + " => " + note.getData());
                                /*notesInFolder.add(new AudioNote( note.getString("title"),
                                        note.getString("id"), note.getString("folderId"),
                                        note.getString("owner"), note.getDate("creation"),
                                        note.getDate("modify"), note.getString("imagepath")));

                                 */
                            }

                        } else {
                            Log.d(TAG, "Error getting notes: ", task.getException());
                        }
                    }
                });

        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .whereEqualTo("owner", getCurrentUser())
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot note : task.getResult()) {
                                Log.d(TAG, note.getId() + " => " + note.getData());
                                /*notesInFolder.add(new TextNote( note.getString("title"),
                                        note.getString("id"), note.getString("folderId"),
                                        note.getString("owner"), note.getDate("creation"),
                                        note.getDate("modify"), note.getString("imagepath")));
                                        notesInFolder.sort(new DateSorter()); // tendre que quitar el otro setCollection()
                                        listener.setCollection(notesInFolder);
                                 */
                            }

                        } else {
                            Log.d(TAG, "Error getting notes: ", task.getException());
                        }
                    }
                });
    }

    public void saveImageNote (String title, String id, String folderId, String owner,
                               Date creation, Date modify, String imagepath) {

        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("id", id);
        note.put("folderId", folderId);
        note.put("owner", owner);
        note.put("creation", creation);
        note.put("modify", modify);
        note.put("imagepath", imagepath);

        Log.d(TAG, "saveImageNote");
        // Add a new document with a generated ID
        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Image Note added with ID: " + documentReference.getId());
                        if (!imagepath.isEmpty()){
                            saveImagefromNote(imagepath);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Image Note", e);
                    }
                });
    }

    public void saveImagefromNote (String imagepath) {

        Uri file = Uri.fromFile(new File(imagepath));
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(""+File.separator+file.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Image uploaded correctly");
                } else {
                    Log.w(TAG, "Error uploading image");
                }
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });
    }

}
