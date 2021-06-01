package com.example.my_notes.RecyclerView_adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.Model.AudioNote;
import com.example.my_notes.Model.ImageNote;
import com.example.my_notes.Model.Note;
import com.example.my_notes.Model.TextNote;
import com.example.my_notes.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ComplexNotesSharedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Atributs de la classe
    private ArrayList<Note> localDataSet;
    private final Context parentContext;
    private final int TEXTNOTE = 0, IMAGENOTE = 1;

    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ComplexNotesSharedAdapter(Context current, ArrayList<Note> an){
        this.parentContext = current;
        this.localDataSet = an;
    }

    @Override
    public int getItemViewType(int position) {
        if (localDataSet.get(position) instanceof TextNote) {
            return TEXTNOTE;
        } else if (localDataSet.get(position) instanceof ImageNote) {
            return IMAGENOTE;
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
                View v1 = inflater.inflate(R.layout.textnoteshared_rv_card, parent, false);
                viewHolder = new ViewHolderTextNotes(v1, "");
                break;
            case IMAGENOTE:
                View v2 = inflater.inflate(R.layout.imagenoteshared_rv_card, parent, false);
                viewHolder = new ViewHolderImageNotes(v2, "");
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
                        bundle.putString("title", t.getTitle());
                        /*Passo aquesta dada per saber si estem accedint desde la opcio del menu "shared" o "notes".
                         * Per a poder deshabilitar els ImageBottom a l'hora de obrir una nota compartida.*/
                        bundle.putString("tipus", "shared");
                        Navigation.findNavController(v).navigate(R.id.action_nav_sharedNotes_to_textNoteContentFragment, bundle);
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
                        bundle.putString("title", n.getTitle());
                        /*Passo aquesta dada per saber si estem accedint desde la opcio del menu "shared" o "notes".
                         * Per a poder deshabilitar els ImageBottom a l'hora de obrir una nota compartida.*/
                        bundle.putString("tipus", "shared");
                        Navigation.findNavController(v).navigate(R.id.action_nav_sharedNotes_to_imageNoteFragment, bundle);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.localDataSet.size();
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
            vh1.getSharedEmail().setText(tnote.getOwner());
            vh1.getTextNoteLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Remove note? ");

                    mydialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextNote t = (TextNote) localDataSet.get(position);
                            t.deleteSharedTextNote();
                            localDataSet.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, localDataSet.size());
                        }
                    });
                    mydialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.show();
                    return true;
                }
            });
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
            vh2.getSharedEmail().setText(inote.getOwner());
            vh2.getImageNoteLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Remove note? ");

                    mydialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ImageNote i = (ImageNote) localDataSet.get(position);
                            i.deleteImageNote();
                            localDataSet.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, localDataSet.size());
                        }
                    });
                    mydialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.show();
                    return true;
                }
            });
        }
    }
}
