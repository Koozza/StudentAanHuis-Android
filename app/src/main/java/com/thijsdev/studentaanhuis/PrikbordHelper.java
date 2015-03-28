package com.thijsdev.studentaanhuis;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrikbordHelper {
    public void updatePrikbordItems(final Context context, final Callback existingItemCallback, final Callback newItemCallback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final HttpClientClass client = HttpClientClass.getInstance();
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();
        final TextView prikbord_status;

        //Dit alleen doen als het door de UI thread is aangeroepen
        if (context instanceof Activity) {
            prikbord_status = (TextView) ((Activity)context).findViewById(R.id.prikbord_status);
            prikbord_status.setVisibility(View.GONE);
        }else{
            prikbord_status = null;
        }

        client.init();


        prikbordHttpHandler.getPrikbordItems(client, context, new Callback() {
            @Override
            public void onTaskCompleted(Object result) {
                boolean gotItem = false;

                Document doc = Jsoup.parse((String) result);
                final Elements trs = doc.select("tr:has(td)");
                for (int i = 0; i < trs.size(); i++) {
                    Element tr = trs.get(i);
                    Elements tds = tr.select("td");

                    if (tds.size() > 3) {
                        gotItem = true;
                        int id = Integer.parseInt(tds.get(3).children().first().attr("href").split("/")[3]);
                        final boolean heeftGereageerd = tds.get(3).children().first().text().contains("ingeschreven");

                        PrikbordItem piDB = db.getPrikbordItem(id);
                        if (piDB == null) {
                            final PrikbordItem pi = new PrikbordItem();
                            pi.setType(tds.get(0).text());
                            pi.setAdres(tds.get(1).text());
                            pi.setDeadlineFromWebsite(tds.get(2).text());
                            pi.setId(id);

                            //Ophalen van details
                            prikbordHttpHandler.getPrikbordItem(client, context, id, new Callback() {
                                @Override
                                public void onTaskCompleted(Object result) {
                                    Document doc = Jsoup.parse((String) result);
                                    String omschrijving = doc.getElementsByClass("widget").first().children().last().text();
                                    pi.setBeschrijving(omschrijving);

                                    String checked_yes = doc.getElementById("pinboard_note_response_is_available_yes").attr("checked");
                                    String checked_no = doc.getElementById("pinboard_note_response_is_available_no").attr("checked");
                                    if (!heeftGereageerd)
                                        pi.setBeschikbaar(0);
                                    else if (checked_no.equals("checked"))
                                        pi.setBeschikbaar(1);
                                    else if (checked_yes.equals("checked"))
                                        pi.setBeschikbaar(2);

                                    String loc = doc.getElementById("appt_map_canvas").attr("data-positions").substring(1, doc.getElementById("appt_map_canvas").attr("data-positions").length() - 1);
                                    String[] coords = loc.split(",");

                                    pi.setLat(Double.parseDouble(coords[0]));
                                    pi.setLng(Double.parseDouble(coords[1]));

                                    //db.addPrikbordItem(pi);
                                    newItemCallback.onTaskCompleted(pi);
                                }
                            });
                        } else {
                            existingItemCallback.onTaskCompleted(piDB);
                        }
                    }
                }
                if (!gotItem) {
                    //Dit alleen doen als het door de UI thread is aangeroepen
                    if (context instanceof Activity) {
                        prikbord_status.setVisibility(View.VISIBLE);
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
            public void onTaskCompleted(Object result) {
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
            public void onTaskCompleted(Object result) {
                item.setBeschikbaar(2);
                db.updatePrikbordItem(item);

                callback.onTaskCompleted(result);
            }
        }, context);
    }
}
