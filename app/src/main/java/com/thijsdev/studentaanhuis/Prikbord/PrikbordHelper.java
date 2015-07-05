package com.thijsdev.studentaanhuis.Prikbord;

import android.content.Context;

import com.thijsdev.studentaanhuis.ArraylistIdSearch;
import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.Database.DatabaseObject;
import com.thijsdev.studentaanhuis.Database.PrikbordItem;
import com.thijsdev.studentaanhuis.Database.Werkgebied;
import com.thijsdev.studentaanhuis.R;
import com.thijsdev.studentaanhuis.RetryCallbackFailure;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class PrikbordHelper {
    private int itemsAdded = 0;
    final ArrayList<PrikbordItem> newItems = new ArrayList<>();
    final PrikbordHTTPHandler prikbordHttpHandler = new PrikbordHTTPHandler();

    private Context context;

    //Temp variables
    Elements prikbordItems = null;
    final ArrayList<DatabaseObject> stillActive = new ArrayList<>();

    //Callbacks
    Callback itemRemovedCallback, itemAddedCallback, itemUpdatedCallback = null;

    public PrikbordHelper(Context _context) {
        context = _context;
    }

    /**
     * Function to read the SAH 3.0 website for prikbord items.
     * Places Elements into "prikbordItems" variable.
     * finished callback returns Elements prikbordItems.
     *
     * @param finished
     * @param failure
     */
    public void readPrikbordItems(final Callback finished, final Callback failure) {
        prikbordHttpHandler.getPrikbordItems(context, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                Document doc = Jsoup.parse((String) results[0]);
                prikbordItems = doc.select("tr:has(td)");

                finished.onTaskCompleted(prikbordItems);
            }
        }, failure);
    }

    /**
     * Function to count the ammount of prikbord items on SAH 3.0.
     *
     * @throws RuntimeException
     * @return int
     */
    public int countPrikbordItems() {
        if(prikbordItems == null) {
            throw new RuntimeException("prikbordItems is null. Call readPrikbordItems() first.");
        } else {
            int itemCounter = 0;
            for (Element tr : prikbordItems)
                if(tr.select("td").size() > 3)
                    itemCounter++;

            return itemCounter;
        }
    }

    /**
     * Function to process the prikbord items from SAH 3.0.
     * itemFinished returns: bool isUpdate, PrikbordItem prikborditem.
     * finished return nothing.
     *
     * @throws RuntimeException
     * @param itemFinished
     * @param finished
     */
    public void processPrikbordItems(final Callback itemFinished, final Callback finished) {
        //Check variables
        if (prikbordItems == null)
            throw new RuntimeException("werkgebieden is null. Call readWerkgebieden() first.");

        final DatabaseHandler db = DatabaseHandler.getInstance(context);

        //Soms zijn er geen prikbord items, dus gelijk een callback aanroepen doen.
        if(countPrikbordItems() == 0)
            isFinalPrikbordUpdate(1, db, finished); //Call it with 1 or it'll fail

        for (Element tr : prikbordItems) {
            Elements tds = tr.select("td");

            if (tds.size() > 3) {
                int id = Integer.parseInt(tds.get(3).children().first().attr("href").split("/")[3]);
                final boolean heeftGereageerd = tds.get(3).children().first().text().contains("Reeds gereageerd");

                PrikbordItem piDB = db.getPrikbordItem(id);
                final boolean isUpdate = piDB != null;

                //create PI variable
                PrikbordItem tempPI;
                if (!isUpdate)
                    tempPI = new PrikbordItem();
                else
                    tempPI = piDB;

                final PrikbordItem pi = tempPI;

                if(!isUpdate) {
                    pi.setType(tds.get(0).text());
                    pi.setAdres(tds.get(1).text());
                    pi.setDeadlineFromWebsite(tds.get(2).text());
                    pi.setId(id);
                }

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

                        if(!isUpdate) {
                            db.addPrikbordItem(pi);
                            newItems.add(pi);
                            if(itemAddedCallback != null)
                                itemAddedCallback.onTaskCompleted(pi);
                        }else{
                            db.updatePrikbordItem(pi);
                            if(itemUpdatedCallback != null)
                                itemUpdatedCallback.onTaskCompleted(pi);
                        }

                        //Mark prikbord item as still active
                        stillActive.add(pi);

                        itemFinished.onTaskCompleted(isUpdate, pi);
                        isFinalPrikbordUpdate(countPrikbordItems(), db, finished);
                    }
                }, new RetryCallbackFailure(10));
            }
        }
    }

    /**
     * Add callback to Item Removed event.
     * Returns a prikbord item in the callback
     * @param callback
     */
    public void addItemRemovedCallback(Callback callback) {
        itemRemovedCallback = callback;
    }

    /**
     * Add callback to Item Added event.
     * Returns a prikbord item in the callback
     * @param callback
     */
    public void addItemAddedCallback(Callback callback) {
        itemAddedCallback = callback;
    }

    /**
     * Add callback to Item Updated event.
     * Returns a prikbord item in the callback
     * @param callback
     */
    public void addItemUpdatedCallback(Callback callback) {
        itemUpdatedCallback = callback;
    }

    public void declineItem(final Context context, final PrikbordItem item, final Callback callback) {
        final DatabaseHandler db = DatabaseHandler.getInstance(context);
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
        final DatabaseHandler db = DatabaseHandler.getInstance(context);
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

    private void isFinalPrikbordUpdate(int totalItems, DatabaseHandler db, Callback callback) {
        itemsAdded++;
        if(itemsAdded == totalItems) {
            itemsAdded = 0;

            //Remove other prikbord items to keep DB clean
            for(PrikbordItem pi : db.getPrikbordItems()) {
                if (!ArraylistIdSearch.compare(stillActive, pi)) {
                    if(itemRemovedCallback != null)
                        itemRemovedCallback.onTaskCompleted(pi.getId());

                    db.deletePrikbordItem(pi);
                }
            }

            callback.onTaskCompleted(newItems);
        }
    }

    public static void fixNoItemsfound(PrikbordAdapter prikbordAdapter, Context context) {
        for(int i = 0; i<3; i++) {
            int headerLocation = prikbordAdapter.findItem(i);
            if(headerLocation+1 < prikbordAdapter.getItemCount()) {
                if(prikbordAdapter.getItemViewType(headerLocation+1) == 1)
                    prikbordAdapter.addItem(headerLocation+1, new PrikbordHeader(i+3, context.getString(R.string.no_prikbord_items_found), true));
                else if(prikbordAdapter.getItemViewType(headerLocation+1) == 0 && prikbordAdapter.findItem(i+3) != -1)
                    prikbordAdapter.removeItem(prikbordAdapter.findItem(i+3));

            }else{
                prikbordAdapter.addItem(headerLocation+1, new PrikbordHeader(i+3, context.getString(R.string.no_prikbord_items_found), true));
            }
        }
    }
}
