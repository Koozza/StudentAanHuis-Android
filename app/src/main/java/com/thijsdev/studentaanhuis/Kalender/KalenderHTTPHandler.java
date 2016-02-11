package com.thijsdev.studentaanhuis.Kalender;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.HttpClientClass;
import com.thijsdev.studentaanhuis.SAHApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KalenderHTTPHandler {
    private DateFormat urlDateformat = new SimpleDateFormat("yyyy-MM-dd");

    public void getWeek(Date date, Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/calendar?start_day="+urlDateformat.format(date));

            HttpClientClass client = new HttpClientClass();
            SAHApplication.httpClientClass.getSource(obj, success, failure);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
