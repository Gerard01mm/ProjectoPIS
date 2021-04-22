package com.example.my_notes.ui.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_notes.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLOutput;

public class NotesFragment extends Fragment {
    private NotesViewModel notesViewModel;
    // Floating button functionality
    private FloatingActionButton fab, fabText, fabImage, fabVoice, prueba;
    private Context parentContext;
    private RecyclerView nRecyclerView;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notesViewModel =
                new ViewModelProvider(this).get(NotesViewModel.class);
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
                System.out.println("HOLA");

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


        notesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}
