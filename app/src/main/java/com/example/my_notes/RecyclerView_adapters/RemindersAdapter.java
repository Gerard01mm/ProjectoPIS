package com.example.my_notes.RecyclerView_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;
import com.example.my_notes.Model.Reminder;

import java.util.ArrayList;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder>{

    private final ArrayList<Reminder> localDataSet;
    private final Context parentContext;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView reminder_title;
        private final LinearLayout fLayout;

        /**
         * Cosntructor de la classe interna ViewHolder. Rep un parametre que sera una view
         * @param view
         */
        public ViewHolder(View view){
            super(view);
            this.reminder_title = (TextView)view.findViewById(R.id.reminder_title);
            this.fLayout = (LinearLayout)view.findViewById(R.id.reminder_linear);
        }


        /**
         * Aquesta funció ens permetra recuperar el TextView del titol de la reminder_card
         * @return TextView amb titol del reminder
         */
        public TextView getReminder_title() { return this.reminder_title; }


        /**
         * Aquesta funció ens permetrà recupèrar el linear Layout de la reminder_card
         * @return Linear Layout de la cardView.
         */
        public LinearLayout getfLayout() { return this.fLayout; }
    }


    /**
     * Constructor de la classe. Rep un context i un arrayList de reminders.
     *
     * Assigna el parentCOntext al context que rebem i el localdataSet al arraylist de reminders.
     *
     * @param current Context
     * @param reminders Llistat de reminders.
     */
    public RemindersAdapter(Context current, ArrayList<Reminder> reminders){
        this.parentContext = current;
        this.localDataSet = reminders;
    }


    /**
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.reminder_card, viewGroup, false);

        return new ViewHolder(view);
    }


    /**
     * Aquesta funció s'encarregarà d'ajustar els components de la CardView amb el seu
     * @param viewHolder
     * @param position
     */
    public void onBindViewHolder(ViewHolder viewHolder, final int position){

        //Afegim al titol de la cardView el titol del reminder.
        viewHolder.getReminder_title().setText(localDataSet.get(position).getTitle());

        //Afegim un event de OnClick al LinearLayout
        LinearLayout reminder_layout = viewHolder.getfLayout();
        reminder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obrim un altre finestra amb el contuingut del cardView

                //Toast.makeText(v.getContext(), "Se ha detectado un pulso", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Aquesta funció retornarà el tamamny del localDataSet
     * @return tamany de les dades.
     */
    public int getItemCount(){ return this.localDataSet.size(); }

}
