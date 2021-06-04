package com.example.my_notes.Content;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.my_notes.R;
import com.example.my_notes.Utils.UriUtils.UriUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

import com.example.my_notes.Model.ImageNoteContent;
import com.example.my_notes.Model.NotesContent;

import static android.app.Activity.RESULT_OK;

public class ImageNoteContentFragment extends Fragment {
    private ImageView image, saveImageNote, shareImage2;
    private String noteId, noteFolderId, lastSegment, textWriten, imagepathset, textset, title, tipus;
    private TextInputEditText userShareEmail;
    private Button cancelShare, acceptShare;
    private EditText text;
    private ImageNoteContentViewModel imageNoteContentViewModel;
    private Context parentContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_imagenote, container, false);

        parentContext = root.getContext();
        // Rebem Id de la nota amb Bundle()
        if (getArguments() != null){
            noteId = getArguments().getString("noteId");
            noteFolderId = getArguments().getString("folderId");
            title = getArguments().getString("title");
            tipus = getArguments().getString("tipus");
        }

        imageNoteContentViewModel = new ViewModelProvider(this,
                new ImageNoteContentViewModelFactory(requireActivity().getApplication(), noteId, noteFolderId)).get(ImageNoteContentViewModel.class);

        text = root.findViewById(R.id.editTextImageNote);
        saveImageNote = root.findViewById(R.id.saveImageNoteContent);
        shareImage2 = root.findViewById(R.id.shareImage);
        image = root.findViewById(R.id.addImage);

        //Si la dada passada pel Bunddle és String = "shared", deshabilitem els botons del xtml.
        if(tipus.equals("shared")){
            shareImage2.setVisibility(View.INVISIBLE);
            saveImageNote.setVisibility(View.INVISIBLE);
            text.setKeyListener(null);
            image.setOnKeyListener(null);
        } else {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carregarImatge();
                }
            });

        }

        shareImage2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textWriten = text.getText().toString();
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Would you like to save your recent changes in the note first?");

                mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageNoteContentViewModel.saveImageNoteContent(lastSegment, textWriten, noteId, noteFolderId);
                        Toast.makeText(parentContext, "Saved note", Toast.LENGTH_SHORT).show();
                        dialogShare();
                    }
                });
                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialogShare();
                    }
                });
                mydialog.show();
            }
        });

        saveImageNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWriten = text.getText().toString();
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Save note?");

                mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageNoteContentViewModel.saveImageNoteContent(lastSegment, textWriten, noteId, noteFolderId);
                        Toast.makeText(parentContext, "Saved note", Toast.LENGTH_SHORT).show();
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

    public void dialogShare(){
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
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                textWriten = text.getText().toString();
                emailCorrect = pattern.matcher(userShareEmail.getText().toString()).matches();
                if(!emailCorrect){
                    userShareEmail.setError("Incorrect email format!");
                }else{
                    imageNoteContentViewModel.checkEmail(userShareEmail.getText().toString(), noteId, noteFolderId, textWriten, title, userShareEmail, content);
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
            Glide.with(parentContext)
                    .load(UriUtils.getPathFromUri(parentContext, path))
                    .into(image);
            lastSegment = UriUtils.getPathFromUri(parentContext, path);;
            System.out.println("ES AKI:" + UriUtils.getPathFromUri(parentContext, path));
        }
    }

    public void setLiveDataObservers(View root) {
        //Subscribe the activity to the observable

        final Observer<NotesContent> observer = new Observer<NotesContent>() {
            @Override
            public void onChanged(NotesContent notesContent) {
                ImageNoteContent imageNoteContent = (ImageNoteContent) notesContent;
                if(imageNoteContent.getImagepath() != null){
                    Glide.with(parentContext)
                        .load(imageNoteContent.getImagepath())
                        .into(image);
                }
                text.setText(imageNoteContent.getTextNote());
                System.out.println("ES realment AKI:" + lastSegment);
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };

        imageNoteContentViewModel.getImageNoteContent().observe(getViewLifecycleOwner(), observer);
        imageNoteContentViewModel.getmToast().observe(getViewLifecycleOwner(), observerToast);
    }
}
