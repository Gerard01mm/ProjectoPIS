package com.example.my_notes.RecyclerView_adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;
import com.example.my_notes.Model.Reminder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder>{

    private final ArrayList<Reminder> localDataSet;
    private final Context parentContext;
    private final Activity parentActivity;

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
    public RemindersAdapter(Context current, ArrayList<Reminder> reminders, Activity act){
        this.parentContext = current;
        this.localDataSet = reminders;
        this.parentActivity = act;
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
                Reminder selected = localDataSet.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                LayoutInflater inflater = parentActivity.getLayoutInflater();
                View vista  = inflater.inflate(R.layout.reminder_content, null);
                AlertDialog content = builder.create();
                content.setView(vista);

                TextInputEditText title = (TextInputEditText)vista.findViewById(R.id.reminder_title_content);
                title.setText(selected.getTitle());

                TextInputEditText description = (TextInputEditText) vista.findViewById(R.id.reminder_descp_content);
                description.setText(selected.getDescription());

                TextView alert = (TextView)vista.findViewById(R.id.alertTextView);
                alert.setText(selected.getReminder_alert());

                ImageView delete = (ImageView) vista.findViewById(R.id.reminder_delete_button);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected.removeReminder();
                        localDataSet.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, localDataSet.size());
                        content.dismiss();
                    }
                });


                ImageView save = (ImageView)vista.findViewById(R.id.reminder_save_button);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected.setTitle(title.getText().toString());
                        selected.setDescription(description.getText().toString());
                        selected.setReminder_alert(alert.getText().toString());
                        selected.updateReminder();
                        notifyDataSetChanged();
                        content.dismiss();
                    }
                });



                ImageView setAlert = (ImageView)vista.findViewById(R.id.change_alarm_button);
                setAlert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mCalendar = Calendar.getInstance();
                        TimePickerDialog timePickerDialog =
                                new TimePickerDialog(parentActivity, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        mCalendar.set(Calendar.MINUTE, minute);
                                        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mCalendar.getTime());
                                        alert.setText(time);
                                        //Log.d("MainActivity", "Selected time is " + time);
                                    }
                                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                });

                content.show();
            }
        });
    }


    /**
     * Aquesta funció retornarà el tamamny del localDataSet
     * @return tamany de les dades.
     */
    public int getItemCount(){ return this.localDataSet.size(); }

}
