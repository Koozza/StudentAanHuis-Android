package com.thijsdev.studentaanhuis;

import android.location.Location;

public class Werkgebied {
    private int id, actief;
    private Double lat, lng;
    private String naam, adres, straal;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActief() {
        return actief;
    }

    public void setActief(int actief) {
        this.actief = actief;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getStraal() {
        return straal;
    }

    public void setStraal(String straal) {
        this.straal = straal;
    }

    public Location getLocation() {
        Location tempLoc = new Location("");
        tempLoc.setLatitude(lat);
        tempLoc.setLongitude(lng);

        return tempLoc;
    }
}
