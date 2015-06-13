package com.thijsdev.studentaanhuis.Data;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class DataService extends IntentService {
    private int counter = 0;

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                uppCounter();
                publishResults(counter);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void uppCounter() {
        counter++;
    }

    private void publishResults(int result) {
        Intent intent = new Intent("DATA_UPDATE");
        intent.putExtra("COUNTER", result);
        //sendBroadcast(intent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
