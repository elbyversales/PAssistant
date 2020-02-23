package com.verdy.personalassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.verdy.personalassistant.service.ReviewNotifierService;
import com.verdy.personalassistant.util.AlarmRegister;
import com.verdy.personalassistant.util.AuxLogWriter;
import com.verdy.personalassistant.util.MApp;

public class ReviewNotifierReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AuxLogWriter.writeToLog(ReviewNotifierReceiver.class + "alarm fired, notification receiver received. Calling notification service");
        ReviewNotifierService.enqueueWork(context, intent);
        AlarmRegister.setDailyReviewsServiceAlarm();
    }
}