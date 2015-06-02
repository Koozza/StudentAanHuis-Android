package com.thijsdev.studentaanhuis.Loon;

import android.content.Context;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.DefaultCallbackFailure;
import com.thijsdev.studentaanhuis.HttpClientClass;

import org.json.JSONException;
import org.json.JSONObject;

public class LoonHTTPHandler {
    public void getMonths(final Context context, Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/wages");

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success, new DefaultCallbackFailure(context, failure));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMonth(final Context context, final String date, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/wages/show_month?date=" + date);

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success ,new DefaultCallbackFailure(context, failure));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
