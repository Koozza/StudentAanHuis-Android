package com.thijsdev.studentaanhuis.Werkgebied;

import android.app.Activity;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.DefaultCallbackFailure;
import com.thijsdev.studentaanhuis.HttpClientClass;

import org.json.JSONException;
import org.json.JSONObject;

public class WerkgebiedHTTPHandler {
    public void getWerkGebieden(final Activity activity, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/work_areas");

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success, new DefaultCallbackFailure(activity, failure));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
