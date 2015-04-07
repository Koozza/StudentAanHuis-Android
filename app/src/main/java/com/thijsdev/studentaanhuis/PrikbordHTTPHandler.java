package com.thijsdev.studentaanhuis;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PrikbordHTTPHandler {
    public void getPrikbordItems(HttpClientClass client, Context context, Callback callback) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes");

            //TODO: Implement failure callback
            client.getSource(obj, callback, new Callback());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPrikbordItem(HttpClientClass client, Context context, int id, Callback callback) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/" + id + "/respond");

            //TODO: Implement failure callback
            client.getSource(obj, callback, new Callback());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void declineItem(final HttpClientClass client, final int id, final Callback callback, Context context) {
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

                        //TODO: Implement failure callback
                        client.doPost(obj, callback, new Callback());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            //TODO: Implement failure callback
                }
            }, new Callback());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void acceptItem(final HttpClientClass client, final int id, final String beschikbaarheid, final int werkgebiedId, final Callback callback, Context context) {
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

                        //TODO: Implement failure callback
                        client.doPost(obj, callback, new Callback());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            //TODO: Implement failure callback
                }
            }, new Callback());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
