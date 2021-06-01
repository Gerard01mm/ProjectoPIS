package com.example.my_notes.RecyclerView_adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.my_notes.Notify.AlarmReceiver;
import com.example.my_notes.R;
import com.example.my_notes.Model.Reminder;
import com.google.android.material.textfield.TextInputEditText;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder>{

    //Constants
    private final String TITLE_KEY = "REMINDER_TITLE";
    private final String CONTENT_KEY = "REMINDER_CONTENT";

    //Atributs
    private final ArrayList<Reminder> localDataSet;
    private final Context parentContext;
    private final Activity parentActivity;
    private AlarmManager alarmManager;
    private Long timeInMillis = null;
    private int ID;
    private MaterialCalendarView calendar;

    private String icon1 = null, icon2 = null, icon3 = null, desc1 = null, desc2 = null, desc3 = null;

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
    public RemindersAdapter(Context current, ArrayList<Reminder> reminders, Activity act, AlarmManager alarmManager, int ID, MaterialCalendarView calendar){
        this.parentContext = current;
        this.localDataSet = reminders;
        this.parentActivity = act;
        this.alarmManager = alarmManager;
        this.ID = ID;
        this.calendar = calendar;
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
                //Obrim un altre finestra amb el contingut del cardView
                Reminder selected = localDataSet.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                LayoutInflater inflater = parentActivity.getLayoutInflater();
                View vista  = inflater.inflate(R.layout.reminder_content, null);
                AlertDialog content = builder.create();
                content.setView(vista);

                if (selected.getLongitude() == null && selected.getLatitude() == null){
                    LinearLayout linearLayout = (LinearLayout) vista.findViewById(R.id.allLocation);
                    linearLayout.setVisibility(View.INVISIBLE);
                }
                else{
                    getClimaData(selected.getLatitude(), selected.getLongitude(), selected.getCountrycode(), vista);
                    TextView loc = (TextView) vista.findViewById(R.id.textViewInfoLoc);
                    String s = null;
                    DecimalFormat numberFormat = new DecimalFormat("#.000");

                    if (selected.getLocality() == null){
                        s = "\n" + selected.getCountry() + "\nLatitude: " + numberFormat.format(selected.getLatitude().doubleValue()) +
                            "\nLongitude: " + numberFormat.format(selected.getLongitude().doubleValue()) + "\n";
                    }

                    else{
                        s = "\n" + selected.getLocality() + ", " + selected.getCountry() +
                            "\nLatitude: " + numberFormat.format(selected.getLatitude().doubleValue()) +
                            "\nLongitude: " + numberFormat.format(selected.getLongitude().doubleValue()) + "\n";
                    }
                    loc.setText(s);
                }


                TextInputEditText title = (TextInputEditText)vista.findViewById(R.id.reminder_title_content);
                title.setText(selected.getTitle());

                TextInputEditText description = (TextInputEditText) vista.findViewById(R.id.reminder_descp_content);
                description.setText(selected.getDescription());

                TextView alert = (TextView)vista.findViewById(R.id.alertTextView);
                alert.setText(selected.getReminder_alert());

                ImageView delete = (ImageView) vista.findViewById(R.id.reminder_delete_button);

                //Ajustem el botó d'eliminar
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Eliminem l'alarma en cas que estigués configurada
                        if (selected.getAlarmPendingIntent() != null) {
                            alarmManager.cancel(selected.getAlarmPendingIntent());
                        }
                        selected.removeReminder();
                        localDataSet.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, localDataSet.size());
                        content.dismiss();
                    }
                });

                //Configuració del botó de guardar
                ImageView save = (ImageView)vista.findViewById(R.id.reminder_save_button);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected.setTitle(title.getText().toString());
                        selected.setDescription(description.getText().toString());
                        selected.setReminder_alert(alert.getText().toString());

                        //Si no hem escollit una hora, no es configurarà l'alarma
                        if (timeInMillis != null){

                            //En cas que tinguessim ja un alarma configurada
                            if (selected.getAlarmPendingIntent() != null){
                                //Cancelem l'antiga alarma
                                alarmManager.cancel(selected.getAlarmPendingIntent());
                            }

                            //Configurem l'alarma
                            Intent intent = new Intent(parentContext, AlarmReceiver.class);
                            intent.putExtra(TITLE_KEY, selected.getTitle());
                            intent.putExtra(CONTENT_KEY, selected.getDescription());
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(parentContext, ID, intent, 0);
                            ID++;

                            selected.setAlarmIntent(intent);
                            selected.setAlarmPendingIntent(pendingIntent);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                        }

                        selected.updateReminder();
                        notifyDataSetChanged();
                        content.dismiss();
                    }
                });

                //Botó per ajustar l'horari de la notificació
                ImageView setAlert = (ImageView)vista.findViewById(R.id.change_alarm_button);
                setAlert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mCalendar = Calendar.getInstance();
                        TimePickerDialog timePickerDialog =
                                new TimePickerDialog(parentActivity, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                        //Añadir año
                                        //mCalendar.set(Calendar.DAY_OF_YEAR, calendar.getSelectedDate().getDay());
                                        mCalendar.set(Calendar.YEAR, calendar.getSelectedDate().getYear());
                                        mCalendar.set(Calendar.MONTH, calendar.getSelectedDate().getMonth());
                                        mCalendar.set(Calendar.DAY_OF_MONTH, calendar.getSelectedDate().getDay());
                                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay /*+ (calendar.getSelectedDate().getDay() - new CalendarDay().getDay()) * 24*/);
                                        mCalendar.set(Calendar.MINUTE, minute);
                                        mCalendar.set(Calendar.SECOND, 0);
                                        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mCalendar.getTime());
                                        alert.setText(time);

                                        //Ajustem l'horari de la notificació en milisegons
                                        timeInMillis = mCalendar.getTimeInMillis();
                                        Log.d("MainActivity", "Selected time is " + time);
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

    public void getClimaData(Double lat, Double lon, String countrycode, View vista) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url("https://weatherbit-v1-mashape.p.rapidapi.com/forecast/daily?lat=" + lat + "&lon=" + lon)
            .get()
            .addHeader("x-rapidapi-key", "9ccdce2b87mshfb1a0d8ad72a9edp1ceab9jsne75320017c29")
            .addHeader("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
            .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(parentContext, "Error accessing Weather API", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()){
                    String newresponse = response.body().string();
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = new JSONObject(newresponse);

                                TextView textView1 = (TextView) vista.findViewById(R.id.infodia1);
                                TextView textViewDia1 = (TextView) vista.findViewById(R.id.dia1);
                                icon1 = jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("weather").getString("icon");

                                Glide.with(parentContext)
                                        .load("https://www.weatherbit.io/static/img/icons/" + icon1 + ".png")
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into((ImageView) vista.findViewById(R.id.icon1));

                                String infoDia1 = jsonObject.getJSONArray("data").getJSONObject(0).getString("temp") + " ºC"
                                        + "\nMax: " + jsonObject.getJSONArray("data").getJSONObject(0).getString("max_temp") + " ºC"
                                        + "\nMin: " + jsonObject.getJSONArray("data").getJSONObject(0).getString("min_temp") + " ºC\n";
                                textView1.setText(infoDia1);

                                String p1 = jsonObject.getJSONArray("data").getJSONObject(0).getString("datetime")
                                        + "\n" + jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("weather").getString("description");
                                textViewDia1.setText(p1);

                                TextView textView2 = (TextView) vista.findViewById(R.id.infodia2);
                                TextView textViewDia2 = (TextView) vista.findViewById(R.id.dia2);
                                icon2 = jsonObject.getJSONArray("data").getJSONObject(1).getJSONObject("weather").getString("icon");

                                Glide.with(parentContext)
                                        .load("https://www.weatherbit.io/static/img/icons/" + icon2 + ".png")
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into((ImageView) vista.findViewById(R.id.icon2));

                                String infoDia2 = jsonObject.getJSONArray("data").getJSONObject(1).getString("temp") + " ºC"
                                        + "\nMax: " + jsonObject.getJSONArray("data").getJSONObject(1).getString("max_temp") + " ºC"
                                        + "\nMin: " + jsonObject.getJSONArray("data").getJSONObject(1).getString("min_temp") + " ºC\n";
                                textView2.setText(infoDia2);

                                String p2 = jsonObject.getJSONArray("data").getJSONObject(1).getString("datetime")
                                        + "\n" +jsonObject.getJSONArray("data").getJSONObject(1).getJSONObject("weather").getString("description");

                                textViewDia2.setText(p2);

                                TextView textView3 = (TextView) vista.findViewById(R.id.infodia3);
                                TextView textViewDia3 = (TextView) vista.findViewById(R.id.dia3);
                                icon3 = jsonObject.getJSONArray("data").getJSONObject(2).getJSONObject("weather").getString("icon");
                                Glide.with(parentContext)
                                        .load("https://www.weatherbit.io/static/img/icons/" + icon3 + ".png")
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into((ImageView) vista.findViewById(R.id.icon3));

                                String infoDia3 = jsonObject.getJSONArray("data").getJSONObject(2).getString("temp") + " ºC"
                                        + "\nMax: " + jsonObject.getJSONArray("data").getJSONObject(2).getString("max_temp") + " ºC"
                                        + "\nMin: " + jsonObject.getJSONArray("data").getJSONObject(2).getString("min_temp") + " ºC\n";
                                textView3.setText(infoDia3);

                                String p3 = jsonObject.getJSONArray("data").getJSONObject(2).getString("datetime")
                                        + "\n" +jsonObject.getJSONArray("data").getJSONObject(2).getJSONObject("weather").getString("description");

                                textViewDia3.setText(p3);

                                Glide.with(parentContext)
                                        .load("https://www.countryflags.io/" + countrycode + "/flat/64.png")
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into((ImageView) vista.findViewById(R.id.flag));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        /*
        Request request2 = new Request.Builder()
                .url("https://www.weatherbit.io/static/img/icons/" + icon[0] + ".png")
                .get()
                .addHeader("x-rapidapi-key", "9ccdce2b87mshfb1a0d8ad72a9edp1ceab9jsne75320017c29")
                .addHeader("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
                .build();


        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(parentContext, "Error accessing Weather API", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()){
                    System.out.println("entra");
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    // Remember to set the bitmap in the main thread.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("entra en el bitmap");
                            ImageView clima = (ImageView) vista.findViewById(R.id.imageViewClima);
                            clima.setImageBitmap(bitmap);
                        }
                    });
                }else {
                    System.out.println(" no entraen nitmap");
                    System.out.println(response.code());
                }
            }
        });
        */
    }
}
