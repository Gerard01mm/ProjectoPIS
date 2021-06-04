package com.example.my_notes.Model;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;


public class ImageNote extends Note {

    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public ImageNote(String folderId){
        super(folderId);
    }

    public ImageNote(String title, String folderId){
        super(title, folderId);
    }

    public ImageNote(String title, String id, String folderId, String owner, ArrayList<String> shared,
                     Date creation, Date modify){
        super(title, folderId, id, owner, shared, creation, modify);
    }
    public ImageNote(String title, String id, String folderId, String owner,
                     Date creation, Date modify){
        super(title, folderId, id, owner, creation, modify);
    }

    public void saveImageNote(){
        adapter.saveImageNote(getTitle(), getId() , getFolderId() , getOwner(),
                getCreation_date(), getModify_date());
    }
    public void deleteImageNote(){
        adapter.deleteImageNote(getId(), getFolderId());
    }

    /*Amb aquest mètode cridem a un del baseAdapter*/
    public void deleteSharedImageNote(){
        adapter.deleteSharedImageNote(getId(), getFolderId());
    }
    public void updateImageNote(){
        setModify_date();
        adapter.updateImageNote(getTitle(), getFolderId(), getId(), getOwner(), getCreation_date(), new Date());
    }
}