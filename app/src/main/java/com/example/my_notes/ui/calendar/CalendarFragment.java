package com.example.my_notes.ui.calendar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.my_notes.GeoLocation;
import com.example.my_notes.LoginUserActivity;
import com.example.my_notes.MainActivity;
import com.example.my_notes.RecyclerView_adapters.RemindersAdapter;
import com.example.my_notes.Model.Reminder;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_notes.R;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class CalendarFragment extends Fragment {

    private static final int REQUEST_PLACE_PICKER = 1001;
    //Constantes
    private final String FORM_TITLE = "Add new reminder";
    private final String ACCEPT_BUTTON = "Accept";
    private final String CANCEL_BUTTON = "Cancel";

    private GoogleMap googleMap;

    //Variables
    private MaterialCalendarView calendar;
    private ExtendedFloatingActionButton add_reminder;
    private CalendarViewModel calendarViewModel;
    private Context parentContext;
    private RecyclerView reminder_list;
    //Dialog
    private ImageButton alarm;
    private Button checkAddress;
    private TextInputEditText location, nomReminder, descriptionRem;
    private TextView dateReminder, latAndLong;
    private SupportMapFragment mapFragment;
    private FragmentManager fragmentManager;

    //Variables delr eminder
    private String title = null;
    private String description = null;
    private String date;
    private String adress = null, country = null, locality = null, countrycode = null;
    private Double longitude = null;
    private Double latitude = null;
    private final String EMPTY_INPUT = "Text area is empty";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Emmagatzem el context del pare
        this.parentContext = root.getContext();


        //Ajustem la recyclerView
        this.reminder_list = (RecyclerView) root.findViewById(R.id.cRecyclerView);
        this.reminder_list.setLayoutManager(new LinearLayoutManager(parentContext));


        //Ajustem el calendari i afegim el event
        this.calendar = (MaterialCalendarView) root.findViewById(R.id.calendar);

        this.calendar.setSelectedDate(new CalendarDay());
        this.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String day = calendar.getSelectedDate().toString();
                Log.d("DIA SELECCIONAT", "EL DIA POLSAT ÉS EL" + day);

                //Cridarem a la funció que refrescarà i mostrara el contingut dels reminders del dia
                //Seleccionat
                calendarViewModel.remindersDaySelected(day);
            }
        });

        this.add_reminder = (ExtendedFloatingActionButton) root.findViewById(R.id.fab_add_reminder);
        this.add_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adress = null;
                country = null;
                locality = null;
                countrycode = null;
                longitude = null;
                latitude = null;

                AlertDialog.Builder tit = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = requireActivity().getLayoutInflater();

                View dialog = inflater.inflate(R.layout.reminder_form, null);

                alarm = (ImageButton) dialog.findViewById(R.id.add_alarm_button);
                nomReminder = (TextInputEditText) dialog.findViewById(R.id.dialog_title);
                descriptionRem = (TextInputEditText) dialog.findViewById(R.id.dialog_description);
                dateReminder = (TextView) dialog.findViewById(R.id.horaSelected);
                location = (TextInputEditText) dialog.findViewById(R.id.dialog_location);
                latAndLong = (TextView) dialog.findViewById(R.id.textViewLatLong);
                checkAddress = (Button) dialog.findViewById(R.id.checklocbutton);

                tit.setTitle("Write a title for the reminder:");
                tit.setView(dialog);

                checkAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adress = null;
                        country = null;
                        locality = null;
                        countrycode = null;
                        longitude = null;
                        latitude = null;
                        if (location.getText().toString().equals("")) {
                            location.setError(EMPTY_INPUT);
                        }
                        else {
                            adress = location.getText().toString();
                            GeoLocation.getAdress(adress, getContext(), new GeoHandler());

                        }
                    }
                });

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
                        adress = location.getText().toString();

                        if (title != null && description != null) {
                            String day = calendar.getSelectedDate().toString();

                            if (longitude == null || latitude == null) {
                                Toast.makeText(getContext(), "Saving reminder with no location", Toast.LENGTH_SHORT).show();
                            }

                            calendarViewModel.addReminder(title, description, dateReminder.getText().toString(),
                                    day, longitude, latitude, country, locality, countrycode);
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

        final Observer<ArrayList<Reminder>> observer = new Observer<ArrayList<Reminder>>() {

            @Override
            public void onChanged(ArrayList<Reminder> reminders) {
                RemindersAdapter newAdapter = new RemindersAdapter(parentContext, reminders, getActivity());
                reminder_list.swapAdapter(newAdapter, false);
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(parentContext, s, Toast.LENGTH_SHORT).show();
            }
        };

        calendarViewModel.getReminders().observe(getViewLifecycleOwner(), observer);
        calendarViewModel.getToast().observe(getViewLifecycleOwner(), observerToast);
    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    country = bundle.getString("country");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    locality = bundle.getString("locality");
                    countrycode = bundle.getString("countrycode");
                    System.out.println("NOT NULL");
                    break;
                default:
                    System.out.println("Null bundle");
            }
            latAndLong.setText(getString(R.string.complete_address, country, latitude, longitude));
            if (latitude == null || longitude == null){
                Toast.makeText(getContext(), "Null location", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("country", country);
                startActivity(intent);
            }
        }
    }

}