package com.example.my_notes.ui.calendar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.example.my_notes.Notify.AlarmReceiver;
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


public class CalendarFragment extends Fragment{

    //Constantes
    private static final int REQUEST_PLACE_PICKER = 1001;
    private final String FORM_TITLE = "Add new reminder";
    private final String ACCEPT_BUTTON = "Accept";
    private final String CANCEL_BUTTON = "Cancel";
    private final String TAG = "CALENDARFRAGMENT";
    private final String TITLE_KEY = "REMINDER_TITLE";
    private final String CONTENT_KEY = "REMINDER_CONTENT";
    private final String ID_KEY = "ALARM_ID";

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


    //Variables del reminder
    private String title = null, description = null, date;
    private String adress = null, country = null, locality = null, countrycode = null;
    private Double longitude = null;
    private Double latitude = null;
    private final String EMPTY_INPUT = "Text area is empty";
    private int ID = 0;
    private Long timeInMillis = null;
    private AlarmManager alarmManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        this.alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        //Emmagatzem el context del pare
        this.parentContext = root.getContext();


        //Ajustem la recyclerView
        this.reminder_list = (RecyclerView) root.findViewById(R.id.cRecyclerView);
        this.reminder_list.setLayoutManager(new LinearLayoutManager(parentContext));


        //Ajustem el calendari i afegim el event
        this.calendar = (MaterialCalendarView) root.findViewById(R.id.calendar);
        CalendarDay today = new CalendarDay();
        this.calendar.setSelectedDate(today);
        this.calendarViewModel.remindersDaySelected(today.toString());

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

                tit.setTitle(FORM_TITLE);
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

                Calendar mCalendar = Calendar.getInstance();
                alarm.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        Calendar mCalendar = Calendar.getInstance();
                        TimePickerDialog timePickerDialog =
                                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        //mCalendar.set(Calendar.DAY_OF_YEAR, calendar.getSelectedDate().getDay() - new CalendarDay().getDay());

                                        Log.d(TAG, calendar.getSelectedDate().getDay() + "");
                                        Log.d(TAG, new CalendarDay().getDay() + "");

                                        mCalendar.set(Calendar.YEAR, calendar.getSelectedDate().getYear());
                                        mCalendar.set(Calendar.MONTH, calendar.getSelectedDate().getMonth());
                                        mCalendar.set(Calendar.DAY_OF_MONTH, calendar.getSelectedDate().getDay());
                                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay /*+ (calendar.getSelectedDate().getDay() - new CalendarDay().getDay()) * 24*/);
                                        mCalendar.set(Calendar.MINUTE, minute);
                                        mCalendar.set(Calendar.SECOND, 0);

                                        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mCalendar.getTime());
                                        dateReminder.setText(time);
                                        Log.d("MainActivity", "Selected time is " + time);
                                        timeInMillis = mCalendar.getTimeInMillis();
                                    }
                                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                });

                tit.setPositiveButton(ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = nomReminder.getText().toString();
                        description = descriptionRem.getText().toString();
                        adress = location.getText().toString();

                        //En el cas que hi hagi un titol i un contignut
                        if (title != null && description != null) {
                            String day = calendar.getSelectedDate().toString();

                            if (longitude == null || latitude == null || adress.equals("")) {
                                Toast.makeText(getContext(), "Saving reminder with no location", Toast.LENGTH_SHORT).show();

                            }

                            PendingIntent pendingIntent = null;
                            Intent intent = null;

                            //En cas que hi hagi seleccionada una hora configurarà l'alarma
                            if(timeInMillis != null) {

                                //Guardem el titol,d escripcio i id al intent
                                intent = new Intent(requireContext(), AlarmReceiver.class);
                                intent.putExtra(TITLE_KEY, title);
                                intent.putExtra(CONTENT_KEY, description);

                                pendingIntent = PendingIntent.getBroadcast(requireContext(), ID , intent, 0);

                                //Incremerntem el id per evitar solapaments.
                                ID++;

                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

                                timeInMillis = null;
                            }

                            calendarViewModel.addReminder(title, description, dateReminder.getText().toString(),
                                    day, longitude, latitude, country, locality, countrycode, intent, pendingIntent);
                        }
                    }
                })
                .setNegativeButton(CANCEL_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                tit.show();
            }
        });

        createNotificationChannel();
        setLiveDataObservers();
        this.ID = this.setID(); // AJustem l'id per evitar solapaments amb altres recordatoris
        return root;
    }


    /**
     * Aquesta funció s'encarrega d'ajustar els observers, els quals controlen si el contingut de
     * la recyclerView i s'encarrega de canviar l'adaptador
     */
    public void setLiveDataObservers() {

        final Observer<ArrayList<Reminder>> observer = new Observer<ArrayList<Reminder>>() {

            @Override
            public void onChanged(ArrayList<Reminder> reminders) {
                RemindersAdapter newAdapter = new RemindersAdapter(parentContext, reminders, getActivity(), alarmManager, ID, calendar);
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


    /**
     * Aquesta funció s'encarrega de crear un canal de notificació de cara a que l'usuari pugi
     * rebre les notificacions.
     */
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Prueba numero 1";
            String description = "Canal de prueba";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel  channel = new NotificationChannel("notifyChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
                    break;
                default:
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

    /**
     * Aquesta funció servirà per ajustar l'id dels recordatoris per evitar solapaments
     * @return Valor del id
     */
    private int setID(){
        return this.calendarViewModel.getNumberOfReminders();
    }
}