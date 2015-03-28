package com.thijsdev.studentaanhuis;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    final public static String ALARM = "alarm";
    final public static String PRIKBORD = "prikbord";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.w("boot_broadcast_poc", "GOT INTENT");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.w("boot_broadcast_poc", "GOT BOOT");
            setPrikbordTimer(context);
        }else if ("com.thijsdev.studentaanhuis.TIMER_UPDATE".equals(intent.getAction())) {
            Log.w("boot_broadcast_poc", "GOT UPDATE");
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SAH ALARM MANAGER");

            wl.acquire();
            Bundle extras = intent.getExtras();

            //Check of het om de prikbord timer gaat
            if (extras != null && extras.getString(PRIKBORD).equals(PRIKBORD)) {
                Log.w("boot_broadcast_poc", "GOT CORRECT UPDATE");
                PrikbordHelper prikbordHelper = new PrikbordHelper();
                prikbordHelper.updatePrikbordItems(context, new Callback(), new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        generateNewPrikbordNotification(context);
                    }
                });
            }
            wl.release();
        }
    }

    public void setPrikbordTimer(Context context)
    {
        //TODO: Timer herstarten nadat app is forced quit.
        //TODO: Timer langer zetten
        //TODO: Database write terug zeggen!
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.thijsdev.studentaanhuis.TIMER_UPDATE");
        intent.putExtra(ALARM, PRIKBORD);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30 , pi);
    }

    private void generateNewPrikbordNotification(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.icon);
        mBuilder.setContentTitle("Nieuwe Prikbord Items!");
        mBuilder.setContentText("Er zijn nieuwe prikbord items beschikbaar.");

        Intent resultIntent = new Intent(context, PrikbordActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PrikbordActivity.class);

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
