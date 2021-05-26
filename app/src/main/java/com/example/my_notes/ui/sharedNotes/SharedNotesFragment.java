package com.example.my_notes.ui.sharedNotes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.Model.Note;
import com.example.my_notes.R;
import com.example.my_notes.RecyclerView_adapters.ComplexNotesSharedAdapter;
import com.example.my_notes.ui.notes.NotesViewModel;
import com.example.my_notes.ui.notes.NotesViewModelFactory;

import java.util.ArrayList;


public class SharedNotesFragment extends Fragment {

    private SharedNotesViewModel sharedNotesViewModel;
    private RecyclerView nRecyclerView;
    private LinearLayoutManager layoutManager;
    private Context parentContext;
    private String folderId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null){
            folderId = getArguments().getString("FolderId");
        }

        sharedNotesViewModel = new ViewModelProvider(this,
                new SharedNotesViewModelFactory(requireActivity().getApplication(), folderId)).get(SharedNotesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_shared_notes2, container, false);
        parentContext = root.getContext();

        nRecyclerView = root.findViewById(R.id.recycler_notes);
        layoutManager = new LinearLayoutManager(parentContext);
        nRecyclerView.setLayoutManager(layoutManager);

        setLiveDataObservers();
        return root;
    }

    public void setLiveDataObservers() {
        //Subscribe the activity to the observable

        final Observer<ArrayList<Note>> observer = new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> notes) {
                //Log.d("SharedNotesFragment", "observer");
                ComplexNotesSharedAdapter newAdapter = new ComplexNotesSharedAdapter(parentContext, notes);
                nRecyclerView.swapAdapter(newAdapter, false);
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                //Log.d("SharedNotesFragment", "observer2");
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };

        sharedNotesViewModel.getNotes().observe(getViewLifecycleOwner(), observer);
        sharedNotesViewModel.getToast().observe(getViewLifecycleOwner(), observerToast);
    }
}