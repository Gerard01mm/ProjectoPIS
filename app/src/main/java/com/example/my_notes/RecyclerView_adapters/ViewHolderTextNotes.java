package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderTextNotes extends RecyclerView.ViewHolder {
    private ImageView typeText;
    private TextView NoteDate, NoteTitle;

    public ViewHolderTextNotes(View v){
        super(v);
        this.typeText = v.findViewById(R.id.typeText);
        this.NoteDate = v.findViewById(R.id.NoteDate);
        this.NoteTitle = v.findViewById(R.id.NoteTitle);
    }

    public ImageView getTypeText() {
        return typeText;
    }

    public TextView getNoteDate() {
        return NoteDate;
    }

    public TextView getNoteTitle() {
        return NoteTitle;
    }

    public void setNoteDate(TextView noteDate) {
        NoteDate = noteDate;
    }

    public void setNoteTitle(TextView noteTitle) {
        NoteTitle = noteTitle;
    }

    public void setTypeText(ImageView typeText) {
        this.typeText = typeText;
    }
}
