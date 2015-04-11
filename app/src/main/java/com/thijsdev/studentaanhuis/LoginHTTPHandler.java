package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoginHTTPHandler {
    public LoginHTTPHandler() {

    }

    public void checkLogin(final Activity activity, final Callback succes, final Callback failure) {
        SharedPreferences sharedpreferences = activity.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        String session = sharedpreferences.getString("session", null);
        if(session != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("url", "https://nl.sah3.net/students/time_offs");

                HttpClientClass client = new HttpClientClass();
                client.getSource(obj, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        Document doc = Jsoup.parse((String) results[0]);
                        Elements elements = doc.select("h1");
                        for (Element element : elements) {
                            if(element.text().equals("Aanmelden") || element.text().contains("Bad Request")) {
                                SAHApplication.cookieManager.getCookieStore().removeAll();
                                failure.onTaskCompleted((Object[])null);
                            }else{
                                succes.onTaskCompleted((Object[])null);
                            }
                            break;
                        }
                    }
                }, new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        HttpClientClass client = ((HttpClientClass)results[1]);
                        if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            client.retryLastCall();
                        }else{
                            SAHApplication.cookieManager.getCookieStore().removeAll();
                            if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                client.retryLastCall();
                            }else {
                                failure.onTaskCompleted((Object[])null);

                                Toast toast = Toast.makeText(activity, activity.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            SAHApplication.cookieManager.getCookieStore().removeAll();
            failure.onTaskCompleted((Object[])null);
        }
    }

    public void doLogin(final Activity activity, final String username, final String password, final Callback succes, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/login");

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, new Callback() {
                @Override
                public void onTaskCompleted(Object... results) {
                    Document doc = Jsoup.parse((String) results[0]);
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

                        HttpClientClass client = new HttpClientClass();
                        client.doPost(obj, new Callback() {
                            @Override
                            public void onTaskCompleted(Object... results) {
                                Document doc = Jsoup.parse((String) results[0]);
                                Element content = doc.getElementById("flash_alert");

                                if(content == null)
                                    succes.onTaskCompleted("");
                                else
                                    failure.onTaskCompleted(content.text());
                            }
                        }, new Callback() {
                            @Override
                            public void onTaskCompleted(Object... results) {
                                HttpClientClass client = ((HttpClientClass)results[1]);
                                if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    client.retryLastCall();
                                }else {
                                    if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        client.retryLastCall();
                                    }else {
                                        failure.onTaskCompleted((Object[])null);

                                        Toast toast = Toast.makeText(activity, activity.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Callback() {
                @Override
                public void onTaskCompleted(Object... results) {
                    HttpClientClass client = ((HttpClientClass)results[1]);
                    if(client.getHttpClientObject().getAttempt() < SAHApplication.HTTP_RETRIES) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        client.retryLastCall();
                    }else {
                        failure.onTaskCompleted((Object[])null);

                        Toast toast = Toast.makeText(activity, activity.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
