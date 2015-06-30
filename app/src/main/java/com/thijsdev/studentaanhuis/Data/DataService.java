package com.thijsdev.studentaanhuis.Data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.DatabaseObject;
import com.thijsdev.studentaanhuis.Database.PrikbordItem;
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
    public static String FINISHED = "finished";

    //Prikbord
    public static String PRIKBORD_ITEM_REMOVED = "prikbord_item_removed";
    public static String PRIKBORD_ITEM_ADDED = "prikbord_item_added";
    public static String PRIKBORD_ITEM_UPDATED = "prikbord_item_updated";
    public static String PRIKBORD_FINISHED = "prikbord_finished";

    //Loon
    public static String LOON_ITEM_ADDED = "loon_item_added";
    public static String LOON_ITEM_UPDATED = "loon_item_updated";
    public static String LOON_FINISHED = "loon_finished";

    //Loon
    public static String WERKGEBIED_FINISHED = "werkgebied_finished";

    private int progress = 0;

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("ACTION");
        if(action.equals("ALL")) {
            processAll();
        }else if (action.equals("PRIKBORD")) {
            processPrikbord(new Callback());
        }else if (action.equals("LOON")) {
            processLoon(new Callback());
        }else if (action.equals("WERKGEBIED")) {
            processWerkgebieden(new Callback());
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

                                //Set status to completely updated.
                                SharedPreferences sharedpreferences = getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sharedpreferences.edit();
                                edit.putInt("DATA_VERSION", DataActivity.VERSION);
                                edit.commit();

                                statusUpdate(FINISHED);
                            }
                        });
                    }
                });
            }
        });
    }

    private void processWerkgebieden(final Callback callback) {
        statusUpdate(CURRENTLY_UPDATING, getString(R.string.loading_werkgebied));
        statusUpdate(CLEAR_PROGRESS);
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
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(WERKGEBIED_FINISHED);
                        callback.onTaskCompleted(results);
                    }
                });
            }
        }, new RetryCallbackFailure(10));
    }

    private void processPrikbord(final Callback callback) {
        statusUpdate(CURRENTLY_UPDATING, getString(R.string.loading_prikbord));
        statusUpdate(CLEAR_PROGRESS);
        statusUpdate(SET_TOTAL_PROGRESS, 2);
        statusUpdate(INCREASE_PROGRESS, 0);

        //Callback for removed prikbord items
        prikbordHelper.addItemRemovedCallback(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(PRIKBORD_ITEM_REMOVED, (Integer) results[0]);
            }
        });

        //Callback for added prikbord items
        prikbordHelper.addItemAddedCallback(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(PRIKBORD_ITEM_ADDED, (PrikbordItem) results[0]);
            }
        });

        //Callback for updated prikbord items
        prikbordHelper.addItemUpdatedCallback(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(PRIKBORD_ITEM_UPDATED, (PrikbordItem) results[0]);
            }
        });

        prikbordHelper.readPrikbordItems(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(SET_TOTAL_PROGRESS, prikbordHelper.countPrikbordItems() + 1);
                prikbordHelper.processPrikbordItems(new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(INCREASE_PROGRESS, 0);
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(PRIKBORD_FINISHED);
                        callback.onTaskCompleted(results);
                    }
                });
            }
        }, new RetryCallbackFailure(10));
    }

    private void processLoon(final Callback callback) {
        statusUpdate(CURRENTLY_UPDATING, getString(R.string.loading_loon));
        statusUpdate(CLEAR_PROGRESS);
        statusUpdate(SET_TOTAL_PROGRESS, 2);
        statusUpdate(INCREASE_PROGRESS, 0);

        //Callback for added loon items
        loonHelper.addItemAddedCallback(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(LOON_ITEM_ADDED, (String) results[0]);
            }
        });

        //Callback for updated loon items
        loonHelper.addItemUpdatedCallback(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(LOON_ITEM_UPDATED, (String) results[0]);
            }
        });

        loonHelper.readLoonItems(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                statusUpdate(SET_TOTAL_PROGRESS, loonHelper.countLoonItems() + 1);
                loonHelper.processLoonItems(new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(INCREASE_PROGRESS, 0);
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        statusUpdate(LOON_FINISHED);
                        callback.onTaskCompleted(results);
                    }
                });
            }
        }, new RetryCallbackFailure(10));
    }

    private void statusUpdate(String key) {
        Intent intent = new Intent("DATA_UPDATE");
        intent.putExtra(key, (String) null);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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

    private void statusUpdate(String key, DatabaseObject value) {
        Intent intent = new Intent("DATA_UPDATE");
        intent.putExtra(key, value.getId());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
