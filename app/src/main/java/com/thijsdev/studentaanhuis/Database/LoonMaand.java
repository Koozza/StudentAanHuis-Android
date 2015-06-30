package com.thijsdev.studentaanhuis.Database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoonMaand implements DatabaseObject {
    private int id, servicevragen, afspraken;
    private String naam;
    private boolean isCompleet, isUitbetaald;
    private Date datum;
    private double loon = 0d;
    private double loonMogelijk = 0d;
    private double loonAndereMaand = 0d;


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

    public void setDatumFromString(String datum) {
        if(datum != "") {
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            try {
                this.datum = format.parse(datum);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int getServicevragen() {
        return servicevragen;
    }

    public void addServicevragen(int servicevragen) {
        this.servicevragen += servicevragen;
    }

    public void addServicevraag() {
        this.servicevragen++;
    }

    public int getAfspraken() {
        return afspraken;
    }

    public void addAfspraken(int afspraken) {
        this.afspraken += afspraken;
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

    public double getLoonAndereMaand() {
        return loonAndereMaand;
    }

    public void setLoonAndereMaand(double loonAndereMaand) {
        this.loonAndereMaand = loonAndereMaand;
    }

    public void addLoonAndereMaand(double loonAndereMaand) {
        this.loonAndereMaand += loonAndereMaand;
    }
}
