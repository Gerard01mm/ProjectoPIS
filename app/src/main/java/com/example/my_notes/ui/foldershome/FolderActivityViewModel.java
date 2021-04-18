package com.example.my_notes.ui.foldershome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FolderActivityViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FolderActivityViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}