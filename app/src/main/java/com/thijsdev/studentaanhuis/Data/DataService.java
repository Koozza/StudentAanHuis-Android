package com.thijsdev.studentaanhuis.Data;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Loon.LoonHelper;
import com.thijsdev.studentaanhuis.Prikbord.PrikbordHelper;
import com.thijsdev.studentaanhuis.R;
import com.thijsdev.studentaanhuis.RetryCallbackFailure;
import com.thijsdev.studentaanhuis.Werkgebied.WerkgebiedHelper;

public class DataService extends IntentService {
    WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper(this);
    PrikbordHelper prikbordHelper = new PrikbordHelper(this);
    LoonHelper loonHelper = new LoonHelper(this);
    public static String CURRENTLY_UPDATING = "currently_updating";
    public static String SET_TOTAL_PROGRESS = "set_total_progress";
    public static String INCREASE_PROGRESS = "increase_progress";
    public static String CLEAR_PROGRESS = "clear_progress";

    private int progress = 0;

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("ACTION");
        if(action.equals("ALL")) {
            processAll();
        }
    }

    private void processAll() {
        processWerkgebieden(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                processPrikbord(new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        processLoon(new Callback() {
                            @Override
                            public void onTaskCompleted(Object... results) {
                                int i = 0;
                            }
                        });
                    }
                });
            }
        });
    }

    private void processWerkgebieden(final Callback callback) {
        statusUpdate(CURRENTLY_UPDATING, getString(R.string.loading_werkgebied));
        statusUpdate(CLEAR_PROGRESS, null);
        statusUpdate(SET_TOTAL_PROGRESS, 2);
        statusUpdate(INCREASE_PROGRESS, 0);

        werkgebiedHelper.readWerkgebieden(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(SET_TOTAL_PROGRESS, werkgebiedHelper.countWerkgebiedAantal() + 1);
                werkgebiedHelper.processWerkgebieden(new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(INCREASE_PROGRESS, 0);
                    }
                }, callback);
            }
        }, new RetryCallbackFailure(10));
    }

    private void processPrikbord(final Callback callback) {
        statusUpdate(CURRENTLY_UPDATING, getString(R.string.loading_prikbord));
        statusUpdate(CLEAR_PROGRESS, null);
        statusUpdate(SET_TOTAL_PROGRESS, 2);
        statusUpdate(INCREASE_PROGRESS, 0);

        prikbordHelper.readPrikbordItems(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(SET_TOTAL_PROGRESS, prikbordHelper.countPrikbordItems() + 1);
                prikbordHelper.processPrikbordItems(new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(INCREASE_PROGRESS, 0);
                    }
                }, callback);
            }
        }, new RetryCallbackFailure(10));
    }

    private void processLoon(final Callback callback) {
        statusUpdate(CURRENTLY_UPDATING, getString(R.string.loading_loon));
        statusUpdate(CLEAR_PROGRESS, null);
        statusUpdate(SET_TOTAL_PROGRESS, 2);
        statusUpdate(INCREASE_PROGRESS, 0);

        loonHelper.readLoonItems(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(SET_TOTAL_PROGRESS, loonHelper.countLoonItems() + 1);
                loonHelper.processLoonItems(new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(INCREASE_PROGRESS, 0);
                    }
                }, callback);
            }
        }, new RetryCallbackFailure(10));
    }

    private void statusUpdate(String key, int value) {
        Intent intent = new Intent("DATA_UPDATE");
        intent.putExtra(key, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void statusUpdate(String key, String value) {
        Intent intent = new Intent("DATA_UPDATE");
        intent.putExtra(key, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
