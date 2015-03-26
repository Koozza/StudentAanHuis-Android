package com.thijsdev.studentaanhuis;

import android.app.Activity;

public class WerkgebiedHelper {
    public void updateWerkgebieden(final Activity activity) {
        final DatabaseHandler db = new DatabaseHandler(activity);
        final HttpClientClass client = HttpClientClass.getInstance();
        WerkgebiedHTTPHandler werkgebiedHTTPHandler = new WerkgebiedHTTPHandler();

        werkgebiedHTTPHandler.getWerkGebieden(client, activity, new Callback() {
            @Override
            public void onTaskCompleted(String result) {
                
            }
        });
    }
}
