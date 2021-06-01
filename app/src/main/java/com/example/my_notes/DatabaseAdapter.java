package com.example.my_notes;

import android.app.AlertDialog;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.core.UserWriteRecord;
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
    public static DatabaseReference usersReference;

    private ArrayList<Note> notesInFolder;
    private ArrayList<Note> sharedNotes;

    public DatabaseAdapter(vmInterface listener){
        DatabaseAdapter.listener = listener;
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
        initFirebase();
        notesInFolder = new ArrayList<>();
        sharedNotes = new ArrayList<>();
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
                            //Log.d(TAG, "signInAnonymously:success");
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

    /* Mètode que serveix per afegir un nou usuari en l'atribut "shared" de la nota que es un array, per
     * compartir la nota amb més d'un usuari.*/
    public void updateSharedUsers (String noteId, String noteFolderId, String idUser){
        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .whereEqualTo("id", noteId)
                .whereEqualTo("folderId", noteFolderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                ArrayList<String> users = new ArrayList<String>();
                                Map<String, Object> textNote = new HashMap<>();
                                textNote.put("title", note.getString("title"));
                                textNote.put("id", note.getString("id"));
                                textNote.put("folderId", note.getString("folderId"));
                                textNote.put("owner", note.getString("owner"));
                                textNote.put("creation", (Timestamp) note.get("creation"));
                                textNote.put("modify", (Timestamp) note.get("modify"));

                                users = (ArrayList<String>) note.get("shared");
                                if (users != null) {
                                    boolean exist = false;
                                    for (String use : users) {
                                        if(use.equals(idUser)){
                                            exist = true;
                                        }
                                    }
                                    if(!exist) {
                                        users.add(idUser);
                                        textNote.put("shared", users);
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
                                }
                            }

                        } else {
                            Log.d(TAG, "Error: No note found ", task.getException());
                        }
                    }
                });
    }
    /* Mètode que serveix per afegir un nou usuari en l'atribut "shared" de la nota que es un array, per
     * compartir la nota amb més d'un usuari.*/
    public void updateSharedImageNote (String noteId, String noteFolderId, String idUser) {

        //Log.d(TAG, "updateImageNote");
        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .whereEqualTo("id", noteId)
                .whereEqualTo("folderId", noteFolderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                ArrayList<String> users = new ArrayList<String>();
                                Map<String, Object> imageNote = new HashMap<>();
                                imageNote.put("title", note.getString("title"));
                                imageNote.put("id", note.getString("id"));
                                imageNote.put("folderId", note.getString("folderId"));
                                imageNote.put("owner", note.getString("owner"));
                                imageNote.put("creation", (Timestamp) note.get("creation"));
                                imageNote.put("modify", (Timestamp) note.get("modify"));

                                users = (ArrayList<String>) note.get("shared");
                                if (users != null) {
                                    boolean exist = false;
                                    for (String use : users) {
                                        if (use.equals(idUser)) {
                                            exist = true;
                                        }
                                    }
                                    if (!exist) {
                                        users.add(idUser);
                                        imageNote.put("shared", users);
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
                        }
                    }
                });
    }

    /* Mètode que comproba si el correu existeix, per motius de incompatibilitat gestiono els errors que
     * es mostraran a l'editText desde aqui, en el cas que no existeixi l'usuari i si introduixen el
     * seu propi correu.*/
    public void checkEmail(String idUser, String noteId, String noteFolderId, String textWriten, String title, TextInputEditText userEmail, AlertDialog content){

        mAuth.fetchSignInMethodsForEmail(idUser)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        //
                        if(!check){
                            userEmail.setError("Don't exist user with this email");
                        }else{
                            //Si el correu introduit és el del mateix usuari.
                            if(getCurrentUser().equals(idUser)){
                                userEmail.setError("This is your own email!!");
                            }else{
                                updateSharedUsers(noteId, noteFolderId, idUser);
                                content.dismiss();
                            }
                        }
                    }
                });
    }
    /* Mètode que comproba si el correu existeix, per motius de incompatibilitat gestiono els errors que
     * es mostraran a l'editText desde aqui, en el cas que no existeixi l'usuari i si introduixen el
     * seu propi correu.*/
    public void checkEmailImatge(String idUser, String noteId, String noteFolderId, String textWriten, String title, TextInputEditText userEmail, AlertDialog content){

        mAuth.fetchSignInMethodsForEmail(idUser)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();
                        //
                        if(!check){
                            userEmail.setError("Don't exist user with this email");
                        }else{
                            //Si el correu introduit és el del mateix usuari.
                            if(getCurrentUser().equals(idUser)){
                                userEmail.setError("This is your own email!!");
                            }else{
                                updateSharedImageNote(noteId, noteFolderId, idUser);
                                content.dismiss();
                            }
                        }
                    }
                });
    }

    public void getCollectionFolders(){
        //Log.d(TAG,"getFolders");
        db.collection("folders")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<NoteFolder> retrieved_ac = new ArrayList<NoteFolder>() ;
                        for (QueryDocumentSnapshot folder : task.getResult()) {
                            //Log.d(TAG, folder.getId() + " => " + folder.getData());
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
        //Log.d(TAG,"getFoldersByUser");
        db.collection("folders")
                .whereEqualTo("owner", getCurrentUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<NoteFolder> retrieved_ac = new ArrayList<NoteFolder>() ;
                            for (QueryDocumentSnapshot folder : task.getResult()) {
                                //Log.d(TAG, folder.getId() + " => " + folder.getData());
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

        //Log.d(TAG, "saveFolder");
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
        //Log.d(TAG, "updateFolder");
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

    public void saveReminder(String title, String id, String owner, String date, String description, String alarm,
                             Double longitude, Double latitude, String country, String locality, String countrycode){
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);
        reminder.put("id", id);
        reminder.put("owner", owner);
        reminder.put("description", description);
        reminder.put("alarm", alarm);
        reminder.put("longitude", longitude);
        reminder.put("latitude", latitude);
        reminder.put("country", country);
        reminder.put("locality", locality);
        reminder.put("countrycode", countrycode);

        //Log.d(TAG, "saveReminder");

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
                    Log.w(TAG, "Error adding reminder", e);
                }
            });
    }


    public void updateReminder(String title, String id, String owner, String date, String description, String alarm,
                               Double longitude, Double latitude, String country, String locality, String countrycode){
        final DocumentReference [] docRef = new DocumentReference[1];

        Map<String, Object> reminder = new HashMap<>();
        reminder.put("title", title);
        reminder.put("date", date);
        reminder.put("id", id);
        reminder.put("owner", owner);
        reminder.put("description", description);
        reminder.put("alarm", alarm);
        reminder.put("longitude", longitude);
        reminder.put("latitude", latitude);
        reminder.put("country", country);
        reminder.put("locality", locality);
        reminder.put("countrycode", countrycode);

        //Log.d(TAG, "updateReminder");

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
                                                Log.d(TAG, "Error updating reminder");
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

        //Log.d(TAG, "saveAudioNote");
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

        //Log.d(TAG, "updateAudioNote");
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
        //Log.d(TAG, "getRemindersByUserAndDay");
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
                                //Log.d(TAG, reminder.getId() + " => " + reminder.getData());
                                retrieved_ac.add(new Reminder(reminder.getString("title"),
                                        reminder.getString("description"),
                                        reminder.getString("alert"),
                                        reminder.getString("date"),
                                        reminder.getString("id"),
                                        reminder.getString("owner"),
                                        reminder.getDouble("longitude"),
                                        reminder.getDouble("latitude"),
                                        reminder.getString("country"),
                                        reminder.getString("locality"),
                                        reminder.getString("countrycode")));
                            }

                            System.out.println("Numero de notes del dia: " + date + retrieved_ac.size());
                            listener.setCollection(retrieved_ac);
                        }
                        else{
                            Log.d(TAG,"Error getting reminders: ", task.getException());
                        }
                    }
                });
    }


    /**
     * Aquesta funció retorna el número total de recordatoris que té un usuari. Servirà per
     * recuperar el id correcte i evitar que es solapin les notificacions
     * @return Numero de recordatoris que tingui un usuari
     */
    public int getNumberOfReminders(){
        int[] counter = {0};
        db.collection("reminders")
                .whereEqualTo("owner", getCurrentUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            //ArrayList<Reminder> retrieved_ac = new ArrayList<>();
                            for (QueryDocumentSnapshot reminder : task.getResult()){
                                counter[0]++;
                                //Log.d(TAG, reminder.getId() + " => " + reminder.getData());
                                /*
                                retrieved_ac.add(new Reminder(reminder.getString("title"),
                                        reminder.getString("description"),
                                        reminder.getString("alert"),
                                        reminder.getString("date"),
                                        reminder.getString("id"),
                                        reminder.getString("owner"),
                                        reminder.getDouble("longitude"),
                                        reminder.getDouble("latitude"),
                                        reminder.getString("country"),
                                        reminder.getString("locality"),
                                        reminder.getString("countrycode")));*/
                            }
                        }
                        else{
                            Log.d(TAG,"Error getting reminders: ", task.getException());
                        }
                    }
                });

        return counter[0];
    }

    /* Mètode que agafa les notes que siguin compartides amb el currentUser mirant el camp
     * "shared" que és un array, per veure si conté el correu electronic del usuuari actual. */
    public void getCollectionNotesBySharedToUser(){
        //Log.d(TAG,"getCollectionNotesBySharedToUser");
        db.collection("notes")
                .document("roomTextNotes")
                .collection("textNotes")
                .whereArrayContains("shared", getCurrentUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> userShared = new ArrayList<String>();
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                userShared = (ArrayList<String>) note.get("shared");
                                sharedNotes.add(new TextNote( note.getString("title"),
                                        note.getString("id"), note.getString("folderId"),
                                        note.getString("owner"), userShared, note.getDate("creation"),
                                        note.getDate("modify")));
                                Log.d("AAADAPTEEER TEXTOO: ", "Supuestamente cogo una de texto");
                            }
                        } else {
                            Log.d(TAG, "Error getting notes: ", task.getException());
                        }
                    }
                });

        db.collection("notes")
                .document("roomImageNotes")
                .collection("imageNotes")
                .whereArrayContains("shared", getCurrentUser())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot note : task.getResult()) {
                                ArrayList<String> userShared = new ArrayList<String>();
                                userShared = (ArrayList<String>) note.get("shared");
                                sharedNotes.add(new ImageNote( note.getString("title"),
                                        note.getString("id"), note.getString("folderId"),
                                        note.getString("owner"), userShared, note.getDate("creation"),
                                        note.getDate("modify")));
                                Log.d("AAADAPTEEER IMAGEEN: ", "Supuestamente cogo la imagen");
                            }
                            sharedNotes.sort(new DateSorter());
                            listener.setCollection(sharedNotes);
                        } else {
                            Log.d(TAG, "Error getting notes: ", task.getException());
                        }
                    }
                });

    }

    public void getCollectionNotesByFolderAndUser(String folderId){
        //Log.d(TAG,"getNotesByFolderAndUser");
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
                                //Log.d(TAG, note.getId() + " => " + note.getData());
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
                                //Log.d(TAG, note.getId() + " => " + note.getData());
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
                                //Log.d(TAG, note.getId() + " => " + note.getData());
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

        //Log.d(TAG, "updateTextNote");
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

        //Log.d(TAG, "updateImageNote");
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
        ArrayList<String> user = new ArrayList<String>();
        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("id", id);
        note.put("folderId", folderId);
        note.put("owner", owner);
        note.put("creation", creation);
        note.put("modify", modify);
        note.put("shared", user);

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

        ArrayList<String> user = new ArrayList<String>();
        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("id", id);
        note.put("folderId", folderId);
        note.put("owner", owner);
        note.put("creation", creation);
        note.put("modify", modify);
        note.put("shared", user);

        //Log.d(TAG, "saveTextNote");
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
        //System.out.println("Directori de la imatge:" + imagepath);
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
                        if (!task.getResult().isEmpty()){
                            for (QueryDocumentSnapshot noteC : task.getResult()) {
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
                Log.d(TAG, exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload Successful");
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
                //.whereEqualTo("owner", getCurrentUser())
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

    /* Aquest mètode elimina per l'usuari actual, la nota compartida per un altre, de manera que l'atribut
     * de la col·lecció  "shared" que és un array, elimina a l'usuari actual de la llista de usuaris compartits.
     */
    public void deleteSharedTextNote(String id, String folderId){
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
                                ArrayList<String> users = new ArrayList<String>();
                                Map<String, Object> textNote = new HashMap<>();
                                textNote.put("title", note.getString("title"));
                                textNote.put("id", note.getString("id"));
                                textNote.put("folderId", note.getString("folderId"));
                                textNote.put("owner", note.getString("owner"));
                                textNote.put("creation", (Timestamp) note.get("creation"));
                                textNote.put("modify", (Timestamp) note.get("modify"));

                                users = (ArrayList<String>) note.get("shared");
                                if (users != null) {
                                    boolean exist = false;
                                    for (int i = 0; i < users.size(); i++) {
                                        if(users.get(i).equals(getCurrentUser())){
                                            users.remove(i);
                                            i = users.size();
                                        }
                                    }
                                    textNote.put("shared", users);
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
                            }
                        } else {
                            Log.d(TAG, "Error deleting folder: ", task.getException());
                        }
                    }
                });
    }

    /* Aquest mètode elimina per l'usuari actual, la nota compartida per un altre, de manera que l'atribut
     * de la col·lecció  "shared" que és un array, elimina a l'usuari actual de la llista de usuaris compartits.*/
    public void deleteSharedImageNote(String id, String folderId){
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
                                ArrayList<String> users = new ArrayList<String>();
                                Map<String, Object> textNote = new HashMap<>();
                                textNote.put("title", note.getString("title"));
                                textNote.put("id", note.getString("id"));
                                textNote.put("folderId", note.getString("folderId"));
                                textNote.put("owner", note.getString("owner"));
                                textNote.put("creation", (Timestamp) note.get("creation"));
                                textNote.put("modify", (Timestamp) note.get("modify"));

                                users = (ArrayList<String>) note.get("shared");
                                if (users != null) {
                                    boolean exist = false;
                                    for (int i = 0; i < users.size(); i++) {
                                        if(users.get(i).equals(getCurrentUser())){
                                            users.remove(i);
                                            i = users.size();
                                        }
                                    }
                                    textNote.put("shared", users);
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
                //.whereEqualTo("owner", getCurrentUser())
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
