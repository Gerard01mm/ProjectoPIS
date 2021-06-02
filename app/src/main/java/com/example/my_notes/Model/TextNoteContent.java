package com.example.my_notes.Model;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

public class TextNoteContent extends NotesContent{
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public TextNoteContent(String noteId, String folderId, String text) {
        super(noteId, folderId, text);
    }

    public void saveContent(){
        //Log.d("saveTextNoteContent", "adapter-> saveImageNote");
        adapter.saveTextNoteContent(getNoteId(), getFolderId() , getTextNote());
    }
}
