package com.example.my_notes.RecyclerView_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;

import java.util.ArrayList;

import Notes.NoteFolder;

/**
 * Aquesta clase s'utilitzarà per adaptar la visualització en el Recycelr View de les carpetes.
 */
public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.ViewHolder>{

    //Aqui tenim l'ArrayList de portafolis.
    private ArrayList<NoteFolder> localDataSet;

    /**
     * Clase viewHolder interna
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView folder_title;


        /**
         * Constructor de la clase. Rep una View per poder cridar al super constructor i despres
         * assignarem el TextView folder_title al TextView folders_title, a la layout foldercard.xml
         * @param view
         */
        public ViewHolder(View view){
            super(view);
            this.folder_title = (TextView)view.findViewById(R.id.folder_title);
        }

        /**
         * Ens retorna el camp de text
         * @return TextView con el titulo del protafolios
         */
        public TextView getTextView(){ return this.folder_title; }
    }

    /**
     * Constructor de la clase FOlders Adapter. Rep com a parametres un ArrayList de carpetes i
     * l'assigna a la variable localDataSet.
     * @param folders arraylist amb les carpetes
     */
    public FoldersAdapter(ArrayList<NoteFolder> folders){
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

        //Coje el elemento dels aaray localDataSer en position i reemplaza el contenido de la view
        viewHolder.getTextView().setText(localDataSet.get(position).get_Title());

    }


    /**
     * Aquesta funció s'encarregarà de retornar el tamany de la llista de portafolis
     * @return tamany de localDataSet
     */
    @Override
    public int getItemCount(){ return this.localDataSet.size(); }
}
