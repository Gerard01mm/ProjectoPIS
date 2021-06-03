package com.example.my_notes.Model;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Date;

public class TextNote extends Note {

    //Atributs de la classe
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public TextNote(String folderId){
        super(folderId);
    }

    public TextNote(String title, String folderId){
        super(title, folderId);
    }

    //Notas compartidas
    public TextNote(String title, String id, String folderId, String owner, ArrayList<String> shared,
                    Date creation, Date modify){
        super(title, folderId, id, owner, shared, creation, modify);
    }

    public TextNote(String title, String id, String folderId, String owner,
                     Date creation, Date modify){
        super(title, folderId, id, owner, creation, modify);
    }

    public void saveTextNote(){
        adapter.saveTextNote(getTitle(), getId() , getFolderId() , getOwner(),
                getCreation_date(), getModify_date());
    }

    public void updateTextNote(){
        setModify_date();
        adapter.updateTextNote(getTitle(), getFolderId(), getId(), getOwner(), getCreation_date(), new Date());
    }

    public void deleteTextNote(){
        adapter.deleteTextNote(getId(), getFolderId());
    }

    /*Amb aquest mètode cridem a un del baseAdapter*/
    public void deleteSharedTextNote(){
        adapter.deleteSharedTextNote(getId(), getFolderId());
    }

    /*Amb aquest mètode cridem a un del baseAdapter*/
    public void deleteSharedImageNote(){
        adapter.deleteSharedImageNote(getId(), getFolderId());
    }
}
