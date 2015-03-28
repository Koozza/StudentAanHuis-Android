package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PrikbordHTTPHandler {
    public void getPrikbordItems(HttpClientClass client, Context context, Callback callback) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
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

    public void getPrikbordItem(HttpClientClass client, Context context, int id, Callback callback) {
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
            String session = sharedpreferences.getString("session", null);

            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/" + id + "/respond");
            ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

            client.getSource(obj, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void declineItem(final HttpClientClass client, final int id, final Callback callback, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        final String session = sharedpreferences.getString("session", null);

        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/respond");

            client.getSource(obj, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    Document doc = Jsoup.parse((String) result);
                    Element content = doc.getElementsByAttributeValue("name", "authenticity_token").first();

                    try {
                        JSONObject obj = new JSONObject();
                        JSONObject params = new JSONObject();

                        params.put("authenticity_token", content.attr("value"));
                        params.put("pinboard_note_response[is_available]", "no");
                        params.put("commit", "Opslaan");

                        obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/create_or_update");
                        obj.put("params", params);

                        ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

                        client.doPost(obj, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void acceptItem(final HttpClientClass client, final int id, final String beschikbaarheid, final int werkgebiedId, final Callback callback, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        final String session = sharedpreferences.getString("session", null);

        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/respond");

            client.getSource(obj, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    Document doc = Jsoup.parse((String) result);
                    Element content = doc.getElementsByAttributeValue("name", "authenticity_token").first();

                    try {
                        JSONObject obj = new JSONObject();
                        JSONObject params = new JSONObject();

                        params.put("authenticity_token", content.attr("value"));
                        params.put("pinboard_note_response[is_available]", "yes");
                        params.put("pinboard_note_response[content]", beschikbaarheid);
                        params.put("pinboard_note_response[address_id]", werkgebiedId);
                        params.put("commit", "Opslaan");

                        obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/create_or_update");
                        obj.put("params", params);

                        ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

                        client.doPost(obj, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
