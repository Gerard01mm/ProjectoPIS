package com.example.my_notes.Content;

public class NotesContent {
    private String textNote, noteId, folderId;
    public NotesContent(String noteId, String folderId, String text){
        this.noteId = noteId;
        this.folderId = folderId;
        this.textNote  = text;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
}
