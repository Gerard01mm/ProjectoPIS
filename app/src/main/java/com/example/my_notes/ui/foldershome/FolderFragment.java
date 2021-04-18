package com.example.my_notes.ui.foldershome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class FolderFragment extends Fragment {

    private FolderActivityViewModel homeViewModel;
    // Floating button functionality
    private ExtendedFloatingActionButton extendedFab;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folderlist, container, false);

        extendedFab = root.findViewById(R.id.extended_fab_note);
        extendedFab.setOnClickListener((v) -> {
            // Change Extended FAB aspect and handle recording
            extendedFab.extend();
            /*if (isRecording) {
                extendedFab.extend();
                extendedFab.setIcon(
                        ContextCompat.getDrawable(
                                parentContext, android.R.drawable.ic_input_add));
                stopRecording();
                showPopup(mRecyclerView);

            } else {
                extendedFab.shrink();
                extendedFab.setIcon(
                        ContextCompat.getDrawable(
                                parentContext, android.R.drawable.ic_btn_speak_now));
                startRecording();
            }*/
        });
        return root;
    }

    // This is the interface.
    public interface FragmentClickListener {
        void onFragmentClick(View v);
    }
}