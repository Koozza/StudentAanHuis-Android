package com.thijsdev.studentaanhuis.Werkgebied;

import android.content.Context;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.DefaultCallbackFailure;
import com.thijsdev.studentaanhuis.HttpClientClass;
import com.thijsdev.studentaanhuis.SAHApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class WerkgebiedHTTPHandler {
    public void getWerkGebieden(final Context context, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/work_areas");

            //HttpClientClass client = new HttpClientClass();
            SAHApplication.httpClientClass.getSource(obj, success, new DefaultCallbackFailure(context, failure));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
