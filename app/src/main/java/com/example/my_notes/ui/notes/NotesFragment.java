package com.example.my_notes.ui.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
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

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.example.my_notes.Model.Note;

public class NotesFragment extends Fragment {
    protected NotesViewModel notesViewModel;
    // Floating button functionality
    protected FloatingActionButton fab, fabText, fabImage, fabVoice;
    protected Context parentContext;
    protected RecyclerView nRecyclerView;
    protected LinearLayoutManager layoutManager;
    protected String folderId, fileName, audioName;
    protected MediaRecorder recorder;
    protected boolean isRecording = false;
    private int backgroundColor;
    private Spinner spinner_sort;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Rebem la Id de la carpeta que conté les notes amb Bundle()
        if (getArguments() != null){
            folderId = getArguments().getString("FolderId");
            backgroundColor = getArguments().getInt("FolderColor");
        }
        notesViewModel = new ViewModelProvider(this,
                new NotesViewModelFactory(requireActivity().getApplication(), folderId)).get(NotesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notelist, container, false);

        root.setBackgroundColor(backgroundColor);
        nRecyclerView = root.findViewById(R.id.recycler_notes);
        parentContext = root.getContext();
        layoutManager = new LinearLayoutManager(parentContext);
        nRecyclerView.setLayoutManager(layoutManager);

        spinner_sort = root.findViewById(R.id.spinner_sort);
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        notesViewModel.sortByCreationDate();
                        break;
                    case 1:
                        notesViewModel.sortByModifyDate();
                        break;
                    case 2:
                        notesViewModel.sortByTitle();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Title of the note: ");

                final EditText input = new EditText(parentContext);
                mydialog.setView(input);

                mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input_text = input.getText().toString();
                        notesViewModel.addTextNote(input_text);
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

        fabVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    Toast.makeText(getActivity(), "Audio recording stoped", Toast.LENGTH_SHORT).show();
                    fabVoice.setImageResource(R.drawable.mic_black_24dp);
                    stopRecording();
                    showPopup(nRecyclerView);

                } else {
                    Toast.makeText(getActivity(), "Audio recording started", Toast.LENGTH_SHORT).show();
                    fabVoice.setImageResource(R.drawable.ic_baseline_stop_24);
                    startRecording();
                }
            }
        });

        setLiveDataObservers();
        return root;
    }

    private void startRecording() {
        //Log.d("startRecording", "startRecording");

        recorder = new MediaRecorder();
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.GERMANY);
        String date = df.format(Calendar.getInstance().getTime());
        audioName = date+"3.gp";
        fileName =  getActivity().getExternalCacheDir().getAbsolutePath()+File.separator +audioName;
        //Log.d("startRecording", fileName);

        recorder.setOutputFile(fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.d("startRecording", "prepare() failed");
        }
        recorder.start();
        isRecording = true;
    }

    private void showPopup(RecyclerView nRecyclerView) {
        View popupView = getLayoutInflater().inflate(R.layout.save_audio_note_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 800, 600);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(nRecyclerView, Gravity.CENTER, 0, 0);

        // Initialize objects from layout
        TextInputLayout saveDescr = popupView.findViewById(R.id.note_description);
        Button saveButton = popupView.findViewById(R.id.save_button);
        saveButton.setOnClickListener((v) -> {
            String title = saveDescr.getEditText().getText().toString();
            if(title.length() > 15){
                saveDescr.setError("You exceed the maximum characters (15)");
            }else{
                notesViewModel.addAudioCard(title, fileName, folderId, audioName);
                popupWindow.dismiss();
            }
        });
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;

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

        final Observer<ArrayList<String>> observer1 = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {

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
