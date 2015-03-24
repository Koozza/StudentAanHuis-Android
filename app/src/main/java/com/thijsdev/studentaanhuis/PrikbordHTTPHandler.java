package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PrikbordHTTPHandler {
    public PrikbordHTTPHandler() {

    }

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

    private class SessionInjector implements HttpRequestInterceptor {
        private String session;
        public SessionInjector(String _session) {
            session = _session;
        }

        @Override
        public void process(HttpRequest request, HttpContext context)  throws HttpException, IOException {
            request.setHeader("Cookie", session);
        }

    }
}
