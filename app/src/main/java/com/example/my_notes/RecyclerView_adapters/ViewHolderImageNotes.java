package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderImageNotes extends RecyclerView.ViewHolder {
    private ImageView typeImage, editImageNote;
    private TextView imageNoteDate, imageNoteTitle;
    private LinearLayout imageNoteLayout;

    public ViewHolderImageNotes(View v){
        super(v);
        this.typeImage = v.findViewById(R.id.typeImage);
        this.imageNoteDate = v.findViewById(R.id.imageNoteDate);
        this.imageNoteTitle = v.findViewById(R.id.imageNoteTitle);
        this.imageNoteLayout = v.findViewById(R.id.imagenote_layout);
        this.editImageNote = v.findViewById(R.id.editImageNote);
    }

    public ImageView getTypeImage() {
        return typeImage;
    }

    public TextView getImageNoteDate() {
        return imageNoteDate;
    }

    public TextView getImageNoteTitle() {
        return imageNoteTitle;
    }

    public void setImageNoteDate(TextView imageNoteDate) {
        this.imageNoteDate = imageNoteDate;
    }

    public void setImageNoteTitle(TextView imageNoteTitle) {
        this.imageNoteTitle = imageNoteTitle;
    }

    public void setTypeImage(ImageView typeImage) {
        this.typeImage = typeImage;
    }

    public LinearLayout getImageNoteLayout() {
        return imageNoteLayout;
    }

    public void setImageNoteLayout(LinearLayout imageNoteLayout) {
        this.imageNoteLayout = imageNoteLayout;
    }

    public ImageView getEditImageNote() {
        return editImageNote;
    }

    public void setEditImageNote(ImageView editImageNote) {
        this.editImageNote = editImageNote;
    }
}
