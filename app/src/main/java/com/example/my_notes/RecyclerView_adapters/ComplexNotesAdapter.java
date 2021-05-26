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

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.my_notes.Model.AudioNote;
import com.example.my_notes.Model.ImageNote;
import com.example.my_notes.Model.Note;
import com.example.my_notes.Model.TextNote;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * Aquesta classe s'utilitzarà per adaptar el contingut d'una carpeta i per mostrar les notes
 * en la RecyclerView en el moment d'obrir una carpeta.
 */
public class ComplexNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Atributs de la classe
    private ArrayList<Note> localDataSet;
    private final Context parentContext;
    private final int TEXTNOTE = 0, IMAGENOTE = 1, AUDIONOTE = 2;

    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());

    private MediaPlayer player;

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
                Log.d("OnCreateViewholder----------------->", "entro en nota de texto");
                View v1 = inflater.inflate(R.layout.textnote_rv_card, parent, false);
                viewHolder = new ViewHolderTextNotes(v1);
                break;
            case IMAGENOTE:
                Log.d("OnCreateViewholder----------------->", "entro en imagen");
                View v2 = inflater.inflate(R.layout.imagenote_rv_card, parent, false);
                viewHolder = new ViewHolderImageNotes(v2);
                break;
            case AUDIONOTE:
                View v3 = inflater.inflate(R.layout.audionote_rv_card, parent, false);
                viewHolder = new ViewHolderAudioNotes(v3);
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
                        //Passo aquesta dada per saber si estem accedint desde la opcio del menu "shared" o "notes".
                        bundle.putString("tipus", "notes");
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
            vh1.getEditTextNote().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Rename the note: ");

                    final EditText input = new EditText(parentContext);
                    mydialog.setView(input);

                    mydialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_text = input.getText().toString();
                            TextNote t = (TextNote) localDataSet.get(position);
                            t.setTitle(input_text);
                            t.updateTextNote();
                            notifyDataSetChanged();
                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.show();
                }
            });
            vh1.getTextNoteLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Remove note? ");

                    mydialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextNote t = (TextNote) localDataSet.get(position);
                            t.deleteTextNote();
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

            vh2.getEditImageNote().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Rename the note: ");

                    final EditText input = new EditText(parentContext);
                    mydialog.setView(input);

                    mydialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_text = input.getText().toString();
                            ImageNote i = (ImageNote) localDataSet.get(position);
                            i.setTitle(input_text);
                            i.updateImageNote();
                            notifyDataSetChanged();
                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.show();
                }
            });
        }
    }

    private void configureViewHolderAudioNotes(ViewHolderAudioNotes vh3, int position) {
        AudioNote anote = (AudioNote) localDataSet.get(position);
        if (anote != null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date dateC = anote.getCreation_date();

            String dateString = df.format(dateC);
            vh3.getDate().setText(dateString);
            vh3.getTitle().setText(anote.getTitle());
            vh3.getPlay_btn().setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    vh3.getPlay_btn().setVisibility(View.INVISIBLE);
                    vh3.getPause_btn().setVisibility(View.VISIBLE);
                    playAudio(position, vh3);
                }
            });

            vh3.getPause_btn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh3.getPause_btn().setVisibility(View.INVISIBLE);
                    vh3.getPlay_btn().setVisibility(View.VISIBLE);
                    stopAudio(position, vh3);
                }
            });

            vh3.getEditbtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Rename the note: ");

                    final EditText input = new EditText(parentContext);
                    mydialog.setView(input);

                    mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_text = input.getText().toString();
                            AudioNote an = (AudioNote)localDataSet.get(position);
                            an.setTitle(input_text);
                            an.updateAudioNote();
                            notifyDataSetChanged();
                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.show();
                }
            });

            vh3.getLinearLayout().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                    mydialog.setTitle("Remove the note?");

                    mydialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AudioNote an = (AudioNote)localDataSet.get(position);
                            an.removeAudioNote();
                            localDataSet.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, localDataSet.size());
                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    /**
     * Aquesta funció s'encarregarà de retornar el tamany de les dades.
     * @return
     */

    @Override
    public int getItemCount() {
        return this.localDataSet.size();
    }

    private void playAudio(int position, ViewHolderAudioNotes viewHolderAudioNotes) {
        try {
            this.player = new MediaPlayer();
            AudioNote an = (AudioNote) localDataSet.get(position);
            String fileName = an.getAdress();
            //Log.d("startPlaying", fileName);
            player.setDataSource(fileName);
            player.prepare();
            player.start();

            runnable = new Runnable() {
                @Override
                public void run() {
                    viewHolderAudioNotes.getSeekBar().setProgress(player.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            };
            int duration = player.getDuration();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    viewHolderAudioNotes.getPause_btn().setVisibility(View.INVISIBLE);
                    viewHolderAudioNotes.getPlay_btn().setVisibility(View.VISIBLE);
                }
            });

            viewHolderAudioNotes.getSeekBar().setMax(player.getDuration());
            handler.postDelayed(runnable, 0);

        } catch (IOException e) {
            Log.d("startPlaying", "prepare() failed");
        }
    }

    public void stopAudio(int position, ViewHolderAudioNotes viewHolderAudioNotes) {
        viewHolderAudioNotes.getSeekBar().setProgress(0);
        player.stop();
        handler.removeCallbacks(runnable);
    }
}
