package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        //Read prefrences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        //Set prikbord timer
        int PRIKBORD_UPDATE_TIME = Integer.parseInt(sharedPref.getString("prikbord_refreshtime", Integer.toString(60 * 120))) * 1000;

        //Start timers if OS didn't do that already
        AlarmManagerBroadcastReceiver ambr = new AlarmManagerBroadcastReceiver();

        //Only start prikbord timer if the timeout is larger then 60 seconds (safety)!
        if(PRIKBORD_UPDATE_TIME >= 1000*60) {
            if (!ambr.checkTimerExists(context)) {
                ambr.createTimer(context, PRIKBORD_UPDATE_TIME, ambr.PRIKBORD);
            } else {
                ambr.updateAlaram(context, PRIKBORD_UPDATE_TIME, ambr.PRIKBORD);
            }
        }
    }

    public void cancelAlarms(Context context) {
        AlarmManagerBroadcastReceiver ambr = new AlarmManagerBroadcastReceiver();
        ambr.cancelAlarms(context);
    }
}
