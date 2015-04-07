package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoginHTTPHandler {
    public LoginHTTPHandler() {

    }

    public void checkLogin(final Activity activity, HttpClientClass client, final Callback succes, final Callback failure) {
        SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        String session = sharedpreferences.getString("session", null);
        if(session != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("url", "https://nl.sah3.net/students/time_offs");

                client.getSource(obj, new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        Document doc = Jsoup.parse((String) result);
                        Elements elements = doc.select("h1");
                        for (Element element : elements) {
                            if(element.text().equals("Aanmelden") || element.text().contains("Bad Request")) {
                                SAHApplication.cookieManager.getCookieStore().removeAll();
                                failure.onTaskCompleted(null);
                            }else{
                                succes.onTaskCompleted(null);
                            }
                            break;
                        }
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object result) {
                        //TODO: Failure callback implementeren
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            failure.onTaskCompleted(null);
        }
    }

    public void doLogin(final HttpClientClass client, final Activity activity, final String username, final String password, final Callback succes, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/login");

            client.getSource(obj, new Callback() {
                @Override
                public void onTaskCompleted(Object result) {
                    Document doc = Jsoup.parse((String) result);
                    Element content = doc.getElementsByAttributeValue("name", "authenticity_token").first();


                    try {
                        JSONObject obj = new JSONObject();
                        JSONObject params = new JSONObject();

                        params.put("authenticity_token", content.attr("value"));
                        params.put("email", username);
                        params.put("password", password);
                        params.put("commit", "Inloggen");

                        obj.put("url", "https://nl.sah3.net/sessions");
                        obj.put("params", params);

                        client.doPost(obj, new Callback() {
                            @Override
                            public void onTaskCompleted(Object result) {
                                Document doc = Jsoup.parse((String) result);
                                Element content = doc.getElementById("flash_alert");

                                if(content == null)
                                    succes.onTaskCompleted("");
                                else
                                    failure.onTaskCompleted(content.text());
                            }
                        }, new Callback() {
                            @Override
                            public void onTaskCompleted(Object result) {
                                //TODO: Implement failure callback
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Callback());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
