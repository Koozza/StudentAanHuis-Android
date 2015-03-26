package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrikbordHelper {
    public void updatePrikbordItems(final Activity activity, final PrikbordAdapter prikbordAdapter) {
        final DatabaseHandler db = new DatabaseHandler(activity);
        final HttpClientClass client = HttpClientClass.getInstance();
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();

        prikbordHttpHandler.getPrikbordItems(client, activity, new Callback() {
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
                            prikbordHttpHandler.getPrikbordItem(client, activity, id, new Callback() {
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

                                    String loc = doc.getElementById("appt_map_canvas").attr("data-positions").substring(1, doc.getElementById("appt_map_canvas").attr("data-positions").length() - 1);
                                    String[] coords = loc.split(",");

                                    pi.setLat(Double.parseDouble(coords[0]));
                                    pi.setLng(Double.parseDouble(coords[1]));

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

    public void declineItem(Context context, final PrikbordItem item, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final HttpClientClass client = HttpClientClass.getInstance();
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();

        prikbordHttpHandler.declineItem(client, item.getId(), new Callback() {
            @Override
            public void onTaskCompleted(String result) {
                item.setBeschikbaar(1);
                db.updatePrikbordItem(item);

                callback.onTaskCompleted(result);
            }
        }, context);
    }

    public void acceptItem(Context context, final PrikbordItem item, final String beschikbaarheid, final Werkgebied werkgebied, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final HttpClientClass client = HttpClientClass.getInstance();
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();

        prikbordHttpHandler.acceptItem(client, item.getId(), beschikbaarheid, werkgebied.getId(), new Callback() {
            @Override
            public void onTaskCompleted(String result) {
                item.setBeschikbaar(2);
                db.updatePrikbordItem(item);

                callback.onTaskCompleted(result);
            }
        }, context);
    }
}
