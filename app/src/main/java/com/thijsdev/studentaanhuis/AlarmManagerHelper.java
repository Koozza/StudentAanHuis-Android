package com.thijsdev.studentaanhuis;

import android.content.Context;

public class AlarmManagerHelper {
    private static AlarmManagerHelper instance = null;

    public AlarmManagerHelper() {
    }
    public static AlarmManagerHelper getInstance() {
        if(instance == null) {
            instance = new AlarmManagerHelper();
        }
        return instance;
    }

    public void startAlarms(Context context) {
        //Start timers if OS didn't do that already
        AlarmManagerBroadcastReceiver ambr = new AlarmManagerBroadcastReceiver();
        if(!ambr.checkTimerExists(context)) {
            ambr.createTimer(context, ambr.PRIKBORD_UPDATE_TIME, ambr.PRIKBORD);
        }else{
            ambr.updateAlaram(context, ambr.PRIKBORD_UPDATE_TIME, ambr.PRIKBORD);
        }
    }

    public void cancelAlarms(Context context) {
        AlarmManagerBroadcastReceiver ambr = new AlarmManagerBroadcastReceiver();
        ambr.cancelAlarms(context);
    }
}
