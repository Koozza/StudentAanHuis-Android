package com.thijsdev.studentaanhuis.Database;

import java.util.Date;

public class LoonMaand implements DatabaseObject {
    private int id, servicevragen, afspraken;
    private String naam;
    private boolean isCompleet, isUitbetaald;
    private Date datum;
    private double loon = 0d;
    private double loonMogelijk = 0d;


    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public boolean isCompleet() {
        return isCompleet;
    }

    public void setIsCompleet(boolean isCompleet) {
        this.isCompleet = isCompleet;
    }

    public double getLoon() {
        return loon;
    }

    public void setLoon(double _loon) {
        this.loon = _loon;
    }


    public void addLoonZeker(double _loon) {
        this.loon += _loon;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public int getServicevragen() {
        return servicevragen;
    }

    public void addServicevraag() {
        this.servicevragen++;
    }

    public int getAfspraken() {
        return afspraken;
    }

    public void addAfspraak() {
        this.afspraken++;
    }

    public boolean isUitbetaald() {
        return isUitbetaald;
    }

    public void setIsUitbetaald(boolean isUitbetaald) {
        this.isUitbetaald = isUitbetaald;
    }

    public double getLoonMogelijk() {
        return loonMogelijk;
    }

    public void setLoonMogelijk(double loonMogelijk) {
        this.loonMogelijk = loonMogelijk;
    }

    public void addLoonMogelijk(double loonMogelijk) {
        this.loonMogelijk += loonMogelijk;
    }
}
