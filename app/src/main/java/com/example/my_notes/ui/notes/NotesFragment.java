package com.example.my_notes.ui.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.my_notes.R;
import com.example.my_notes.RecyclerView_adapters.ComplexNotesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Notes.Note;

public class NotesFragment extends Fragment {
    private NotesViewModel notesViewModel;
    // Floating button functionality
    private FloatingActionButton fab, fabText, fabImage, fabVoice, prueba;
    private Context parentContext;
    private RecyclerView nRecyclerView;
    private LinearLayoutManager layoutManager;

    private String folderId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Rebem la Id de la carpeta que conté les notes amb Bundle()
        if (getArguments() != null){
            folderId = getArguments().getString("FolderId");
        }
        notesViewModel = new ViewModelProvider(this,
                new NotesViewModelFactory(requireActivity().getApplication(), folderId)).get(NotesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notelist, container, false);

        nRecyclerView = root.findViewById(R.id.recycler_notes);
        parentContext = root.getContext();
        layoutManager = new LinearLayoutManager(parentContext);
        nRecyclerView.setLayoutManager(layoutManager);

        fab = root.findViewById(R.id.extended_fab_notes);
        fabText = root.findViewById(R.id.extended_fab_textNote);
        fabImage = root.findViewById(R.id.extended_fab_imageNote);
        fabVoice = root.findViewById(R.id.extended_fab_audioNote);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!fabText.isShown()){
                    fabText.show();
                    fabImage.show();
                    fabVoice.show();
                }else{
                    fabVoice.hide();
                    fabImage.hide();
                    fabText.hide();
                }
            }
        });

        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Title of the note: ");

                final EditText input = new EditText(parentContext);
                mydialog.setView(input);

                mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input_text = input.getText().toString();
                        notesViewModel.addImageNote(input_text);
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

        setLiveDataObservers();
        return root;
    }

    public void setLiveDataObservers() {
        //Subscribe the activity to the observable

        final Observer<ArrayList<Note>> observer = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> notes) {
                ComplexNotesAdapter newAdapter = new ComplexNotesAdapter(parentContext, notes);
                nRecyclerView.swapAdapter(newAdapter, false);
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };

        notesViewModel.getNotes().observe(getViewLifecycleOwner(), observer);
        notesViewModel.getToast().observe(getViewLifecycleOwner(), observerToast);
    }
}
