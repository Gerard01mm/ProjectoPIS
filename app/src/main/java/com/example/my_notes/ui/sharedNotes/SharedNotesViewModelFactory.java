package com.example.my_notes.ui.sharedNotes;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.ui.notes.NotesViewModel;
import com.example.my_notes.ui.notes.NotesViewModelFactory;

public class SharedNotesViewModelFactory implements ViewModelProvider.Factory {
    private static final String DEFAULT_LIMIT = "4";
    static Application application;
    static String created;

    public static SharedNotesViewModelFactory createFactory(Activity activity) {
        application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new SharedNotesViewModelFactory(application, created);
    }

    public SharedNotesViewModelFactory(Application application, String created) {
        SharedNotesViewModelFactory.application = application;
        SharedNotesViewModelFactory.created = created;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return (T) new SharedNotesViewModel(application, created);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
