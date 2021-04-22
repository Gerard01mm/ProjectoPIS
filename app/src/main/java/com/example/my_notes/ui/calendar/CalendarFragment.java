package com.example.my_notes.ui.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_notes.RecyclerView_adapters.RemindersAdapter;
import com.example.my_notes.Reminders.Reminder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Date;


public class CalendarFragment extends Fragment {

    //Constantes
    private final String FORM_TITLE = "Add new reminder";
    private final String ACCEPT_BUTTON = "Accept";
    private final String CANCEL_BUTTON = "Cancel";


    //Variables
    private MaterialCalendarView calendar;
    private ExtendedFloatingActionButton add_reminder;
    private CalendarViewModel calendarViewModel;
    private Context parentContext;
    private RecyclerView reminder_list;

    //Variables delr eminder
    private String title = null, description = null, date;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Emmagatzem el context del pare
        this.parentContext = root.getContext();

        //Ajustem la recyclerView
        this.reminder_list = (RecyclerView)root.findViewById(R.id.cRecyclerView);
        this.reminder_list.setLayoutManager(new LinearLayoutManager(parentContext));

        //Ajustem el calendari i afegim el event
        this.calendar = (MaterialCalendarView)root.findViewById(R.id.calendar);
        this.calendar.setSelectedDate(new CalendarDay());
        this.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String day = calendar.getSelectedDate().toString();

                //Cridarem a la funció que refrescarà i mostrara el contingut dels reminders del dia
                //Seleccionat
                calendarViewModel.remindersDaySelected(day);
            }
        });

        this.add_reminder = (ExtendedFloatingActionButton)root.findViewById(R.id.fab_add_reminder);
        this.add_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder tit = new AlertDialog.Builder(getActivity());
                tit.setTitle("Write a title for the reminder:");

                final EditText input = new EditText(getActivity());
                tit.setView(input);

                tit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = input.getText().toString();
                        System.out.println(title);
                        description = "Primera reminder";
                        if(title != null && description != null){
                            String day = calendar.getSelectedDate().toString();

                            //Reminder nR = new Reminder(title, description, day);
                            calendarViewModel.addReminder(title, description, "", day, "", "");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                tit.show();
            }
        });

        setLiveDataObservers();
        return root;
    }


    public void setLiveDataObservers() {
        this.calendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);

        final Observer<ArrayList<Reminder>> observer = new Observer<ArrayList<Reminder>>(){

            @Override
            public void onChanged(ArrayList<Reminder> reminders) {
                RemindersAdapter newAdapter = new RemindersAdapter(parentContext, reminders);
                reminder_list.swapAdapter(newAdapter, false);
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer <String> observerToast =  new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(parentContext, s, Toast.LENGTH_SHORT).show();
            }
        };

        calendarViewModel.getReminders().observe(getViewLifecycleOwner(), observer);
        calendarViewModel.getToast().observe(getViewLifecycleOwner(), observerToast);
    }
}