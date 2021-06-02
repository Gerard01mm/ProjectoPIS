package com.example.my_notes.ui.foldershome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;
import com.example.my_notes.RecyclerView_adapters.FoldersAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

import com.example.my_notes.Model.NoteFolder;

import petrov.kristiyan.colorpicker.ColorPicker;

public class FolderFragment extends Fragment {

    private FolderViewModel folderViewModel;
    // Floating button functionality
    private ExtendedFloatingActionButton extendedFab;
    private Context parentContext;
    private RecyclerView fRecyclerView;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folderlist, container, false);
        fRecyclerView = root.findViewById(R.id.recycler_folder);
        parentContext = root.getContext();
        layoutManager = new LinearLayoutManager(parentContext);
        fRecyclerView.setLayoutManager(layoutManager);

        extendedFab = root.findViewById(R.id.extended_fab_folder);
        extendedFab.setOnClickListener((v) -> {
            // Change Extended FAB aspect and handle recording¡
            extendedFab.extend();
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            mydialog.setTitle("New folder title: ");

            final EditText input = new EditText(getActivity());
            mydialog.setView(input);

            ArrayList<String> colors = new ArrayList<>();
            /*colors.add("#FFEFFF");
            colors.add("#E6E6FA");
            colors.add("#E0F8F5");
            colors.add("#BEEDE5");
            colors.add("#A7D9C9");

            colors.add("#FFFFF0");
            colors.add("#FAEBD7");
            colors.add("#FAF0E6");
            colors.add("#FFF0F5");
            colors.add("#FFE4E1");

            colors.add("#E0FFFF");
            colors.add("#F0FFFF");
            colors.add("#F0F8FF");
            colors.add("#F0FFF0");
            colors.add("#F5F5DC");*/

            /*colors.add("#FFAACC");
            colors.add("#FFBBCC");
            colors.add("#FFDDCC");
            colors.add("#FFEECC");
            colors.add("#FFFFCC");

            colors.add("#CCAAFF");
            colors.add("#CCCCFF");
            colors.add("#CCDDFF");
            colors.add("#CCEEFF");
            colors.add("#CCFFFF");

            colors.add("#CCAACC");
            colors.add("#CCBBCC");
            colors.add("#CCDDCC");
            colors.add("#CCEECC");
            colors.add("#CCFFCC");*/

            colors.add("#FFEBEE");
            colors.add("#FCE4EC");
            colors.add("#F3E5F5");
            colors.add("#EDE7F6");
            colors.add("#E8EAF6");

            colors.add("#E3F2FD");
            colors.add("#E0F7FA");
            colors.add("#E0F2F1");
            colors.add("#E8F5E9");
            colors.add("#F9FBE7");

            colors.add("#FFFDE7");
            colors.add("#FFF8E1");
            colors.add("#FFF3E0");
            colors.add("#FBE9E7");
            colors.add("#EFEBE9");

            mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final ColorPicker colorPicker = new ColorPicker((Activity) parentContext);
                    colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                        @Override
                        public void onChooseColor(int position,int color) {
                            if (color != 0){
                                String input_text = input.getText().toString();
                                folderViewModel.addFolder(input_text, color);
                            }else{
                                String input_text = input.getText().toString();
                                folderViewModel.addFolder(input_text, Color.parseColor("#ffffff"));
                            }
                        }

                        @Override
                        public void onCancel(){
                            String input_text = input.getText().toString();
                            folderViewModel.addFolder(input_text, Color.WHITE);
                        }
                    }).setColors(colors)
                    .setRoundColorButton(true)
                    .setColumns(5)
                    .setTitle("Choose a folder color or press cancel to create a white one")
                    .show();
                }
            });
            mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            mydialog.show();
        });

        setLiveDataObservers();

        return root;
    }

    // This is the interface.
    public interface FragmentClickListener {
        void onFragmentClick(View v);
    }

    public void setLiveDataObservers() {
        //Subscribe the activity to the observable
        folderViewModel = new ViewModelProvider(requireActivity()).get(FolderViewModel.class);

        final Observer<ArrayList<NoteFolder>> observer = new Observer<ArrayList<NoteFolder>>() {
            @Override
            public void onChanged(ArrayList<NoteFolder> noteFolders) {
                FoldersAdapter newAdapter = new FoldersAdapter(parentContext, noteFolders);
                fRecyclerView.swapAdapter(newAdapter, false);
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };

        folderViewModel.getFolders().observe(getViewLifecycleOwner(), observer);
        folderViewModel.getToast().observe(getViewLifecycleOwner(), observerToast);
    }
}