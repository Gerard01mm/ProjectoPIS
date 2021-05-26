package com.example.my_notes.Notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.my_notes.R;

import java.util.concurrent.atomic.AtomicInteger;

public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = "ALARM RECEIVER";
    private final String TITLE_KEY = "REMINDER_TITLE";
    private final String CONTENT_KEY = "REMINDER_CONTENT";
    //private final String ID_KEY = "ALARM_ID";
    private static final AtomicInteger id_value = new AtomicInteger(0);

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = id_value.getAndIncrement();
        Log.d(TAG, "Valor del id: " + id);

        //Recuperem el titol i el contingut del recordatori
        String title = intent.getStringExtra(TITLE_KEY);
        String content = intent.getStringExtra(CONTENT_KEY);

        //Creem la notificació
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyChannel")
                .setSmallIcon(R.drawable.logo_small_icon_only_inverted) //Afegim l'icone
                .setContentTitle(title) //Agefeix el titol a la notificació
                .setContentText(content) //Afegeix el contingut de la notificació
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //Ajustem prioritat

        Log.d(TAG, title);
        Log.d(TAG, content);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(id, builder.build());

        Log.d(TAG, "S'ha rebut la alarma");
    }
}
