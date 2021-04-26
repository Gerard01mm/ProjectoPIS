package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderTextNotes extends RecyclerView.ViewHolder {
    private ImageView typeText;
    private TextView NoteDate, NoteTitle;
    private LinearLayout textNoteLayout;

    public ViewHolderTextNotes(View v){
        super(v);
        this.typeText = v.findViewById(R.id.typeText);
        this.NoteDate = v.findViewById(R.id.NoteDate);
        this.NoteTitle = v.findViewById(R.id.NoteTitle);
        this.textNoteLayout = v.findViewById(R.id.textnote_layout);
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

    public LinearLayout getTextNoteLayout() {
        return textNoteLayout;
    }

    public void setTextNoteLayout(LinearLayout textNoteLayout) {
        this.textNoteLayout = textNoteLayout;
    }
}
