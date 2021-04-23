package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderImageNotes extends RecyclerView.ViewHolder {
    private ImageView typeImage;
    private TextView imageNoteDate, imageNoteTitle;

    public ViewHolderImageNotes(View v){
        super(v);
        this.typeImage = v.findViewById(R.id.typeImage);
        this.imageNoteDate = v.findViewById(R.id.imageNoteDate);
        this.imageNoteTitle = v.findViewById(R.id.imageNoteTitle);
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
}
