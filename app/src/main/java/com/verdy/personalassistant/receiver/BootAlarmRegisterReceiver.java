package com.verdy.personalassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.verdy.personalassistant.service.ReviewNotifierService;
import com.verdy.personalassistant.util.AlarmRegister;
import com.verdy.personalassistant.util.AuxLogWriter;
import com.verdy.personalassistant.util.MApp;

public class BootAlarmRegisterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            AuxLogWriter.writeToLog(BootAlarmRegisterReceiver.class + " action BOOT received. Calling notification service and alarm register");
            Intent service = new Intent(context, ReviewNotifierService.class);
            service.setAction(Integer.toString(MApp.DAILY_REVIEWS_ALARM));
            ReviewNotifierService.enqueueWork(context,service);
        }
    }
}
