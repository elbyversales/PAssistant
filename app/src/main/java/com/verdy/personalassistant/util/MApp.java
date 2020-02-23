package com.verdy.personalassistant.util;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.verdy.personalassistant.receiver.ReviewNotifierReceiver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MApp extends Application {
    public static volatile Context mAppContext = null;
    public static volatile File logFile;
    public static final int DAILY_REVIEWS_ALARM = 101;
    public static final int REMINDER_ALARM = 102;
    public static final int NOTIFICATION_ID = 123456;

    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    @Override
    public void onCreate(){
        super.onCreate();
        MApp.mAppContext = this.getApplicationContext();
        setupLogWriter();
        checkAlarms();
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                AuxLogWriter.writeToLog(Log.getStackTraceString(paramThrowable));
                defaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
            }
        });
    }

    private void checkAlarms(){
        Intent intent = new Intent(mAppContext, ReviewNotifierReceiver.class);
        intent.setAction(Integer.toString(DAILY_REVIEWS_ALARM));
        boolean noAlarmSet = PendingIntent.getBroadcast(mAppContext, DAILY_REVIEWS_ALARM, intent, PendingIntent.FLAG_NO_CREATE) == null;
        if(noAlarmSet){
            AlarmRegister.setDailyReviewsServiceAlarm();
        }
    }

    private void setupLogWriter(){
        if (isExternalStorageReadable() ) {
            createLogDir();
        }
    }

    private void createLogDir(){
        File appDirectory = new File( Environment.getExternalStorageDirectory() + "/PAssistant/" );
        File logDirectory = new File( appDirectory + "/logs" );
        if ( !appDirectory.exists() ) {
            appDirectory.mkdir();
        }
        if ( !logDirectory.exists() ) {
            logDirectory.mkdir();
        }
        logFile = new File( logDirectory, "logger_" + new SimpleDateFormat("dd_MM_yyyy").format(new Date(System.currentTimeMillis())) + ".txt" );
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals( state ) ;
    }

}
