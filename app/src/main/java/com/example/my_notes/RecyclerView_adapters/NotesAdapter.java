package com.example.my_notes.RecyclerView_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

import Notes.AudioNote;
import Notes.Note;
import Notes.NoteFolder;
import Notes.TextNote;

/**
 * Aquesta classe s'utilitzarà per adaptar el contingut d'una carpeta i per mostrar les notes
 * en la RecyclerView en el moment d'obrir una carpeta.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{

    //Atributs de la classe
    private NoteFolder locallDataSet;


    //Aqui haure d'escriure la classe interna estatica de ViewHolder si ho veus NO TOCAR!!!!
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView type_image;
        private TextView title;
        private TextView date;


        /**
         * Aquesta funció serà el cosntructor de la classe del nostre ViewHolder. Cridarem al
         * superconstructor, i assignarem cada atribut al seu element de la layout.
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.type_image = (ImageView)itemView.findViewById(R.id.typeNoteImage);
            this.title = (TextView)itemView.findViewById(R.id.NoteTitle);
            this.date = (TextView)itemView.findViewById(R.id.NoteDate);
        }

        public ImageView getType_image(){ return this.type_image; }
        public TextView getTitle() { return this.title; }
        public TextView getDate() { return this.date; }
    }

    //Falta reescriure els metodes corresponents
    public NotesAdapter(NoteFolder data){ this.locallDataSet = data;}


    @NonNull
    @Override
    /**
     * Aquesta funció ens permetrà crear un ViewHolder
     */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.note_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = this.locallDataSet.get_index(position);

        //Depenent del tipus de nota, afagirem una imatge diferent:
        //Si es una nota de text, afegirem la imatge d'un text
        if (note instanceof TextNote){
            holder.getType_image().setImageResource(R.drawable.text_snippet_black_24dp);
        }

        //Si és una nota de veu, afegirem la imatge d'un microfon
        else if (note instanceof AudioNote){
            holder.getType_image().setImageResource(R.drawable.mic_black_24dp);
        }

        //Si el una nota amb imatge, afegirem la imatge d'una foto
        else{
            holder.getType_image().setImageResource(R.drawable.image_black_24dp);
        }

        //Afegirem el titol i a aprt afagirem la data de creació en forma d'string
        holder.getTitle().setText(note.getTitle());
        holder.getDate().setText(note.getCreation_date().toString());
    }


    /**
     * Aquesta funció s'encarregarà de retornar el tamany de les dades.
     * @return
     */

    @Override
    public int getItemCount() {
        return this.locallDataSet.get_size();
    }
}
