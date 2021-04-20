package com.example.my_notes.ui.calendar;

import android.app.AlertDialog;
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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.my_notes.R;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


public class CalendarFragment extends Fragment {

    //Constantes
    private final String FORM_TITLE = "Add new reminder";
    private final String ACCEPT_BUTTON = "Accept";
    private final String CANCEL_BUTTON = "Cancel";


    //Variables
    private MaterialCalendarView calendar;
    private ExtendedFloatingActionButton add_reminder;


    private CalendarViewModel calendarViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });


        this.calendar = (MaterialCalendarView)root.findViewById(R.id.calendar);
        this.add_reminder = (ExtendedFloatingActionButton)root.findViewById(R.id.fab_add_reminder);

        this.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //Hay que implementar la  logica para que aparezcan los diferentes avisos
                Toast.makeText(getContext(), "Dia seleccionado: " + date.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        this.add_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Boton pulsado", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder form = new AlertDialog.Builder(getActivity());
                form.setTitle(FORM_TITLE);

                //Afegim un camp per al titol i una etiqueta
                final TextView tit = new TextView(getActivity());
                form.setView(tit);

                final EditText titleArea = new EditText(getActivity());
                form.setView(titleArea);

                //Afegim un camp per escriure una descripció
                final EditText descpArea = new EditText(getActivity());
                form.setView(descpArea);

                //Afegim un boto per a acceptar
                form.setPositiveButton(ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleArea.getText().toString();
                        Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                    }
                });

                //Afegim un boto per a cancelar
                form.setNegativeButton(CANCEL_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                //Mostrem el recuadre
                form.show();
            }
        });

        return root;
    }
}