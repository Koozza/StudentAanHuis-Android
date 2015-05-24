package com.thijsdev.studentaanhuis.Prikbord;

import android.content.Context;
import android.widget.TextView;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.PrikbordItem;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.Database.Werkgebied;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class PrikbordHelper {
    private int itemsAdded = 0;
    final ArrayList<PrikbordItem> newItems = new ArrayList<PrikbordItem>();

    public void updatePrikbordItems(final Context context, final Callback existingItemCallback, final Callback newItemCallback, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();
        final TextView prikbord_status;

        //TODO: Dit moet er wel terug in
        //Dit alleen doen als het door de UI thread is aangeroepen
        /*
        if (context instanceof Activity) {
            prikbord_status = (TextView) ((Activity)context).findViewById(R.id.prikbord_status);
            prikbord_status.setVisibility(View.GONE);
        }else{
            prikbord_status = null;
        }
        */

        prikbordHttpHandler.getPrikbordItems(context, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                boolean gotItem = false;

                Document doc = Jsoup.parse((String) results[0]);
                final Elements trs = doc.select("tr:has(td)");

                //Count number of active prikbord items
                int itemCounter = 0;
                for (Element tr : trs)
                    if(tr.select("td").size() > 3)
                        itemCounter++;

                final int totalItems = itemCounter;

                for (Element tr : trs) {
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
                            prikbordHttpHandler.getPrikbordItem(context, id, new Callback() {
                                @Override
                                public void onTaskCompleted(Object... result) {
                                    Document doc = Jsoup.parse((String) result[0]);
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

                                    db.addPrikbordItem(pi);
                                    newItems.add(pi);
                                    newItemCallback.onTaskCompleted(pi);

                                    isFinalPrikbordUpdate(totalItems, callback);
                                }
                            }, new Callback() {
                                @Override
                                public void onTaskCompleted(Object... results) {
                                    isFinalPrikbordUpdate(totalItems, callback);
                                }
                            });
                        } else {
                            existingItemCallback.onTaskCompleted(piDB);
                            isFinalPrikbordUpdate(totalItems, callback);
                        }
                    }
                }
                if (!gotItem) {
                    //Dit alleen doen als het door de UI thread is aangeroepen
                    /*
                    if (context instanceof Activity) {
                        prikbord_status.setVisibility(View.VISIBLE);
                    }
                    */
                }
            }
        }, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                callback.onTaskCompleted(newItems);
            }
        });
    }

    public void declineItem(final Context context, final PrikbordItem item, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();

        prikbordHttpHandler.declineItem(item.getId(), context, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                item.setBeschikbaar(1);
                db.updatePrikbordItem(item);

                callback.onTaskCompleted(results);
            }
        }, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                callback.onTaskCompleted(newItems);
            }
        });
    }

    public void acceptItem(final Context context, final PrikbordItem item, final String beschikbaarheid, final Werkgebied werkgebied, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();

        prikbordHttpHandler.acceptItem(item.getId(), beschikbaarheid, werkgebied.getId(), context, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                item.setBeschikbaar(2);
                db.updatePrikbordItem(item);

                callback.onTaskCompleted(results);
            }
        }, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                callback.onTaskCompleted(newItems);
            }
        });
    }

    private void isFinalPrikbordUpdate(int totalItems, Callback callback) {
        itemsAdded++;
        if(itemsAdded == totalItems) {
            itemsAdded = 0;
            callback.onTaskCompleted(newItems);
        }
    }
}
