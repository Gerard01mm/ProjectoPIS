package com.example.my_notes.Content;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.R;

import com.example.my_notes.Model.NotesContent;
import com.example.my_notes.Model.TextNoteContent;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class TextNoteContentFragment extends Fragment {
    private ImageView image, saveTextNote, shareImage2, shareText;
    private String noteId, noteFolderId, lastSegment, textWriten, imagepathset, textset, title, tipus;
    private TextInputEditText userShareEmail;
    private Button cancelShare, acceptShare;
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
            title = getArguments().getString("title");
            tipus = getArguments().getString("tipus");
        }

        textNoteContentViewModel = new ViewModelProvider(this,
                new TextNoteContentViewModelFactory(requireActivity().getApplication(), noteId, noteFolderId)).get(TextNoteContentViewModel.class);

        text = root.findViewById(R.id.editTextTextNote);

        shareImage2 = root.findViewById(R.id.shareImage);
        saveTextNote = root.findViewById(R.id.saveImageNoteContent);
        shareText = root.findViewById(R.id.shareText);

        //Si la dada passada pel Bunddle és String = "shared", deshabilitem els botons del xtml.
        if(tipus.equals("shared")){
            shareImage2.setVisibility(View.INVISIBLE);
            saveTextNote.setVisibility(View.INVISIBLE);
            shareText.setVisibility(View.INVISIBLE);
            text.setKeyListener(null);
        }

        shareImage2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder shareNote = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogshare = inflater.inflate(R.layout.dialogshare, null);

                shareNote.setTitle("With which user do you want to share the note? ");
                userShareEmail = (TextInputEditText) dialogshare.findViewById(R.id.userShareEmail);
                cancelShare = (Button) dialogshare.findViewById(R.id.cancelButton);
                acceptShare = (Button) dialogshare.findViewById(R.id.acceptButton);

                AlertDialog content = shareNote.create();
                content.setView(dialogshare);

                acceptShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean emailCorrect = true;
                        textWriten = text.getText().toString();
                        Pattern pattern = Patterns.EMAIL_ADDRESS;
                        emailCorrect = pattern.matcher(userShareEmail.getText().toString()).matches();
                        if(!emailCorrect){
                            userShareEmail.setError("Incorrect email format!");
                        }else{
                            textNoteContentViewModel.checkEmail(userShareEmail.getText().toString(), noteId, noteFolderId, textWriten, title, userShareEmail, content);
                            textNoteContentViewModel.saveTextNoteContent(lastSegment, textWriten, noteId, noteFolderId);
                            Toast.makeText(parentContext, "The note automatically saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancelShare.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        content.dismiss();
                    }
                });
                content.show();
            }
        });

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

        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWriten = text.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textWriten);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
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
