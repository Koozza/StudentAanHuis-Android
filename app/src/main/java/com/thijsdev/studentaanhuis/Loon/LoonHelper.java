package com.thijsdev.studentaanhuis.Loon;

import android.content.Context;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.GeneralFunctions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

public class LoonHelper {
    private int itemsAdded = 0;
    final TreeMap<Date, LoonMaand> loonMaandHashMap = new TreeMap<>();

    public void updateLoon(final Context context, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(context);
        final LoonHTTPHandler loonHTTPHandler = new LoonHTTPHandler();

        loonHTTPHandler.getMonths(context, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                Document doc = Jsoup.parse((String) results[0]);
                Element table = doc.getElementsByClass("table").get(0);

                for(Element e : table.getElementsByTag("a")) {
                    //Get Date & add to hasmap
                    SimpleDateFormat format = new SimpleDateFormat("M yyyy");
                    try {
                        LoonMaand loonMaand = new LoonMaand();
                        Date datum = format.parse(GeneralFunctions.fixDate(e.children().get(0).text()));
                        loonMaand.setDatum(datum);

                        loonMaandHashMap.put(datum, loonMaand);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                final int totalItems = table.getElementsByTag("a").size();

                //Loop trough it AGAIN now getting all details
                for(Element e : table.getElementsByTag("a")) {
                    //Get details for month
                    String[] datumStringParts = GeneralFunctions.fixDate(e.children().get(0).text()).split(" ");
                    loonHTTPHandler.getMonth(context, datumStringParts[1] + "-" + datumStringParts[0] + "-1", new Callback() {
                        @Override
                        public void onTaskCompleted(Object... results) {
                            Document doc = Jsoup.parse((String) results[0]);
                            Element tbody = doc.getElementsByTag("tbody").get(0);

                            //Happens when there's no items in this month
                            if (tbody != null) {
                                Elements trs = tbody.select("tr:has(td)");
                                for (Element tr : trs) {
                                    //Mogelijk voor de huidige maand
                                    if (tr.children().get(4).text() == "") {
                                        //loonMaandHashMap.get(firstDateKey).addLoonMogelijk(Double.parseDouble(tr.children().get(7).text().substring(1).replace(",",".")));
                                    } else {
                                        SimpleDateFormat format = new SimpleDateFormat("M yyyy");
                                        try {
                                            String datumString = tr.children().get(4).text();
                                            String[] datumStringParts = datumString.split(" ");
                                            Date date = format.parse(GeneralFunctions.fixDate(datumStringParts[1] + " " + datumStringParts[2]));
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(date);
                                            cal.add(Calendar.MONTH, -1);
                                            date = cal.getTime();

                                            loonMaandHashMap.get(date).addLoonZeker(Double.parseDouble(tr.children().get(7).text().substring(1).replace(",",".")));
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                                isFinalLoonUpdate(totalItems, callback);
                            }
                        }
                    }, new Callback());
                }
            }
        }, new Callback());
    }

    private void isFinalLoonUpdate(int totalItems, Callback callback) {
        itemsAdded++;
        if(itemsAdded == totalItems) {
            itemsAdded = 0;
            callback.onTaskCompleted(loonMaandHashMap);
        }
    }
}
