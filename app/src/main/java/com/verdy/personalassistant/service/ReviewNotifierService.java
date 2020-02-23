package com.verdy.personalassistant.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.verdy.personalassistant.controller.MainActivity;
import com.verdy.personalassistant.controller.ReminderReviewConfigFragment;
import com.verdy.personalassistant.dao.ReviewDao;
import com.verdy.personalassistant.model.ReviewReminder;
import com.verdy.personalassistant.util.AuxLogWriter;
import com.verdy.personalassistant.util.FormatterUtility;
import com.verdy.personalassistant.util.MApp;

import java.util.ArrayList;
import java.util.Calendar;

public class ReviewNotifierService extends JobIntentService {
    private static String REMINDER_CHANNEL = "review_reminder";
    static final int JOB_ID = 1000;
    private int actionFlag;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ReviewNotifierService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        actionFlag = Integer.parseInt(intent.getAction()) ;
        switch (actionFlag){
            case MApp.DAILY_REVIEWS_ALARM:
                setDailyReviews();
                break;
            case MApp.REMINDER_ALARM:
                buildNotification();
                break;
        }
    }

    private void setDailyReviews(){
        AuxLogWriter.writeToLog(ReviewNotifierService.class + " setting daily reivews");
        Calendar calendar = Calendar.getInstance();
        String currentDate = FormatterUtility.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        final ArrayList<ReviewReminder> reviews = (ArrayList<ReviewReminder>) ReviewDao.getReviews(ReviewDao.ConditionFlags.REVIEWS_TO_REMINDER, 0, currentDate);
        if(reviews != null){
            ReviewDao.insertReminders(reviews);
            AuxLogWriter.writeToLog("reviews have been inserted. Calling notification builder...");
            buildNotification();
        }
    }

    public void buildNotification(){
        Intent intent = new Intent(MApp.mAppContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        String contentText = "There are some reviews for today";
        createMessagesNotificationChannel(this.getApplicationContext());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, REMINDER_CHANNEL)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Reviews reminder").setContentText(contentText)
                .setContentIntent(pendingIntent).setAutoCancel(true);
        if(actionFlag == MApp.DAILY_REVIEWS_ALARM){
            builder.addAction(setReminderAlarmAction());
        }
        notificationManager.notify(MApp.NOTIFICATION_ID, builder.build());
    }

    private NotificationCompat.Action setReminderAlarmAction(){
        Intent intent = new Intent(this, ReminderReviewConfigFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("NOTIFICATION_ID", MApp.NOTIFICATION_ID);
        PendingIntent setAlarm = PendingIntent.getActivity(this, MApp.REMINDER_ALARM, intent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Action.Builder(android.R.drawable.ic_popup_reminder,
                "SET REMINDER", setAlarm).build();
    }

    public void createMessagesNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(REMINDER_CHANNEL, "com.verdy.personalassistant.remindernotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
