package com.example.my_notes.ui.notes;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;

public class NotesViewModelFactory implements ViewModelProvider.Factory {
    private static final String DEFAULT_LIMIT = "4";
    static Application application;
    static String created;

    public static NotesViewModelFactory createFactory(Activity activity) {
        application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new NotesViewModelFactory(application, created);
    }

    public NotesViewModelFactory(Application application, String created) {
        NotesViewModelFactory.application = application;
        NotesViewModelFactory.created = created;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            System.out.println(created);
            return (T) new NotesViewModel(application, created);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}