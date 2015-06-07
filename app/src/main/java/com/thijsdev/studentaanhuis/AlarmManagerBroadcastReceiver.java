package com.thijsdev.studentaanhuis;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.thijsdev.studentaanhuis.Prikbord.PrikbordHelper;
import com.thijsdev.studentaanhuis.Database.PrikbordItem;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    final public static String ALARM = "alarm";
    final public static String PRIKBORD = "prikbord";
    public static final int REQUEST_CODE = 131131;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //Get preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

            //Set prikbord timer
            int PRIKBORD_UPDATE_TIME = Integer.parseInt(sharedPref.getString("prikbord_refreshtime", Integer.toString(60 * 120))) * 1000;
            if(PRIKBORD_UPDATE_TIME >= 1000*60)
                createTimer(context, PRIKBORD_UPDATE_TIME, PRIKBORD);
        }else if ("com.thijsdev.studentaanhuis.TIMER_UPDATE".equals(intent.getAction())) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SAH ALARM MANAGER");

            Bundle extras = intent.getExtras();

            //Check of het om de prikbord timer gaat
            if (extras != null && extras.getString(ALARM) != null) {
                wl.acquire();

                //Restore session
                SessionHelper.registerCookieHandler();
                SessionHelper.restoreSession(context);

                //Prikbord update
                if(extras.getString(ALARM).equals(PRIKBORD)) {

                    PrikbordHelper prikbordHelper = new PrikbordHelper();
                    prikbordHelper.updatePrikbordItems(context, new Callback(), new Callback(), new Callback() {
                        @Override
                        public void onTaskCompleted(Object... results) {
                            if(((ArrayList<PrikbordItem>) results[0]).size() != 0)
                                generateNewPrikbordNotification(context);
                        }
                    });
                }

                wl.release();
            }
        }
    }

    public static void createTimer(Context context, int frequency, String identifier) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, frequency);

        Intent intent = new Intent("com.thijsdev.studentaanhuis.TIMER_UPDATE");
        intent.putExtra(ALARM, identifier);

        boolean alarmRunning = (PendingIntent.getBroadcast(context, REQUEST_CODE,intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        //Check if alarm is already running
        if(alarmRunning){
            return;
        }

        PendingIntent sender = PendingIntent.getBroadcast(context,REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),frequency, sender);
    }


    public boolean checkTimerExists(Context context) {
        Intent intent = new Intent("com.thijsdev.studentaanhuis.TIMER_UPDATE");
        boolean alarmRunning = (PendingIntent.getBroadcast(context, REQUEST_CODE,intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        return alarmRunning;
    }

    public static void updateAlaram(Context context, int frequency, String identifier) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, frequency);

        Intent intent = new Intent("com.thijsdev.studentaanhuis.TIMER_UPDATE");
        intent.putExtra(ALARM, identifier);

        PendingIntent sender = PendingIntent.getBroadcast(context,REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        am.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),frequency, sender);

    }



    public static void cancelAlarms(Context context) {
        Intent intent = new Intent("com.thijsdev.studentaanhuis.TIMER_UPDATE");
        PendingIntent sender = PendingIntent.getBroadcast(context,REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    private void generateNewPrikbordNotification(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.icon);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);
        mBuilder.setLargeIcon(bm);
        mBuilder.setContentTitle("Nieuwe Prikbord Items!");
        mBuilder.setContentText("Er zijn nieuwe prikbord items beschikbaar.");
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(context, SplashScreen.class);
        resultIntent.putExtra("LAUNCH_FROM_NOTIFICATION", "PRIKBORD");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashScreen.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
