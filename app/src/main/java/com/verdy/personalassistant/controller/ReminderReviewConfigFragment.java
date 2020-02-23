package com.verdy.personalassistant.controller;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.verdy.personalassistant.R;
import com.verdy.personalassistant.util.AlarmRegister;
import com.verdy.personalassistant.util.MApp;

public class ReminderReviewConfigFragment extends AppCompatActivity implements View.OnClickListener{
    private TimePicker timePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_date_picker);
        LinearLayout layout = findViewById(R.id.custom_date_picker);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        timePicker = (TimePicker) layout.getChildAt(1);
        layout.getChildAt(2).setOnClickListener(this);
        cacelNotification();
    }

    private void cacelNotification(){
        Intent intent = this.getIntent();
        if(intent != null && intent.getIntExtra("NOTIFICATION_ID", 0) == MApp.NOTIFICATION_ID){
            final int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationId);
        }
    }

    @Override
    public void onClick(View v) {
        AlarmRegister.setReminderAlarm(timePicker.getHour(), timePicker.getMinute());
        Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
