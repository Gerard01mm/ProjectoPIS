package com.example.my_notes.Content;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.ui.notes.NotesViewModel;
import com.example.my_notes.ui.notes.NotesViewModelFactory;

public class ImageNoteContentViewModelFactory implements ViewModelProvider.Factory {
    private static final String DEFAULT_LIMIT = "4";
    static Application application;
    static String noteId, folderId;

    public static ImageNoteContentViewModelFactory createFactory(Activity activity) {
        application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Not yet attached to Application");
        }
        return new ImageNoteContentViewModelFactory(application, noteId, folderId);
    }

    public ImageNoteContentViewModelFactory(Application application, String noteId, String folderId) {
        ImageNoteContentViewModelFactory.application = application;
        ImageNoteContentViewModelFactory.noteId = noteId;
        ImageNoteContentViewModelFactory.folderId = folderId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return (T) new ImageNoteContentViewModel(application, noteId, folderId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}