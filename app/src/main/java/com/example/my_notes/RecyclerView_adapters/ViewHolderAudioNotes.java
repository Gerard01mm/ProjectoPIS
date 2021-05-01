package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderAudioNotes extends RecyclerView.ViewHolder {
    private ImageButton play_btn, delete_btn;
    private TextView title, date;

    public ViewHolderAudioNotes(View v){
        super(v);
        this.play_btn = v.findViewById(R.id.play_btn);
        this.delete_btn = v.findViewById(R.id.delete_btn);
        this.title = v.findViewById(R.id.NoteTitle);
        this.date = v.findViewById(R.id.NoteDate);
    }

    public ImageButton getDelete_btn() {
        return delete_btn;
    }

    public ImageButton getPlay_btn() {
        return play_btn;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getTitle() {
        return title;
    }
}
