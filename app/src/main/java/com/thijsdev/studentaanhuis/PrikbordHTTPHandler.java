package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PrikbordHTTPHandler {
    public void getPrikbordItems(final Context context, Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes");

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success, new Callback() {
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

                        Toast toast = Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPrikbordItem(final Context context, final int id, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/" + id + "/respond");

            HttpClientClass client = new HttpClientClass();
            client.getSource(obj, success ,new Callback() {
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

                        Toast toast = Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void declineItem(final int id, final Context context, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/respond");

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
                        params.put("pinboard_note_response[is_available]", "no");
                        params.put("commit", "Opslaan");

                        obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/create_or_update");
                        obj.put("params", params);

                        HttpClientClass client = new HttpClientClass();
                        client.doPost(obj, success, new Callback() {
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

                                    Toast toast = Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                                    toast.show();
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

                        Toast toast = Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void acceptItem(final int id, final String beschikbaarheid, final int werkgebiedId, final Context context, final Callback success, final Callback failure) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/respond");

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
                        params.put("pinboard_note_response[is_available]", "yes");
                        params.put("pinboard_note_response[content]", beschikbaarheid);
                        params.put("pinboard_note_response[address_id]", werkgebiedId);
                        params.put("commit", "Opslaan");

                        obj.put("url", "https://nl.sah3.net/students/pinboard_notes/"+id+"/create_or_update");
                        obj.put("params", params);

                        HttpClientClass client = new HttpClientClass();
                        client.doPost(obj, success, new Callback() {
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

                                    Toast toast = Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                                    toast.show();
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

                        Toast toast = Toast.makeText(context, context.getString(R.string.error_no_connection), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
