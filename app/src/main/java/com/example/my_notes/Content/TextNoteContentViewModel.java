package com.example.my_notes.Content;


import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;

import com.example.my_notes.Model.NotesContent;
import com.example.my_notes.Model.TextNoteContent;

public class TextNoteContentViewModel extends ViewModel implements DatabaseAdapter.vmInterface {

    private DatabaseAdapter adapter;
    private MutableLiveData<String> mToast;
    private String noteId, folderId;
    private Application application;
    private MutableLiveData<NotesContent> textNoteContent;

    public TextNoteContentViewModel(Application application, String noteId, String folderId){
        this.adapter= new DatabaseAdapter(this);
        this.mToast = new MutableLiveData<>();
        this.textNoteContent = new MutableLiveData<>();
        this.application = application;
        this.noteId = noteId;
        this.folderId = folderId;
        adapter.getTextNoteContent(noteId, folderId);
    }

    public void saveTextNoteContent(String path, String text, String idNote, String idFolder){
        TextNoteContent tx = new TextNoteContent(idNote, idFolder, text);
        textNoteContent.setValue(tx);
        tx.saveContent();
    }

    public MutableLiveData<String> getmToast() {
        return mToast;
    }

    public MutableLiveData<NotesContent> getTextNoteContent() {
        return textNoteContent;
    }

    @Override
    public void setCollection(ArrayList ac) {
        textNoteContent.setValue((TextNoteContent) ac.get(0));
    }

    @Override
    public void setToast(String s) {
        this.mToast.setValue(s);
    }
}
