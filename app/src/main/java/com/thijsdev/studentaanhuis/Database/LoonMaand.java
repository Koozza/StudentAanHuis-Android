package com.thijsdev.studentaanhuis.Database;

import java.util.Date;

public class LoonMaand {
    private int id;
    private String naam;
    private boolean isCompleet;
    private Date datum;

    //Numbers
    private double loonZeker = 0d, loonMogelijk = 0d;

    public int getId() {
        return id;
    }

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

    public double getLoonZeker() {
        return loonZeker;
    }

    public void setLoonZeker(double loonZeker) {
        this.loonZeker = loonZeker;
    }


    public void addLoonZeker(double loonZeker) {
        this.loonZeker += loonZeker;
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

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
}
