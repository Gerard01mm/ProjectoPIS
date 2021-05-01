package com.example.my_notes.ui.notes;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;

import Notes.AudioNote;
import Notes.ImageNote;
import Notes.Note;
import Notes.TextNote;

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

    public void addTextNote(String title){
        TextNote n;
        if (title.isEmpty()){
            n = new TextNote(idFolder);
        }else{
            n = new TextNote(title, idFolder);
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
        n.saveTextNote();
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

    public void addAudioCard(String title, String adress, String folderId, String audioName) {
        AudioNote an = new AudioNote(title, adress, folderId, audioName);
        if (mNotes.getValue() == null){
            ArrayList<Note> anf = new ArrayList<>();
            anf.add(an);
            mNotes.setValue(anf);
        }else{
            mNotes.getValue().add(an);
            // Inform observer
            mNotes.setValue(mNotes.getValue());
        }
        an.saveAudioNote();
    }

    public void removeAudioCard(int idx){
        AudioNote an = (AudioNote)mNotes.getValue().remove(idx);
        mNotes.setValue(mNotes.getValue());
        an.removeAudioNote();
    }

    public void updateAudioCard(int idx, String new_title){
        AudioNote an = (AudioNote)mNotes.getValue().get(idx);
        an.setTitle(new_title);
        mNotes.setValue(mNotes.getValue());
        an.updateAudioNote();
    }
    public Note getNote(int idx){
        return mNotes.getValue().get(idx);
    }
}