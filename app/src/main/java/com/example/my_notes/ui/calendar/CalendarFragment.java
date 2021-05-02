package com.example.my_notes.ui.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.my_notes.RecyclerView_adapters.RemindersAdapter;
import com.example.my_notes.Model.Reminder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


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
    //Dialog
    private ImageButton alarm;
    private TextInputEditText nomReminder;
    private TextInputEditText descriptionRem;
    private TextView dateReminder;
    private TextView dateCard;

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
                Log.d("DIA SELECCIONAT", "EL DIA PULSAT ÉS EL" + day);

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
                LayoutInflater inflater = requireActivity().getLayoutInflater();

                View dialog = inflater.inflate(R.layout.reminder_form, null);
                alarm = (ImageButton)dialog.findViewById(R.id.add_alarm_button);
                nomReminder = (TextInputEditText) dialog.findViewById(R.id.dialog_title);
                descriptionRem = (TextInputEditText) dialog.findViewById(R.id.dialog_description);
                dateReminder = (TextView) dialog.findViewById(R.id.horaSelected);

                tit.setTitle("Write a title for the reminder:");
                tit.setView(dialog);


                alarm.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        Calendar mCalendar = Calendar.getInstance();
                        TimePickerDialog timePickerDialog =
                                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        mCalendar.set(Calendar.MINUTE, minute);
                                        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mCalendar.getTime());
                                        dateReminder.setText(time);
                                        Log.d("MainActivity", "Selected time is " + time);
                                    }
                                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                });

                tit.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = nomReminder.getText().toString();
                        description = descriptionRem.getText().toString();
                        if(title != null && description != null){
                            String day = calendar.getSelectedDate().toString();
                            calendarViewModel.addReminder(title, description, dateReminder.getText().toString(), day);
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

        final Observer<ArrayList<Reminder>> observer = new Observer<ArrayList<Reminder>>(){

            @Override
            public void onChanged(ArrayList<Reminder> reminders) {
                RemindersAdapter newAdapter = new RemindersAdapter(parentContext, reminders, getActivity());
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