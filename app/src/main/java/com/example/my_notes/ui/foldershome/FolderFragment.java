package com.example.my_notes.ui.foldershome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.my_notes.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class FolderFragment extends Fragment {

    private FolderViewModel folderViewModel;
    // Floating button functionality
    private ExtendedFloatingActionButton extendedFab;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folderlist, container, false);

        extendedFab = root.findViewById(R.id.extended_fab_note);
        extendedFab.setOnClickListener((v) -> {
            // Change Extended FAB aspect and handle recording¡
            extendedFab.extend();
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            mydialog.setTitle("New folder title: ");

            final EditText input = new EditText(getActivity());
            mydialog.setView(input);

            mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input_text = input.getText().toString();
                    folderViewModel.addFolder(input_text);
                }
            });
            mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            mydialog.show();

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