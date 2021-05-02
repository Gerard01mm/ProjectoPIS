package com.example.my_notes.Model;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

public class ImageNoteContent extends NotesContent{
    private String imagepath;
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public ImageNoteContent(String noteId, String folderId, String text, String imagepath) {
        super(noteId, folderId, text);
        this.imagepath = imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void saveContent(){
        Log.d("saveImageNote", "adapter-> saveImageNote");
        adapter.saveImageNoteContent(getNoteId(), getFolderId() , getTextNote() , imagepath);
    }
}
