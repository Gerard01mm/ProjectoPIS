package com.example.my_notes.ui.foldershome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import Notes.NoteFolder;

public class FolderViewModel extends ViewModel {

    private MutableLiveData<NoteFolder> mFolders;
    private MutableLiveData<String> mToast;

    public FolderViewModel() {
        mFolders = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        //FolderDatabaseAdapter da= new FolderDatabaseAdapter(this);
        //da.getCollection();
    }
    /*
    public LiveData<String> getText() {
        return mText;
    }
    */
    public void addFolder(String titol){
        if (titol.isEmpty()){
            NoteFolder nf = new NoteFolder();
        }else{
            NoteFolder nf = new NoteFolder(titol);
        }
        //mAudioCards.getValue().add(ac);
        // Inform observer.
        //mAudioCards.setValue(mAudioCards.getValue());
        //ac.saveCard();
    }
}