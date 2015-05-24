package com.thijsdev.studentaanhuis.Database;

import java.util.Date;

public class Afspraak {
    private int id;
    private Klant klant;
    private String omschrijving;
    private Date datum;

    //Afhandeling
    private String werkbonNummer;
    private Date ingedient;
    private Date goedgekeurd;

    //Loon
    private float uurloon;
    private float voorfietskosten;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Klant getKlant() {
        return klant;
    }

    public void setKlant(Klant klant) {
        this.klant = klant;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getWerkbonNummer() {
        return werkbonNummer;
    }

    public void setWerkbonNummer(String werkbonNummer) {
        this.werkbonNummer = werkbonNummer;
    }

    public Date getIngedient() {
        return ingedient;
    }

    public void setIngedient(Date ingedient) {
        this.ingedient = ingedient;
    }

    public Date getGoedgekeurd() {
        return goedgekeurd;
    }

    public void setGoedgekeurd(Date goedgekeurd) {
        this.goedgekeurd = goedgekeurd;
    }

    public float getUurloon() {
        return uurloon;
    }

    public void setUurloon(float uurloon) {
        this.uurloon = uurloon;
    }

    public float getVoorfietskosten() {
        return voorfietskosten;
    }

    public void setVoorfietskosten(float voorfietskosten) {
        this.voorfietskosten = voorfietskosten;
    }
}
