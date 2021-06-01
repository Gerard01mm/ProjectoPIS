package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderTextNotes extends RecyclerView.ViewHolder {
    private ImageView typeText, editTextNote;
    private TextView NoteDate, NoteTitle, sharedBy, sharedEmail;
    private LinearLayout textNoteLayout;

    public ViewHolderTextNotes(View v){
        super(v);
        this.typeText = v.findViewById(R.id.typeImage);
        this.NoteDate = v.findViewById(R.id.NoteDate);
        this.NoteTitle = v.findViewById(R.id.imageNoteTitle2);
        this.textNoteLayout = v.findViewById(R.id.textnote_layout);
        this.editTextNote = v.findViewById(R.id.editTextNote);
    }
    public ViewHolderTextNotes(View v, String nota){
        super(v);
        this.typeText = v.findViewById(R.id.typeImage);
        this.NoteDate = v.findViewById(R.id.NoteDate);
        this.NoteTitle = v.findViewById(R.id.imageNoteTitle2);
        this.textNoteLayout = v.findViewById(R.id.textnote_layout);
        this.editTextNote = v.findViewById(R.id.editTextNote);
        this.sharedBy = v.findViewById(R.id.sharedBy);
        this.sharedEmail = v.findViewById(R.id.sharedEmail);
    }

    public ImageView getTypeText() {
        return typeText;
    }

    public TextView getNoteDate() {
        return NoteDate;
    }

    public TextView getNoteTitle(){
        return NoteTitle;
    }

    public TextView getSharedBy() {
        return sharedBy;
    }

    public TextView getSharedEmail() {
        return sharedEmail;
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

    public void setSharedBy(TextView sharedBy){
        this.sharedBy = sharedBy;
    }

    public void setSharedEmail(TextView sharedEmail){
        this.sharedEmail = sharedEmail;
    }

    public LinearLayout getTextNoteLayout() {
        return textNoteLayout;
    }

    public void setTextNoteLayout(LinearLayout textNoteLayout) {
        this.textNoteLayout = textNoteLayout;
    }

    public ImageView getEditTextNote() {
        return editTextNote;
    }

    public void setEditTextNote(ImageView editTextNote) {
        this.editTextNote = editTextNote;
    }
}
