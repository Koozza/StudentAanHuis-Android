package com.thijsdev.studentaanhuis.Werkgebied;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.Werkgebied;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.GeoLocationHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class WerkgebiedHelper {
    public void updateWerkgebieden(final Activity activity, final Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(activity);
        final GeoLocationHelper locHelper = new GeoLocationHelper();
        WerkgebiedHTTPHandler werkgebiedHTTPHandler = new WerkgebiedHTTPHandler();

        werkgebiedHTTPHandler.getWerkGebieden(activity, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {

                Document doc = Jsoup.parse((String) results[0]);
                Elements trs = doc.select("tr:has(td)");
                for (Element tr : trs) {

                    Elements tds = tr.select("td");
                    int id = Integer.parseInt(tds.get(0).child(0).attr("work_area_id"));

                    Werkgebied werkgebiedDB = db.getWerkgebied(id);
                    if (werkgebiedDB == null) {
                        final Werkgebied werkgebied = new Werkgebied();
                        int actief = 0;
                        Location adres = locHelper.getLocationFromAddress(activity, tds.get(2).text() + ", The Netherlands");

                        if(tds.get(0).child(0).attr("checked").equals("checked"))
                            actief = 1;

                        werkgebied.setId(id);
                        werkgebied.setActief(actief);
                        werkgebied.setNaam(tds.get(1).text());
                        werkgebied.setAdres(tds.get(2).text());
                        werkgebied.setStraal(tds.get(3).text());
                        if(adres != null) {
                            werkgebied.setLat(adres.getLatitude());
                            werkgebied.setLng(adres.getLongitude());
                        }else{
                            werkgebied.setLat(0.0);
                            werkgebied.setLng(0.0);
                        }

                        db.addWerkgebied(werkgebied);

                        Log.v("SAH", "Werkgebied Added");
                    }
                }

                callback.onTaskCompleted((Object[])null);
            }
        }, new Callback());
    }

    public CharSequence[] getWerkgebiedenArray(Context context) {
        final DatabaseHandler db = new DatabaseHandler(context);

        ArrayList<String> werkgebieden = new ArrayList<String>();
        for(Werkgebied werkgebied : db.getActiveWerkgebieden()) {
            werkgebieden.add(werkgebied.getNaam());
        }

        return werkgebieden.toArray(new CharSequence[db.getActiveWerkgebieden().size()]);
    }

    public CharSequence[] getWerkgebiedenIDArray(Context context) {
        final DatabaseHandler db = new DatabaseHandler(context);

        ArrayList<String> werkgebieden = new ArrayList<String>();
        for(Werkgebied werkgebied : db.getActiveWerkgebieden()) {
            werkgebieden.add(Integer.toString(werkgebied.getId()));
        }

        return werkgebieden.toArray(new CharSequence[db.getActiveWerkgebieden().size()]);
    }

    public List<Werkgebied> getActiveWerkgebieden(Context context) {
        final DatabaseHandler db = new DatabaseHandler(context);

        return db.getActiveWerkgebieden();
    }

    public Location getFirstWerkgebiedLocation(Context context) {
        final DatabaseHandler db = new DatabaseHandler(context);
        List<Werkgebied> werkgebieden = db.getActiveWerkgebieden();
        if(werkgebieden.size() == 0)
            return null;

        return db.getActiveWerkgebieden().get(0).getLocation();
    }

    public Location getLocationFromWerkgebied(Context context, String WerkgebiedId) {
        final DatabaseHandler db = new DatabaseHandler(context);
        Werkgebied werkgebied = db.getWerkgebied(Integer.parseInt(WerkgebiedId));
        if(werkgebied == null)
            return null;

        return db.getActiveWerkgebieden().get(0).getLocation();
    }

    public void forceUpdateWerkgebieden(final Activity activity, Callback callback) {
        final DatabaseHandler db = new DatabaseHandler(activity);
        db.deleteAllWerkgebieden();
        updateWerkgebieden(activity, callback);
    }
}
