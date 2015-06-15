package com.thijsdev.studentaanhuis.Loon;

import android.content.Context;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.GeneralFunctions;
import com.thijsdev.studentaanhuis.RetryCallbackFailure;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import static com.thijsdev.studentaanhuis.Reversed.reversed;

public class LoonHelper {
    private int itemsAdded = 0;
    final TreeMap<Date, LoonMaand> loonMaandHashMap = new TreeMap<>();
    final LoonHTTPHandler loonHTTPHandler = new LoonHTTPHandler();

    private Context context;

    //Temp variables
    Elements loonItems = null;

    public LoonHelper(Context _context) {
        context = _context;
    }

    public void readLoonItems(final Callback finished, final Callback failure) {
        loonHTTPHandler.getMonths(new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                Document doc = Jsoup.parse((String) results[0]);
                Element table = doc.getElementsByClass("table").get(0);
                loonItems = table.getElementsByTag("a");

                for (Element e : loonItems) {
                    //Get Date & add to hasmap
                    SimpleDateFormat format = new SimpleDateFormat("M yyyy");
                    try {
                        LoonMaand loonMaand = new LoonMaand();
                        Date datum = format.parse(GeneralFunctions.fixDate(e.children().get(0).text()));
                        loonMaand.setDatum(datum);

                        //Check if the date is after july 2013
                        if(datum.after(format.parse("7 2013")))
                            loonMaandHashMap.put(datum, loonMaand);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                finished.onTaskCompleted(loonMaandHashMap);
            }
        }, failure);
    }

    public int countLoonItems() {
        return loonMaandHashMap.size();
    }


    public void processLoonItems(final Callback itemFinished, final Callback finished) {
        nextPage(loonItems, 0, context, countLoonItems(), itemFinished, finished);
    }

    /**
     * DEPRECATED
     */
    public void updateLoon(final Context context, final Callback callback) {
        /*
        final DatabaseHandler db = new DatabaseHandler(context);

        loonHTTPHandler.getMonths(context, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                Document doc = Jsoup.parse((String) results[0]);
                Element table = doc.getElementsByClass("table").get(0);

                for (Element e : table.getElementsByTag("a")) {
                    //Get Date & add to hasmap
                    SimpleDateFormat format = new SimpleDateFormat("M yyyy");
                    try {
                        LoonMaand loonMaand = new LoonMaand();
                        Date datum = format.parse(GeneralFunctions.fixDate(e.children().get(0).text()));
                        loonMaand.setDatum(datum);

                        //Check if the date is after july 2013
                        if(datum.after(format.parse("7 2013")))
                            loonMaandHashMap.put(datum, loonMaand);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                final int totalItems = loonMaandHashMap.size();
                nextPage(table.getElementsByTag("a"), 0, context, totalItems, callback);
            }
        }, new Callback());
        */
    }

    private void nextPage(final Elements elm, final int index, final Context context, final int totalItems, final Callback itemFinished, final Callback callback) {
        //Get details for month
        String[] datumStringParts = GeneralFunctions.fixDate(elm.get(index).children().get(0).text()).split(" ");
        SimpleDateFormat format = new SimpleDateFormat("M yyyy");
        try {
            if(format.parse(datumStringParts[0] + " " + datumStringParts[1]).after(format.parse("7 2013"))) {
                loonHTTPHandler.getMonth(datumStringParts[1] + "-" + datumStringParts[0] + "-1", new Callback() {
                    @Override
                    public void onTaskCompleted(Object... results) {
                        processPage((String) results[0], totalItems, itemFinished, callback);
                        if (index < elm.size() - 1) {
                            nextPage(elm, index + 1, context, totalItems, itemFinished, callback);
                        }
                    }
                }, new RetryCallbackFailure(10));
            }else{
                if (index < elm.size() - 1) {
                    nextPage(elm, index + 1, context, totalItems, itemFinished, callback);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void processPage(String source, int totalItems, Callback itemFinished, Callback callback) {
        Document doc = Jsoup.parse(source);
        Element tbody = doc.getElementsByTag("tbody").get(0);

        //Happens when there's no items in this month
        if (tbody != null) {
            Elements trs = tbody.select("tr:has(td)");
            for (Element tr : reversed(trs)) {
                //Mogelijk voor de huidige maand
                if (tr.children().get(3).text().equals("") && tr.children().get(4).text().equals("")) {
                    //Find first unpayed loonmaand
                    LoonMaand current = null;
                    for (LoonMaand loonMaand : loonMaandHashMap.values()) {
                        if (!loonMaand.isUitbetaald())
                            current = loonMaand;
                    }

                    //Calculate price
                    boolean isServiceVraag = tr.children().get(tr.children().size() - 1).text().contains("-");
                    Double price = Double.parseDouble(tr.children().get(tr.children().size() - 1).text().replace("-", "").substring(1).replace(",", "."));

                    //Check servicevraag
                    if (isServiceVraag) {
                        price = price * -1;
                        current.addServicevraag();
                    }

                    //check if uurloon
                    if (tr.children().get(1).text().contains("uurloon"))
                        current.addAfspraak();

                    current.addLoonMogelijk(price);
                    itemFinished.onTaskCompleted();
                } else {
                    //Payment for sure
                    SimpleDateFormat format = new SimpleDateFormat("M yyyy");
                    try {
                        //Calculate date
                        String datumString;
                        if(!tr.children().get(4).text().equals(""))
                            datumString = tr.children().get(4).text();
                        else
                            datumString = tr.children().get(3).text();

                        String[] datumStringParts = datumString.split(" ");
                        Date date = format.parse(GeneralFunctions.fixDate(datumStringParts[1] + " " + datumStringParts[2]));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        if(!tr.children().get(4).text().equals(""))
                            cal.add(Calendar.MONTH, -1);
                        date = cal.getTime();

                        //Calculate price
                        boolean isServiceVraag = tr.children().get(tr.children().size() - 1).text().contains("-");
                        Double price = Double.parseDouble(tr.children().get(tr.children().size() - 1).text().replace("-","").substring(1).replace(",", "."));

                        //Add date if it doesn't exist yet:
                        if(!loonMaandHashMap.containsKey(date)) {
                            SimpleDateFormat f = new SimpleDateFormat("M yyyy");
                            try {
                                LoonMaand loonMaand = new LoonMaand();
                                loonMaand.setDatum(date);
                                loonMaandHashMap.put(date, loonMaand);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        //Check servicevraag
                        if(isServiceVraag) {
                            price = price * -1;
                            loonMaandHashMap.get(date).addServicevraag();
                        }

                        //check if uurloon
                        if(tr.children().get(1).text().contains("uurloon"))
                            loonMaandHashMap.get(date).addAfspraak();

                        //set this month to uitbetaald
                        if(!tr.children().get(4).text().equals(""))
                            loonMaandHashMap.get(date).setIsUitbetaald(true);

                        loonMaandHashMap.get(date).addLoonZeker(price);
                        itemFinished.onTaskCompleted();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            isFinalLoonUpdate(totalItems, callback);
        }
    }

    private void isFinalLoonUpdate(int totalItems, Callback callback) {
        itemsAdded++;
        if(itemsAdded == totalItems) {
            itemsAdded = 0;
            callback.onTaskCompleted(loonMaandHashMap);
        }
    }
}
