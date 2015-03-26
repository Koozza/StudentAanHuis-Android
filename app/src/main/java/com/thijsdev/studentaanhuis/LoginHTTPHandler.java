package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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
                ((DefaultHttpClient) client.getHTTPClient()).addRequestInterceptor(new SessionInjector(session));

                client.getSource(obj, new Callback() {
                    @Override
                    public void onTaskCompleted(String result) {
                        Document doc = Jsoup.parse(result);
                        Elements elements = doc.select("h1");
                        for (Element element : elements) {
                            if(element.text().equals("Aanmelden") || element.text().contains("Bad Request")) {
                                failure.onTaskCompleted(null);
                            }else{
                                succes.onTaskCompleted(null);
                            }
                            break;
                        }
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            failure.onTaskCompleted(null);
        }
    }

    public void doLogin(final HttpClientClass client, final Activity activity, final String username, final String password) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/login");

            client.getSource(obj, new Callback() {
                @Override
                public void onTaskCompleted(String result) {
                    Document doc = Jsoup.parse(result);
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

                        ((DefaultHttpClient) client.getHTTPClient()).addResponseInterceptor(new SessionKeeper(activity));

                        client.doPost(obj, new Callback() {
                            @Override
                            public void onTaskCompleted(String result) {
                                WerkgebiedHelper werkgebiedHelper = new WerkgebiedHelper();
                                werkgebiedHelper.updateWerkgebieden(activity, new Callback() {
                                    @Override
                                    public void onTaskCompleted(String result) {
                                        launchPrikbord(activity);
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class SessionKeeper implements HttpResponseInterceptor {
        private Activity activity;
        public SessionKeeper(Activity _activity) {
            activity = _activity;
        }

        @Override
        public void process(HttpResponse response, HttpContext context)  throws HttpException, IOException {
            Header[] headers = response.getHeaders("Set-Cookie");
            if ( headers != null && headers.length == 1 ){
                SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("session", headers[0].getValue());
                edit.commit();
            }
        }
    }

    private void launchLogin(Activity activity) {
        Intent goToNextActivity = new Intent(activity.getApplicationContext(), LoginActivity.class);
        activity.startActivity(goToNextActivity);
        activity.finish();
    }

    private void launchPrikbord(Activity activity) {
        Intent goToNextActivity = new Intent(activity.getApplicationContext(), PrikbordActivity.class);
        activity.startActivity(goToNextActivity);
        activity.finish();
    }
}
