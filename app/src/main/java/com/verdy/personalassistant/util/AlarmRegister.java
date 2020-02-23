package com.verdy.personalassistant.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.verdy.personalassistant.receiver.ReviewNotifierReceiver;

import java.util.Calendar;

public class AlarmRegister {
    private static Calendar calendar = Calendar.getInstance();

    public static void setDailyReviewsServiceAlarm(){
        calendar = calendar == null? Calendar.getInstance() : calendar;
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Context context = MApp.mAppContext;
        Intent reminderIntent =  new Intent(context, ReviewNotifierReceiver.class);
        reminderIntent.setAction(Integer.toString(MApp.DAILY_REVIEWS_ALARM));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MApp.DAILY_REVIEWS_ALARM, reminderIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MApp.mAppContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        AuxLogWriter.writeToLog(AlarmRegister.class + " alarm set when:" + FormatterUtility.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        calendar = null;
    }

    public static void setReminderAlarm(final int hour, final int minute){
        calendar = calendar == null? Calendar.getInstance(): calendar;
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Context context = MApp.mAppContext;
        Intent reminderIntent = new Intent(context, ReviewNotifierReceiver.class);
        reminderIntent.setAction(Integer.toString(MApp.REMINDER_ALARM));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MApp.REMINDER_ALARM, reminderIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) MApp.mAppContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        calendar = null;
    }

}
