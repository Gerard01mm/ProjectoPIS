package com.example.my_notes.Content;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;

import Notes.ImageNote;
import Notes.Note;

public class ImageNoteContentViewModel extends ViewModel implements DatabaseAdapter.vmInterface {

    private DatabaseAdapter adapter;
    private MutableLiveData<String> mToast;
    private MutableLiveData<ImageNoteContent> iC;
    private String noteId, folderId;
    private Application application;

    public ImageNoteContentViewModel(Application application, String noteId, String folderId){
        this.adapter= DatabaseAdapter.databaseAdapter;
        this.mToast = new MutableLiveData<>();
        this.iC = new MutableLiveData<>();
        this.application = application;
        this.noteId = noteId;
        this.folderId = folderId;
        adapter.getImageNoteContent(noteId, folderId);
    }

    public void saveImageNoteContent(String path, String text, String idNote, String idFolder){
        ImageNoteContent imageNoteContent = new ImageNoteContent(idNote, idFolder, text, path);
        System.out.println(imageNoteContent);
        iC.setValue(imageNoteContent);
        imageNoteContent.saveContent();
    }

    public void setImageNoteId(String id){
        this.noteId = id;
    }

    public void setImageNoteFolderId(String folderId){
        this.folderId = folderId;
    }

    @Override
    public void setCollection(ArrayList ac) {

    }

    @Override
    public void setToast(String s) {

    }
}
