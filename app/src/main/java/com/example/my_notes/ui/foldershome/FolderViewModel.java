package com.example.my_notes.ui.foldershome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;

import java.util.ArrayList;

import com.example.my_notes.Model.NoteFolder;

public class FolderViewModel extends ViewModel implements DatabaseAdapter.vmInterface{

    private MutableLiveData<ArrayList<NoteFolder>> mFolders;
    private MutableLiveData<String> mToast;
    private DatabaseAdapter da;

    public FolderViewModel() {
        mFolders = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        da = new DatabaseAdapter(this);
        da.getCollectionFoldersByUser();
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

    public void addFolder(String titol, int color){
        NoteFolder nf;
        if (titol.isEmpty()){
            nf = new NoteFolder(color);
        }else{
            nf = new NoteFolder(titol, color);
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

    /*public void deleteFolder(String id){
        da.deleteFolder(id);
    }*/

    @Override
    public void setCollection(ArrayList/*<NoteFolder>*/ f) {
        mFolders.setValue(f);
    }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }
}