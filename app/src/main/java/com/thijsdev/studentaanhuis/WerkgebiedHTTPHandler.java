package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class WerkgebiedHTTPHandler {
    public void getWerkGebieden(final HttpClientClass client, final Activity activity, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/work_areas");

            client.getSource(obj, success, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        client.retryLastCall();
                    }else {
                        failure.onTaskCompleted(null);

                        Toast toast = Toast.makeText(activity, activity.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
