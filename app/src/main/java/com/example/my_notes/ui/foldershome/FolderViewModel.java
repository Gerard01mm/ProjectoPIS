package com.example.my_notes.ui.foldershome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;
import com.example.my_notes.MainActivity;

import java.util.ArrayList;

import Notes.NoteFolder;

public class FolderViewModel extends ViewModel implements DatabaseAdapter.vmInterface{

    private MutableLiveData<ArrayList<NoteFolder>> mFolders;
    private MutableLiveData<String> mToast;

    public FolderViewModel() {
        mFolders = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        DatabaseAdapter da= new DatabaseAdapter(this);
        da.getCollectionFolders();
    }

    public LiveData<ArrayList<NoteFolder>> getFolders() {
        return mFolders;
    }

    public LiveData<String> getToast(){
        return mToast;
    }

    public NoteFolder getNoteFolder(int idx){
        return mFolders.getValue().get(idx);
    }

    public void addFolder(String titol){
        NoteFolder nf;
        if (titol.isEmpty()){
            nf = new NoteFolder();
        }else{
            nf = new NoteFolder(titol);
        }
        if (mFolders.getValue() == null){
            ArrayList<NoteFolder> anf = new ArrayList<>();
            anf.add(nf);
            mFolders.setValue(anf);
        }else{
            mFolders.getValue().add(nf);
            // Inform observer
            mFolders.setValue(mFolders.getValue());
        }
        nf.saveFolder();
    }

    @Override
    public void setCollection(ArrayList<NoteFolder> f) {
        mFolders.setValue(f);
    }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }
}