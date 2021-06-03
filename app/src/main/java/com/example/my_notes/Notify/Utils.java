package com.example.my_notes.Notify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.my_notes.Model.Reminder;

public class Utils {

    private final String TAG = "UTILS";

    public void setAlarm(int i, Long timestamp, Context ctx, String title, String content){
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        alarmIntent.putExtra("reminderTitle", title);
        alarmIntent.putExtra("reminderContent", content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);

    }
}
