package com.example.my_notes.RecyclerView_adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

import java.util.ArrayList;

import com.example.my_notes.Model.NoteFolder;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Aquesta clase s'utilitzarà per adaptar la visualització en el Recycelr View de les carpetes.
 */
public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder>{

    //Aqui tenim l'ArrayList de portafolis.
    private final ArrayList<NoteFolder> localDataSet;
    private final Context parentContext;

    /**
     * Clase viewHolder interna
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView folder_title;
        private final LinearLayout fLayout;
        private final ImageView editNoteName;
        private final CardView fCardView;
        /**
         * Constructor de la clase. Rep una View per poder cridar al super constructor i despres
         * assignarem el TextView folder_title al TextView folders_title, a la layout foldercard.xml
         * @param view
         */
        public ViewHolder(View view){
            super(view);
            this.folder_title = (TextView)view.findViewById(R.id.folder_title);
            this.fLayout = view.findViewById(R.id.folder_linear);
            this.editNoteName = view.findViewById(R.id.editNoteName);
            this.fCardView = view.findViewById(R.id.folder_card_view);
        }

        /**
         * Ens retorna el camp de text
         * @return TextView con el título del protafolios
         */
        public TextView getFolder_title(){ return this.folder_title; }
        public LinearLayout getFolderLayout(){ return this.fLayout; }
        public ImageView getImageEditName() { return this.editNoteName; }
        public CardView getfCardView(){ return this.fCardView; }
    }


    /**
     * Constructor de la clase Folders Adapter. Rep com a parametres un ArrayList de carpetes i
     * l'assigna a la variable localDataSet. També el contexte.
     * @param current context
     * @param folders arraylist amb les carpetes
     */
    public FoldersAdapter(Context current, ArrayList<NoteFolder> folders){
        this.parentContext = current;
        this.localDataSet = folders;
    }


    /**
     * Aquesta funció ens permetrà crear un ViewHolder
     * @param viewGroup
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.folder_card, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Aquesta funció s'encarregarà d'anar reemplaçant els elements que apareixen a la View
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position){

        //Escriurà a la cardView el titol de la carpeta
        viewHolder.getFolder_title().setText(localDataSet.get(position).get_Title());
        viewHolder.getfCardView().setCardBackgroundColor(localDataSet.get(position).getColor());

        LinearLayout folder_layout = viewHolder.getFolderLayout();
        folder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("FolderId", localDataSet.get(position).getId());
                bundle.putInt("FolderColor", localDataSet.get(position).getColor());
                Navigation.findNavController(v).navigate(R.id.action_nav_folder_to_notes, bundle);
            }
        });
        folder_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("Remove folder? ");

                mydialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoteFolder n = localDataSet.get(position);
                        n.removeFolder();
                        /*for (NoteFolder i: localDataSet){
                            System.out.println(i.get_Title());
                        }*/
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
        ImageView op = viewHolder.getImageEditName();
        op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(parentContext);
                mydialog.setTitle("What do you want to modify?");

                mydialog.setPositiveButton("Title", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder mydialog1 = new AlertDialog.Builder(parentContext);
                        mydialog1.setTitle("Rename the folder: ");

                        final EditText input = new EditText(parentContext);
                        mydialog1.setView(input);

                        mydialog1.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String input_text = input.getText().toString();
                                NoteFolder n = localDataSet.get(position);
                                n.set_title(input_text);
                                n.updateFolder();
                                notifyDataSetChanged();
                            }
                        });
                        mydialog1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        mydialog1.show();
                    }
                });
                mydialog.setNegativeButton("Color", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ColorPicker colorPicker = new ColorPicker((Activity) parentContext);
                        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int idx,int color) {
                                NoteFolder n = localDataSet.get(position);
                                if(color != 0){
                                    n.setColor(color);
                                    n.updateFolder();
                                    notifyDataSetChanged();
                                }else{
                                    n.setColor(Color.parseColor("#ffffff"));
                                    n.updateFolder();
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancel(){
                                dialog.cancel();
                            }
                        })
                        .setRoundColorButton(true)
                        .setColumns(5)
                        .setTitle("Choose a new folder color or press OK without selecting any color for " +
                                "choosing white")
                        .show();
                    }
                });
                mydialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mydialog.show();
            }
        });
    }


    /**
     * Aquesta funció s'encarregarà de retornar la mida de la llista de portafolis
     * @return mida de localDataSet
     */
    @Override
    public int getItemCount(){ return this.localDataSet.size(); }


}