package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class WerkgebiedHTTPHandler {
    public void getWerkGebieden(HttpClientClass client, Activity activity, Callback callback) {
        SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        String session = sharedpreferences.getString("session", null);

        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/work_areas");
            ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

            client.getSource(obj, callback);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
