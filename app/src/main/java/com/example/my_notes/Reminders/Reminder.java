package com.example.my_notes.Reminders;

import android.util.Log;

import com.example.my_notes.DatabaseAdapter;

import java.util.Date;
import java.util.UUID;

public class Reminder {


    //Atributs
    private String title, description, id, owner, date, reminder_alert;

    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;


    /**
     * Constructor de la classe 1.
     *
     * Rep com parametres el titol del Reminder, el owner i una data seleccionada.
     *
     * Donat que no rep una data d'avís, el reminder_alert queda a null, conforme no es rebrà cap
     * notificació del reminder.
     *
     * Donat que no es rep cap descripció, aquesta quedarà com una cadena buida.
     *
     * @param title titol del reminder
     * @param date data seleccionada per la data.
     */

    public Reminder(String title, Date date){
        this.title = title;
        this.description = "";
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner = adapter.getCurrentUser();
        this.reminder_alert = null;
    }


    /**
     * Constructor de la classe 2
     *
     * Rep com a parametre el titol del reminder, una descripció del reminder, el owner i una data
     *
     * Donat que no rep cap data d'avis, reminder alert s'ajustarà a null de maner auqe no es
     * rebrà cap notificació
     *
     * @param title Titol del reminder
     * @param description Descripció del reminder
     * @param date Data a la que s'assignara el reminder.
     */

    public Reminder(String title, String description, Date date){
        this.title = title;
        this.description = description;
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner = adapter.getCurrentUser();
        this.reminder_alert = null;
    }


    /**
     * Constructor de la classe 3
     *
     * Rep com a parametres el titol del reminder, el owner, Uuna data d'avis i una data a la qual
     * s'assigna.
     *
     * Donatr que no rep una descripció, aquesta s'assignarà com una cadena buida.
     *
     * @param title titol del reminder
     * @param alert Data en la que saltarà la notifiació.
     * @param date Data a la que s'assignarà el reminder.
     */

    public Reminder(String title, Date alert, Date date){
        this.title = title;
        this.description = "";
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner = adapter.getCurrentUser();
        this.reminder_alert = null;
    }


    /**
     * Constructor de la classe 4
     *
     * Rep com a parametres un titol del reminder, una descripció, el owner del reminder, una data
     * d'avis i un altre data per assignar el reminder.
     *
     * @param title titol del reminder
     * @param description Descripció del reminder
     * @param alert Data per saltar la notificació recordatoria
     * @param date dia al que s'assignarà el reminder.
     */
    public Reminder(String title, String description, String alert, String date, String id, String owner){
        this.title = title;
        this.description = description;
        this.date = date;
        this.id = id;
        this.owner = owner;
        this.reminder_alert = alert;
    }


    public Reminder(String title, String description, String alert, String date, String id){
        this.title = title;
        this.description = description;
        this.date = date;
        this.id = id;
        this.owner = adapter.getCurrentUser();
        this.reminder_alert = alert;
    }


    public Reminder(String title, String description, String alert, String date){
        this.title = title;
        this.description = description;
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.owner = adapter.getCurrentUser();
        this.reminder_alert = alert;
        System.out.println("REMINDER CREAT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }



    /**
     * Retorna el titol del reminder
     * @return titol del reminder
     */
    public String getTitle(){ return this.title; }


    /**
     * Ens permet canviar el titol del reminder
     * @param title nou titol per reminder
     */
    public void setTitle(String title){ this.title = title; }


    /**
     * Retorna la descripció del reminder
     * @return descripció del reminder
     */
    public String getDescription() { return this.description; }


    /**
     * Ens permet modificar la descripció del reminder
     * @param nD nova descripció.
     */
    public void setDescription(String nD) { this.description = nD; }


    /**
     * Retorna la data a la que esta assignada el reminder.
     * @return data que te assignada el reminder.
     */
    public String getDate() { return this.date; }


    /**
     * Ens permet modificar la data assignada al reminder.
     * @param nDate
     */
    public void setDate(Date nDate) { this.date = nDate.toString(); }


    /**
     * Retorna el id del reminder.
     * @return id del reminder.
     */
    public String getId() { return this.id; }


    /**
     * Ens permet modificar el id del reminder.
     */
    public void setId(){ this.id = UUID.randomUUID().toString(); }


    /**
     * Ens retorna el owner del reminder.
     * @return owner del reminder
     */
    public String getOwner() { return this.owner; }


    /**
     * Ens permet canviar el owner del reminder
     * @param nOwner nou Owner del reminder
     */
    public void setOwner(String nOwner) { this.owner = nOwner; }


    /**
     * Ens retorna la data d'avis del reminder
     * @return data d'avís
     */
    public String getReminder_alert() { return this.reminder_alert; }


    /**
     * Ens permet modificar la data d'avis.
     * @param date
     */
    public void setReminder_alert(Date date) { this.reminder_alert = date.toString(); }


    public void saveReminder(){
        Log.d("saveReminder", "adapter-> saveReminder");
        adapter.saveReminder(this.title, this.id, this.owner, this.date, this.description, this.reminder_alert);
    }


    public void removeReminder(){
        Log.d("removeReminder","adapter-> removeRemidner");
        adapter.deleteReminder(this.id);
    }



    public void updateReminder(){
        Log.d("updateReminder", "adapter-> updateFolder");
        adapter.updateReminder(this.title, this.id, this.owner, this.date, this.description, this.reminder_alert);
    }

}
