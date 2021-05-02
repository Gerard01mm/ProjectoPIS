package com.example.my_notes;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.my_notes.Utils.ArrayListComparator.DateSorter;
import com.example.my_notes.Model.ImageNoteContent;
import com.example.my_notes.Model.NotesContent;
import com.example.my_notes.Model.TextNoteContent;
import com.example.my_notes.Model.Reminder;
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
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import com.example.my_notes.Model.AudioNote;
import com.example.my_notes.Model.ImageNote;
import com.example.my_notes.Model.Note;
import com.example.my_notes.Model.NoteFolder;
import com.example.my_notes.Model.TextNote;

public class DatabaseAdapter{
    public static final String TAG = "DatabaseAdapter";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;


    public static vmInterface listener;
    public static DatabaseAdapter databaseAdapter;

    private ArrayList<Note> notesInFolder;

    public DatabaseAdapter(vmInterface listener){
        DatabaseAdapter.listener = listener;
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
            Log.d(TAG, "Authentication with current user.");

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
                                deleteNotesFromFolder(id);
                            }

                        } else {
                            Log.d(TAG, "Error deleting folder: ", task.getException());
                        }
                    }
                });
    }

    public void saveReminder(String title, String id, String owner, String date, String description, String alarm){
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);
        reminder.put("id", id);
        reminder.put("owner", owner);
        reminder.put("description", description);
        reminder.put("alarm", alarm);

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


    public void updateReminder(String title, String id, String owner, String date, String description, String alarm){
        final DocumentReference [] docRef = new DocumentReference[1];

        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);
        reminder.put("id", id);
        reminder.put("owner", owner);
        reminder.put("description", description);
        reminder.put("alarm", alarm);

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

    public void saveAudioNoteWithFile(String id, String title, String owner, String folderId, Date creation_date, Date modify_date, String path, String audioName) {
        Uri file = Uri.fromFile(new File(path));
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child("audio"+File.separator+audioName);
        UploadTask uploadTask = audioRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return audioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveAudio(id, title, owner, folderId, creation_date, modify_date, downloadUri.toString(), audioName);
                } else {
                    // Handle failures
                    // ...
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

    public void saveAudio(String id, String title, String owner, String folderId, Date creation_date, Date modify_date, String url, String audioName){
        // Create a new user with a first and last name
        Map<String, Object> audioNote = new HashMap<>();
        audioNote.put("id", id);
        audioNote.put("title", title);
        audioNote.put("owner", owner);
        audioNote.put("folderId", folderId);
        audioNote.put("creation_date", creation_date);
        audioNote.put("modify_date", modify_date);
        audioNote.put("url", url);
        audioNote.put("audioName", audioName);

        Log.d(TAG, "saveAudioNote");
        // Add a new document with a generated ID
        db.collection("notes").document("roomAudioNotes").collection("audioNotes")
                .add(audioNote)
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
                        Log.w(TAG, "Error adding audioNote", e);
                    }
                });
    }

    public void updateAudioNote(String id, String title, String owner, String folderId, Date creation_date, Date modify_date, String url, String audioName){
        final DocumentReference [] docRef = new DocumentReference[1];

        Map<String, Object> newAudioNote = new HashMap<>();
        newAudioNote.put("id", id);
        newAudioNote.put("title", title);
        newAudioNote.put("owner", owner);
        newAudioNote.put("folderId", folderId);
        newAudioNote.put("creation_date", creation_date);
        newAudioNote.put("modify_date", modify_date);
        newAudioNote.put("url", url);
        newAudioNote.put("audioName", audioName);

        Log.d(TAG, "updateAudioNote");
        db.collection("notes").document("roomAudioNotes").collection("audioNotes")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot audioNote : task.getResult()) {
                                docRef[0] = audioNote.getReference();
                                docRef[0].update(newAudioNote)
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
                            Log.d(TAG, "Error: No audioNote found ", task.getException());
                        }
                    }
                });
    }

    public void removeAudioNote(String id) {
        db.collection("notes").document("roomAudioNotes").collection("audioNotes")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot audioNote : task.getResult()) {
                                audioNote.getReference().delete();
                                deleteAudioFromStorage(audioNote.getString("audioName"));
                            }

                        } else {
                            Log.d(TAG, "Error deleting audioNote: ", task.getException());
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
                                        note.getDate("modify")));
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
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot note : task.getResult()) {
                                Log.d(TAG, note.getId() + " => " + note.getData());
                                notesInFolder.add(new AudioNote( note.getString("title"),
                                        note.getString("id"), note.getString("url"), note.getString("folderId"),
                                        note.getString("owner"), note.getDate("creation_date"),
                                        note.getDate("modify_date"), note.getString("audioName")));
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
                                notesInFolder.add(new TextNote( note.getString("title"),
                                        note.getString("id"), note.getString("folderId"),
                                        note.getString("owner"), note.getDate("creation"),
                                        note.getDate("modify")));
                            }
                            notesInFolder.sort(new DateSorter());
                            listener.setCollection(notesInFolder);
                        } else {
                            Log.d(TAG, "Error getting notes: ", task.getException());
                        }
                    }
                });
    }

    public void updateTextNote (String title, String folderId, String id, String owner, Date creation, Date modify) {
        // Create a new hashmap
        Map<String, Object> textNote = new HashMap<>();
        textNote.put("title", title);
        textNote.put("id", id);
        textNote.put("folderId", folderId);
        textNote.put("owner", owner);
        textNote.put("creation", creation);
        textNote.put("modify", modify);

        Log.d(TAG, "updateTextNote");
        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                note.getReference().update(textNote)
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
                            Log.d(TAG, "Error: No note found ", task.getException());
                        }
                    }
                });

    }

    public void updateImageNote (String title, String folderId, String id, String owner, Date creation, Date modify) {
        // Create a new hashmap
        Map<String, Object> imageNote = new HashMap<>();
        imageNote.put("title", title);
        imageNote.put("id", id);
        imageNote.put("folderId", folderId);
        imageNote.put("owner", owner);
        imageNote.put("creation", creation);
        imageNote.put("modify", modify);

        Log.d(TAG, "updateImageNote");
        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                note.getReference().update(imageNote)
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
                            Log.d(TAG, "Error: No note found ", task.getException());
                        }
                    }
                });

    }

    public void saveImageNote (String title, String id, String folderId, String owner,
                               Date creation, Date modify) {

        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("id", id);
        note.put("folderId", folderId);
        note.put("owner", owner);
        note.put("creation", creation);
        note.put("modify", modify);

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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Image Note", e);
                    }
                });
    }

    public void saveTextNote (String title, String id, String folderId, String owner,
                               Date creation, Date modify) {

        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("id", id);
        note.put("folderId", folderId);
        note.put("owner", owner);
        note.put("creation", creation);
        note.put("modify", modify);

        Log.d(TAG, "saveTextNote");
        // Add a new document with a generated ID
        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Image Note added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Image Note", e);
                    }
                });
    }


    public void saveImageNoteContent (String id, String folderId, String text, String imagepath) {
        // Create a new user with a first and last name
        System.out.println("Directori de la imatge:" + imagepath);
        Map<String, Object> noteContent = new HashMap<>();
        noteContent.put("id", id);
        noteContent.put("folderId", folderId);
        noteContent.put("text", text);
        noteContent.put("imagepath", imagepath);
        noteContent.put("owner", getCurrentUser());
        Log.d(TAG, "saveImageNoteContent");
        // Add a new document with a generated ID
        db.collection("content")
                .document("roomImageNoteContent")
                .collection("ImageNoteContent")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println("onccomplete");
                        if (!task.getResult().isEmpty()){
                            System.out.println("success");
                            for (QueryDocumentSnapshot noteC : task.getResult()) {
                                System.out.println(noteC);
                                Log.d(TAG, noteC.getId() + " => " + noteC.getData());
                                deleteImageFromStorage(noteC.getString("imagepath"));
                                noteC.getReference().update(noteContent);
                                if(imagepath != null){
                                    saveImageInNote(imagepath);
                                }
                            }

                        } else {
                            db.collection("content").document("roomImageNoteContent")
                                    .collection("ImageNoteContent").add(noteContent);
                            if(imagepath != null){
                                saveImageInNote(imagepath);
                            }
                        }
                    }
                });
    }

    public void saveImageInNote (String imagepath) {
        File fitxer = new File(imagepath);
        Uri file = Uri.fromFile(fitxer);
        if (!fitxer.exists()) {
            System.out.println("Existe");
        }
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images"+File.separator+imagepath);

        UploadTask uploadTask = imageRef.putFile(file);


        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println(exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("si");
            }
        });

    }

    public void deleteAudioFromStorage(String audioName){
        StorageReference storageReference = storage.getReference().child("audio"+File.separator+audioName);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Audio deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error deleting: " + e.toString());
            }
        });
    }

    public void deleteImageFromStorage(String imagepath){
        StorageReference storageReference = storage.getReference().child("images"+File.separator+imagepath);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Image deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error deleting: " + e.toString());
            }
        });
    }

    public void getImageNoteContent(String id, String folderId){
        Log.d(TAG,"getImageNoteContent");
        db.collection("content")
                .document("roomImageNoteContent")
                .collection("ImageNoteContent")
                .whereEqualTo("owner", getCurrentUser())
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            ArrayList<NotesContent> retrieved_ac = new ArrayList<NotesContent>() ;
                            for (QueryDocumentSnapshot content : task.getResult()) {
                                Log.d(TAG, content.getId() + " => " + content.getData());
                                retrieved_ac.add(new ImageNoteContent(content.getString("id"),
                                        content.getString("folderId"), content.getString("text"),
                                        content.getString("imagepath")));
                            }
                            listener.setCollection(retrieved_ac);

                        } else {
                            Log.d(TAG, "Error getting image note: ", task.getException());
                        }
                    }
                });
    }

    //TODO
    public void deleteNotesFromFolder(String folderId){
        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                System.out.println("nota image"+note);
                                deleteImageNoteContent(note.getString("id"), note.getString("folderId"));
                                note.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "Error deleting Image note: ", task.getException());
                        }
                    }
                });
        db.collection("notes")
                .document("roomAudioNotes")
                .collection("audioNotes")
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                deleteAudioFromStorage(note.getString("audioName"));
                                note.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "Error deleting Audio note: ", task.getException());
                        }
                    }
                });
        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                deleteTextNoteContent(note.getString("id"), note.getString("folderId"));
                                note.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "Error deleting folder: ", task.getException());
                        }
                    }
                });

    }

    public void deleteTextNote(String id, String folderId){
        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                deleteTextNoteContent(note.getString("id"), note.getString("folderId"));
                                note.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "Error deleting folder: ", task.getException());
                        }
                    }
                });
    }

    public void deleteTextNoteContent (String id, String folderId) {
        Log.d(TAG, "deleteImageNoteContent");
        // Add a new document with a generated ID
        db.collection("content")
                .document("roomTextNoteContent")
                .collection("TextNoteContent")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot noteC : task.getResult()) {
                                System.out.println(noteC);
                                noteC.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "No content found");
                        }
                    }
                });
    }

    public void deleteImageNote(String id, String folderId){
        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                deleteImageNoteContent(note.getString("id"), note.getString("folderId"));
                                note.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "Error deleting folder: ", task.getException());
                        }
                    }
                });
    }

    public void deleteImageNoteContent (String id, String folderId) {
        Log.d(TAG, "saveImageNoteContent");
        // Add a new document with a generated ID
        db.collection("content")
                .document("roomImageNoteContent")
                .collection("ImageNoteContent")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot noteC : task.getResult()) {
                                if(noteC.getString("imagepath") != null){
                                    System.out.println("Entra en el if");
                                    deleteImageFromStorage(noteC.getString("imagepath"));
                                }
                                noteC.getReference().delete();
                            }

                        } else {
                            Log.d(TAG, "No content found");
                        }
                    }
                });
    }

    public void saveTextNoteContent (String id, String folderId, String text) {
        // Create a new user with a first and last name
        Map<String, Object> noteContent = new HashMap<>();
        noteContent.put("id", id);
        noteContent.put("folderId", folderId);
        noteContent.put("text", text);
        noteContent.put("owner", getCurrentUser());
        Log.d(TAG, "saveTextNoteContent");
        // Add a new document with a generated ID
        db.collection("content")
                .document("roomTextNoteContent")
                .collection("TextNoteContent")
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()){
                            for (QueryDocumentSnapshot noteC : task.getResult()) {
                                Log.d(TAG, noteC.getId() + " => " + noteC.getData());
                                noteC.getReference().update(noteContent);
                            }

                        } else {
                            db.collection("content").document("roomTextNoteContent")
                                    .collection("TextNoteContent").add(noteContent);
                        }
                    }
                });
    }

    public void getTextNoteContent(String id, String folderId){
        Log.d(TAG,"getTextNoteContent");
        db.collection("content")
                .document("roomTextNoteContent")
                .collection("TextNoteContent")
                .whereEqualTo("owner", getCurrentUser())
                .whereEqualTo("id", id)
                .whereEqualTo("folderId", folderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            ArrayList<NotesContent> retrieved_ac = new ArrayList<NotesContent>() ;
                            for (QueryDocumentSnapshot content : task.getResult()) {
                                Log.d(TAG, content.getId() + " => " + content.getData());
                                retrieved_ac.add(new TextNoteContent(content.getString("id"),
                                        content.getString("folderId"), content.getString("text")));
                            }
                            listener.setCollection(retrieved_ac);

                        } else {
                            Log.d(TAG, "Error getting text note: ", task.getException());
                        }
                    }
                });
    }
}
