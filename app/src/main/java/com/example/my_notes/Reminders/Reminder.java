package com.example.my_notes.Reminders;

import java.util.Date;
import java.util.UUID;

public class Reminder {


    //Atributs
    private String title, description, id, owner, date;
    private Date reminder_alert;


    public Reminder(String title, String owner, Date date){
        this.title = title;
        this.description = "";
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.reminder_alert = null;
    }


    public Reminder(String title, String description, String owner, Date date){
        this.title = title;
        this.description = description;
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.reminder_alert = null;
    }


    public Reminder(String title, String owner, Date alert, Date date){
        this.title = title;
        this.description = "";
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner =owner;
        this.reminder_alert = alert;
    }


    public Reminder(String title, String description, String owner, Date alert, Date date){
        this.title = title;
        this.description = description;
        this.date = date.toString();
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.reminder_alert =alert;
    }


    public String getTitle(){ return this.title; }

    public void setTitle(String title){ this.title = title; }

}
