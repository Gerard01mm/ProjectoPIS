package com.example.my_notes.Content;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.my_notes.R;

import java.util.ArrayList;

import Notes.Note;

public class ImageNoteContentAdapter {
    private Context parentContext;
    private ImageNoteContent imageNoteContent;
    private ImageView addImage;
    private EditText editText;

    public ImageNoteContentAdapter(Context current, View view, ImageNoteContent im){
        this.parentContext = current;
        this.imageNoteContent = im;
        this.addImage = view.findViewById(R.id.addImage);
        this.editText = view.findViewById(R.id.editTextImageNote);

        //addImage.setImageURI();
        editText.setText(im.getTextNote());
    }




}
