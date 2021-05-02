package com.example.my_notes.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.my_notes.DatabaseAdapter;
import com.example.my_notes.Model.Reminder;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;

public class CalendarViewModel extends ViewModel implements DatabaseAdapter.vmInterface{

    private MutableLiveData<ArrayList<Reminder>> mReminders;
    private MutableLiveData<String> mToast;
    private DatabaseAdapter da;

    public CalendarViewModel(){
        this.mReminders = new MutableLiveData<>();
        this.mToast = new MutableLiveData<>();
        this.da = new DatabaseAdapter(this);

        //Apareixeran els reminders del dia d'avui
        String today = new CalendarDay().toString();
        this.da.getCollectionReminderByUserAndDay(today);

    }

    public LiveData<ArrayList<Reminder>> getReminders(){
        return this.mReminders;
    }


    public LiveData<String> getToast(){
        return this.mToast;
    }


    public Reminder getReminder(int pos) { return this.mReminders.getValue().get(pos);}


    public void addReminder(String title, String description, String alert, String date, String id, String owner){
        Reminder reminder = new Reminder(title, description, alert, date);

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


    public void remindersDaySelected(String date){
        this.da.getCollectionReminderByUserAndDay(date);
    }


    @Override
    public void setCollection(ArrayList list){
        mReminders.setValue(list);
    }


    @Override
    public void setToast(String s){
        mToast.setValue(s);
    }
}