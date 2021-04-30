package com.example.my_notes.Content;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TextNoteContentViewModelFactory implements ViewModelProvider.Factory {
    private static final String DEFAULT_LIMIT = "4";
    static Application application;
    static String noteId, folderId;

    public static TextNoteContentViewModelFactory createFactory(Activity activity) {
        application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new TextNoteContentViewModelFactory(application, noteId, folderId);
    }

    public TextNoteContentViewModelFactory(Application application, String noteId, String folderId) {
        TextNoteContentViewModelFactory.application = application;
        TextNoteContentViewModelFactory.noteId = noteId;
        TextNoteContentViewModelFactory.folderId = folderId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return (T) new TextNoteContentViewModel(application, noteId, folderId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}