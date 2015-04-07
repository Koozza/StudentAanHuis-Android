package com.thijsdev.studentaanhuis;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class WerkgebiedHTTPHandler {
    public void getWerkGebieden(HttpClientClass client, Activity activity, Callback callback) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/work_areas");

            //TODO: Implement failure callback
            client.getSource(obj, callback, new Callback());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
