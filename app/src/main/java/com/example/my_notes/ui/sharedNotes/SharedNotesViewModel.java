package com.example.my_notes.ui.sharedNotes;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;
import com.example.my_notes.Model.Note;
import com.example.my_notes.ui.notes.NotesViewModel;

import java.util.ArrayList;

public class SharedNotesViewModel extends ViewModel implements DatabaseAdapter.vmInterface {

    private MutableLiveData<String> mToast;
    private MutableLiveData<ArrayList<Note>> mNotes;
    private DatabaseAdapter da;

    private Application application;
    private String idFolder;

    public SharedNotesViewModel(Application app, String idFolder) {
        mToast = new MutableLiveData<>();
        mNotes = new MutableLiveData<>();
        da = new DatabaseAdapter(this);
        da.getCollectionNotesBySharedToUser();
        this.application = app;
        this.idFolder = idFolder;
    }

    @Override
    public void setCollection(ArrayList ac) {
        //Log.d("SharedNotesViewModel", "setCollection");
        mNotes.setValue(ac);
    }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }

    public LiveData<ArrayList<Note>> getNotes() {
        return mNotes;
    }

    public LiveData<String> getToast() {
        return mToast;
    }
}

