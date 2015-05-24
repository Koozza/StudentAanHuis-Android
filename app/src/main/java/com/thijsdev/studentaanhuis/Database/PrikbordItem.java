package com.thijsdev.studentaanhuis.Database;

import android.location.Location;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrikbordItem {
    private int id, beschikbaar;
    private String type, adres, deadline, beschrijving;
    private double lat, lng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getFormatedDeadline(String format, Locale locale) {
        if(locale == null)
            locale = Locale.getDefault();

        Date date = getDeadlineDateObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return dateFormat.format(date);
    }

    public Date getDeadlineDateObject() {
        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            date = dateFormat.parse(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setDeadlineFromWebsite(String deadline) {
        deadline = fixDate(deadline);

        SimpleDateFormat importDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = importDateFormat.parse(deadline);
            this.deadline = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("DATE:", this.deadline);
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    //0 = onbekend, 1 = niet beschikbaar, 2 = beschikbaar
    public int getBeschikbaar() {
        return beschikbaar;
    }

    public void setBeschikbaar(int beschikbaar) {
        this.beschikbaar = beschikbaar;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Location getLocation() {
        Location tempLoc = new Location("");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lng);

        return tempLoc;
    }

    private String fixDate(String date) {
        date = date.replace("jan", "01");
        date = date.replace("feb", "02");
        date = date.replace("mrt", "03");
        date = date.replace("apr", "04");
        date = date.replace("mei", "05");
        date = date.replace("jun", "06");
        date = date.replace("jul", "07");
        date = date.replace("aug", "08");
        date = date.replace("sep", "09");
        date = date.replace("okt", "10");
        date = date.replace("nov", "11");
        date = date.replace("dec", "12");

        date = date.replace("maa ", "");
        date = date.replace("din ", "");
        date = date.replace("woe ", "");
        date = date.replace("don ", "");
        date = date.replace("vri ", "");
        date = date.replace("zat ", "");
        date = date.replace("zon ", "");

        return date;
    }
}
