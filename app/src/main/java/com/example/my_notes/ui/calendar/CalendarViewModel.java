package com.example.my_notes.ui.calendar;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;
import com.example.my_notes.Model.Reminder;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;

public class CalendarViewModel extends ViewModel implements DatabaseAdapter.vmInterface{

    //Atributs
    private MutableLiveData<ArrayList<Reminder>> mReminders;
    private MutableLiveData<String> mToast;
    private DatabaseAdapter da;


    /**
     * Constructor de la classe. Inicialitzem els mutableLiveData i inicialitzem l'adaptador de la
     * base de dades.
     */
    public CalendarViewModel(){
        this.mReminders = new MutableLiveData<>();
        this.mToast = new MutableLiveData<>();
        this.da = new DatabaseAdapter(this);
    }


    /**
     * Aquesta funció ens retornaà la llista mReminders, la qual és una MutableLiveData
     * @return llista de reminders
     */
    public LiveData<ArrayList<Reminder>> getReminders(){
        if (mReminders == null){
            mReminders = new MutableLiveData<>();
        }
        return mReminders;
    }


    /**
     * Aquesta funció s'encarregad e cridar a la funcio getNumberOfReminders del databse
     * per poder recuperar el id i evitar solapaments en les notificacions
     * @return numero de recordatoris d'un usuari
     */
    public int getNumberOfReminders(){
        return this.da.getNumberOfReminders();
    }


    /**
     * Aquesta funció ens retorna la llista de Toast
     * @return MutableLiveData de Toast.
     */
    public LiveData<String> getToast(){
        return this.mToast;
    }


    /**
     * Aquesta funció ens permet recuperar un element de la llista de reminders.
     *
     * Rep com a parametre una posició
     * @param pos posició que volem obtenir
     *
     * @return Element que ocupa la posició pos
     */
    public Reminder getReminder(int pos) { return this.mReminders.getValue().get(pos);}


    /**
     * La funció permet afegir una nova reminder a la base de dades.
     *
     * Rep com a parametres un titol, una descripció, una data d'alerta en forma de cadena i la data
     * a kla qual es fixarà el reminder (també en cadena).
     *
     * Instanciarà un reminder amb aquestes variables i aquesta s'afegirà a la base de dades.
     *
     * @param title titol del reminder
     * @param description Descripció del reminder
     * @param alert data quan saltarà el reminder
     * @param date data assignada al reminder
     */
    public void addReminder(String title, String description, String alert, String date,
                            Double longitude, Double latitude, String country, String locality,
                            String countrycode, Intent intent, PendingIntent pendingIntent){

        Reminder reminder = new Reminder(title, description, alert, date, longitude,
                latitude, country, locality, countrycode);

        //Ajustem els intents i pending intent al recordatori creat
        reminder.setAlarmIntent(intent);
        reminder.setAlarmPendingIntent(pendingIntent);

        if(mReminders.getValue() == null){
            ArrayList<Reminder> rem = new ArrayList<>();
            rem.add(reminder);
            mReminders.setValue(rem);
        }
        else{
            mReminders.getValue().add(reminder);
            mReminders.setValue(mReminders.getValue());
        }
        reminder.saveReminder();
    }


    /**
     * Aquesta funció rep una data en forma de cadena i servirà per a que en el moment de
     * seleccionar una data en el calendari, la base de dades carregui els reminders d'aquell dia.
     *
     * @param date data seleccionada al calendari
     */
    public void remindersDaySelected(String date){
        this.da.getCollectionReminderByUserAndDay(date);
    }


    @Override
    public void setCollection(ArrayList rem) {
        this.mReminders.setValue(rem);
    }


    @Override
    public void setToast(String s){ mToast.setValue(s); }
}