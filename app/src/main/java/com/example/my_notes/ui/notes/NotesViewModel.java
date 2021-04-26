package com.example.my_notes.ui.notes;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;

import Notes.ImageNote;
import Notes.Note;

import static com.example.my_notes.ui.notes.NotesViewModelFactory.created;

public class NotesViewModel extends ViewModel implements DatabaseAdapter.vmInterface{

    private MutableLiveData<String> mToast;
    private MutableLiveData<ArrayList<Note>> mNotes;
    private DatabaseAdapter da;

    private Application application;
    private String idFolder;

    public NotesViewModel(Application app, String idFolder) {
        mToast = new MutableLiveData<>();
        mNotes = new MutableLiveData<>();
        da = new DatabaseAdapter(this);
        da.getCollectionNotesByFolderAndUser(idFolder);
        this.application = app;
        this.idFolder = idFolder;
    }

    public void addImageNote(String title){
        ImageNote n;
        if (title.isEmpty()){
            n = new ImageNote(idFolder);
        }else{
            n = new ImageNote(title, idFolder);
        }
        if (mNotes.getValue() == null){
            ArrayList<Note> anf = new ArrayList<>();
            anf.add(n);
            mNotes.setValue(anf);
        }else{
            mNotes.getValue().add(n);
            // Inform observer
            mNotes.setValue(mNotes.getValue());
        }
        n.saveImageNote();
    }

    public LiveData<ArrayList<Note>> getNotes() {
        return mNotes;
    }

    public LiveData<String> getToast() {
        return mToast;
    }

    @Override
    public void setCollection(ArrayList ac) {
        mNotes.setValue(ac);
    }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }
}