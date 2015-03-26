package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class PrikbordHTTPHandler {
    public void getPrikbordItems(HttpClientClass client, Activity activity, Callback callback) {
        SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        String session = sharedpreferences.getString("session", null);

        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes");
            ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

            client.getSource(obj, callback);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPrikbordItem(HttpClientClass client, Activity activity, int id, Callback callback) {
        try {
            SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
            String session = sharedpreferences.getString("session", null);

            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/" + id + "/respond");
            ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

            client.getSource(obj, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
