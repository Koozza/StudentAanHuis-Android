package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PrikbordHelper {
    public void updatePrikbordItems(final Activity activity, final PrikbordAdapter prikbordAdapter) {
        final DatabaseHandler db = new DatabaseHandler(activity);
        final HttpClientClass client = HttpClientClass.getInstance();
        final PrikbordHTTPHandler PrikbordHttpHandler = new PrikbordHTTPHandler();

        PrikbordHttpHandler.getPrikbordItems(client, activity, new Callback() {
            @Override
            public void onTaskCompleted(String result) {

                Document doc = Jsoup.parse(result);
                Elements trs = doc.select("tr:has(td)");
                for (Element tr : trs) {

                    Elements tds = tr.select("td");
                    if(tds.size() > 3) {
                        int id = Integer.parseInt(tds.get(3).children().first().attr("href").split("/")[3]);

                        PrikbordItem piDB = db.getPrikbordItem(id);
                        if (piDB == null) {
                            final PrikbordItem pi = new PrikbordItem();
                            pi.setType(tds.get(0).text());
                            pi.setAdres(tds.get(1).text());
                            pi.setDeadlineFromWebsite(tds.get(2).text());
                            pi.setId(id);

                            //Ophalen van details
                            PrikbordHttpHandler.getPrikbordItem(client, activity, id, new Callback() {
                                @Override
                                public void onTaskCompleted(String result) {
                                    Document doc = Jsoup.parse(result);
                                    String omschrijving = doc.getElementsByClass("widget").first().children().last().text();
                                    pi.setBeschrijving(omschrijving);

                                    String checked_yes = doc.getElementById("pinboard_note_response_is_available_yes").attr("checked");
                                    String checked_no = doc.getElementById("pinboard_note_response_is_available_no").attr("checked");
                                    if (checked_no.equals("checked"))
                                        pi.setBeschikbaar(1);
                                    else if (checked_yes.equals("checked"))
                                        pi.setBeschikbaar(2);
                                    else
                                        pi.setBeschikbaar(0);

                                    db.addPrikbordItem(pi);
                                    prikbordAdapter.addItem(pi);

                                    Log.v("SAH", "Item Added");
                                }
                            });
                        } else {
                            prikbordAdapter.addItem(piDB);
                        }
                    }
                }
            }
        });
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
