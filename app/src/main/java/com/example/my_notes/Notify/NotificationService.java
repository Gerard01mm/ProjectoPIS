  package com.example.my_notes.Notify;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.my_notes.MainActivity;
import com.example.my_notes.R;

public class NotificationService extends IntentService {

    //Constants
    private final String TAG = "NOTIFICATIONSERVICE";

    //Atributs
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    Notification notification;

    public NotificationService(){
        super("SERVICE");
    }


    public NotificationService(String name){
        super(name);
    }


    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getString(R.string.app_name);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, MainActivity.class);
        Resources res = this.getResources();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        String titleReminder = intent.getStringExtra("reminderTitle");
        String contentReminder = intent.getStringExtra("reminderContent");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            final int NOTIFY_ID = 0;
            String id = NOTIFICATION_CHANNEL_ID;
            String title = NOTIFICATION_CHANNEL_ID;
            PendingIntent pendingIntent;
            NotificationCompat.Builder builder;
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notifManager == null){
                notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = notifManager.getNotificationChannel(id);

            if (notificationChannel == null){
                notificationChannel = new NotificationChannel(id, title, importance);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(notificationChannel);
            }

            builder = new NotificationCompat.Builder(context, id);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(titleReminder).setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.logo_small_icon_only_inverted)
                    .setContentText(contentReminder)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo_small_icon_only_inverted))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);
            startForeground(1, notification);
        }
        else{
            pendingIntent = PendingIntent.getActivity(context, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.logo_small_icon_only_inverted)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo_small_icon_only_inverted))
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setContentTitle(getString(R.string.app_name)).setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText(contentReminder)
                    .setContentTitle(titleReminder).build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
