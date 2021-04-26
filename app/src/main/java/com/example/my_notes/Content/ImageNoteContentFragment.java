package com.example.my_notes.Content;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.my_notes.ui.notes.NotesViewModel;
import com.example.my_notes.ui.notes.NotesViewModelFactory;
import com.google.firebase.storage.StorageReference;

import Notes.NoteFolder;

import static android.app.Activity.RESULT_OK;

public class ImageNoteContentFragment extends Fragment {
    private ImageView image, saveImageNote;
    private String noteId, noteFolderId, lastSegment, textWriten;
    private EditText text;
    private ImageNoteContentViewModel imageNoteViewModel;
    private Context parentContext;

    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_imagenote, container, false);

        parentContext = root.getContext();

        imageNoteViewModel = new ViewModelProvider(this,
                new ImageNoteContentViewModelFactory(requireActivity().getApplication(), noteId, noteFolderId)).get(ImageNoteContentViewModel.class);
        // Rebem Id de la nota amb Bundle()
        if (getArguments() != null){
            noteId = getArguments().getString("noteId");
            noteFolderId = getArguments().getString("folderId");
            imageNoteViewModel.setImageNoteFolderId(noteFolderId);
            imageNoteViewModel.setImageNoteId(noteId);
        }

        image = root.findViewById(R.id.addImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarImatge();
            }
        });

        text = root.findViewById(R.id.editTextImageNote);

        saveImageNote = root.findViewById(R.id.saveImageNoteContent);
        saveImageNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textWriten = text.getText().toString();
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Save note? ");

                mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageNoteViewModel.saveImageNoteContent(lastSegment, textWriten, noteId, noteFolderId);
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

    public void carregarImatge(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(Intent.createChooser(intent, "Select an application: "), 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Uri path = data.getData();
            image.setImageURI(path);
            lastSegment = path.getLastPathSegment();
        }
    }

    public void setLiveDataObservers(View root) {
        //Subscribe the activity to the observable

        final Observer<ImageNoteContent> observer = new Observer<ImageNoteContent>() {
            @Override
            public void onChanged(ImageNoteContent im) {
                ImageNoteContentAdapter newAdapter = new ImageNoteContentAdapter(parentContext, root, im);
                //newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };
/*
        notesViewModel.getNotes().observe(getViewLifecycleOwner(), observer);
        notesViewModel.getToast().observe(getViewLifecycleOwner(), observerToast);*/
    }
}
