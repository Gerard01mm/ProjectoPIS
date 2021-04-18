package com.example.my_notes.RecyclerView_adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import Notes.NoteFolder;

/**
 * Aquesta classe s'utilitzarà per adaptar el contingut d'una carpeta i per mostrar les notes
 * en la RecyclerView en el moment d'obrir una carpeta.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{

    //Atributs de la classe
    private NoteFolder locallDataSet;


    //Aqui haure d'escriure la classe interna estatica de ViewHolder si ho veus NO TOCAR!!!!
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //Falta reescriure els metodes corresponents
    public NotesAdapter(NoteFolder data){ this.locallDataSet = data;}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
