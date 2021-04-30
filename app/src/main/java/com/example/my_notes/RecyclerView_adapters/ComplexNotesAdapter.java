package com.example.my_notes.RecyclerView_adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Notes.AudioNote;
import Notes.ImageNote;
import Notes.Note;
import Notes.TextNote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import static android.app.Activity.RESULT_OK;

/**
 * Aquesta classe s'utilitzarà per adaptar el contingut d'una carpeta i per mostrar les notes
 * en la RecyclerView en el moment d'obrir una carpeta.
 */
public class ComplexNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Atributs de la classe
    private ArrayList<Note> localDataSet;
    private final Context parentContext;
    private final int TEXTNOTE = 0;
    private final int IMAGENOTE = 1;
    private final int AUDIONOTE = 2;

    public ComplexNotesAdapter(Context current, ArrayList<Note> an){
        this.parentContext = current;
        this.localDataSet = an;
    }

    @Override
    public int getItemViewType(int position) {
        //System.out.println(localDataSet.get(position).getId());
        if (localDataSet.get(position) instanceof TextNote) {
            return TEXTNOTE;
        } else if (localDataSet.get(position) instanceof ImageNote) {
            return IMAGENOTE;
        }else if (localDataSet.get(position) instanceof AudioNote){
            return AUDIONOTE;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TEXTNOTE:
                View v1 = inflater.inflate(R.layout.textnote_rv_card, parent, false);
                viewHolder = new ViewHolderTextNotes(v1);
                break;
            case IMAGENOTE:
                View v2 = inflater.inflate(R.layout.imagenote_rv_card, parent, false);
                viewHolder = new ViewHolderImageNotes(v2);
                break;
            case AUDIONOTE:
                /*View v3 = inflater.inflate(R.layout.audionote_rv_card, parent, false);
                viewHolder = new ViewHolderAudioNotes(v3);*/
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TEXTNOTE:
                ViewHolderTextNotes vh1 = (ViewHolderTextNotes) holder;
                configureViewHolderTextNotes(vh1, position);
                vh1.getTextNoteLayout().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextNote t = (TextNote) localDataSet.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("noteId", t.getId());
                        bundle.putString("folderId", t.getFolderId());
                        Navigation.findNavController(v).navigate(R.id.action_nav_noteList_to_TextNoteFragment, bundle);
                    }
                });
                break;
            case IMAGENOTE:
                ViewHolderImageNotes vh2 = (ViewHolderImageNotes) holder;
                configureViewHolderImageNotes(vh2, position);
                vh2.getImageNoteLayout().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageNote n = (ImageNote) localDataSet.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("noteId", n.getId());
                        bundle.putString("folderId", n.getFolderId());
                        Navigation.findNavController(v).navigate(R.id.action_nav_noteList_to_imageNoteFragment, bundle);
                    }
                });
                break;
            case AUDIONOTE:
                ViewHolderAudioNotes vh3 = (ViewHolderAudioNotes) holder;
                configureViewHolderAudioNotes(vh3, position);
                //TODO
                break;
            default:
                break;
        }
    }

    private void configureViewHolderTextNotes(ViewHolderTextNotes vh1, int position) {
        TextNote tnote = (TextNote) localDataSet.get(position);
        if (tnote != null) {
            // Create an instance of SimpleDateFormat used for formatting
            // the string representation of date according to the chosen pattern
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            Date dateC = tnote.getCreation_date();
            // Using DateFormat format method we can create a string
            // representation of a date with the defined format.
            String dateString = df.format(dateC);
            vh1.getNoteDate().setText(dateString);
            vh1.getNoteTitle().setText(tnote.getTitle());
        }
    }

    private void configureViewHolderImageNotes(ViewHolderImageNotes vh2, int position) {
        ImageNote inote = (ImageNote) localDataSet.get(position);
        if (inote != null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date dateC = inote.getCreation_date();

            String dateString = df.format(dateC);
            vh2.getImageNoteDate().setText(dateString);
            vh2.getImageNoteTitle().setText(inote.getTitle());
        }
    }

    private void configureViewHolderAudioNotes(ViewHolderAudioNotes vh3, int position) {
        AudioNote anote = (AudioNote) localDataSet.get(position);
        if (anote != null){
            /*
            * Configurar
            * */
        }
    }

    /**
     * Aquesta funció s'encarregarà de retornar el tamany de les dades.
     * @return
     */

    @Override
    public int getItemCount() {
        return this.localDataSet.size();
    }

}
