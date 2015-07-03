package com.thijsdev.studentaanhuis.Kalender;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.HttpClientClass;

import org.json.JSONException;
import org.json.JSONObject;

public class LoonHTTPHandler {
    public void getMonths(Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/wages");

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success, failure);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMonth(final String date, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/wages/show_month?date=" + date);

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success ,failure);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
