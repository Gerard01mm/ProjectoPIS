package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

public class ViewHolderAudioNotes extends RecyclerView.ViewHolder {
    private ImageView play_btn, pause_btn;
    private TextView title, date;
    private SeekBar seekBar;
    private LinearLayout linearLayout;
    private ImageButton editbtn;

    public ViewHolderAudioNotes(View v){
        super(v);
        this.play_btn = v.findViewById(R.id.play_btn);
        this.title = v.findViewById(R.id.NoteTitle);
        this.date = v.findViewById(R.id.NoteDate);
        this.pause_btn = v.findViewById(R.id.pause_btn);
        this.seekBar = v.findViewById(R.id.audioSeekBar);
        this.linearLayout = v.findViewById(R.id.audioNote_layout);
        this.editbtn = v.findViewById(R.id.edit_btn);
    }


    public TextView getDate() {
        return date;
    }

    public TextView getTitle() {
        return title;
    }

    public ImageView getPause_btn() {
        return pause_btn;
    }

    public ImageView getPlay_btn(){
        return play_btn;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public LinearLayout getLinearLayout(){ return this.linearLayout;}

    public ImageButton getEditbtn() {
        return editbtn;
    }
}
