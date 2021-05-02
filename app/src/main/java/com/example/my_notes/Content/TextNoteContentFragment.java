package com.example.my_notes.Content;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.R;

import com.example.my_notes.Model.NotesContent;
import com.example.my_notes.Model.TextNoteContent;

public class TextNoteContentFragment extends Fragment {
    private ImageView image, saveTextNote;
    private String noteId, noteFolderId, lastSegment, textWriten, imagepathset, textset;
    private EditText text;
    private TextNoteContentViewModel textNoteContentViewModel;
    private Context parentContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_textnote, container, false);

        parentContext = root.getContext();
        // Rebem Id de la nota amb Bundle()
        if (getArguments() != null){
            noteId = getArguments().getString("noteId");
            noteFolderId = getArguments().getString("folderId");
        }

        textNoteContentViewModel = new ViewModelProvider(this,
                new TextNoteContentViewModelFactory(requireActivity().getApplication(), noteId, noteFolderId)).get(TextNoteContentViewModel.class);


        text = root.findViewById(R.id.editTextTextNote);

        saveTextNote = root.findViewById(R.id.saveTextNoteContent);
        saveTextNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textWriten = text.getText().toString();
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Save note? ");

                mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textNoteContentViewModel.saveTextNoteContent(lastSegment, textWriten, noteId, noteFolderId);
                    }
                });
                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mydialog.show();
            }
        });

        setLiveDataObservers(root);
        return root;
    }

    public void setLiveDataObservers(View root) {
        //Subscribe the activity to the observable

        final Observer<NotesContent> observer = new Observer<NotesContent>() {
            @Override
            public void onChanged(NotesContent notesContent) {
                TextNoteContent textNoteContent = (TextNoteContent) notesContent;
                text.setText(textNoteContent.getTextNote());
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };

        textNoteContentViewModel.getTextNoteContent().observe(getViewLifecycleOwner(), observer);
        textNoteContentViewModel.getmToast().observe(getViewLifecycleOwner(), observerToast);
    }
}
