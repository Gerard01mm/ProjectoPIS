package com.example.my_notes.Model;

import java.util.Date;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

public class AudioNote extends Note {
    private String adress, audioName;
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public AudioNote(String title, String localPath, String folderId, String audioName){
        super(title, folderId);
        this.adress = localPath;
        this.audioName = audioName;
    }
    public AudioNote(String title,String id, String localPath, String folderId, String owner, Date creation, Date modify, String audioName){
        super(title, folderId, id, owner, creation, modify);
        this.adress = localPath;
        this.audioName = audioName;
    }

    public String getAdress(){
        return this.adress;
    }

    public void saveAudioNote(){
        adapter.saveAudioNoteWithFile(getId(), getTitle(), getOwner(), getFolderId(), getCreation_date(), getModify_date(), this.adress, this.audioName);
    }

    public void updateAudioNote(){
        setModify_date();
        adapter.updateAudioNote(getId(), getTitle(), getOwner(), getFolderId(), getCreation_date(), getModify_date(), this.adress, this.audioName);
    }

    public void removeAudioNote(){
        adapter.removeAudioNote(getId());
    }
}
