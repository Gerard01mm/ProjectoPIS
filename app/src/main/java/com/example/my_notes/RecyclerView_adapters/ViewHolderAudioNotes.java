package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderAudioNotes extends RecyclerView.ViewHolder {
    private ImageButton play_btn, delete_btn, pause_btn;
    private TextView title, date;
    private SeekBar seekBar;

    public ViewHolderAudioNotes(View v){
        super(v);
        this.play_btn = v.findViewById(R.id.play_btn);
        this.delete_btn = v.findViewById(R.id.delete_btn);
        this.title = v.findViewById(R.id.NoteTitle);
        this.date = v.findViewById(R.id.NoteDate);
        this.pause_btn = v.findViewById(R.id.pauseButton);
        this.seekBar = v.findViewById(R.id.audioSeekBar);
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

    public ImageButton getPause_btn() {
        return pause_btn;
    }

    public void setPause_btn(ImageButton pause_btn) {
        this.pause_btn = pause_btn;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public void setDelete_btn(ImageButton delete_btn) {
        this.delete_btn = delete_btn;
    }

    public void setPlay_btn(ImageButton play_btn) {
        this.play_btn = play_btn;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }
}
