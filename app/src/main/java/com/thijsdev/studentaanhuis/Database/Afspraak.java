package com.thijsdev.studentaanhuis.Database;

import java.util.Date;

public class Afspraak implements DatabaseObject {
    private int id;
    private Klant klant;
    private String tags;
    private String omschrijving;
    private String pin;
    private Date start;
    private Date end;

    //Afhandeling
    private String werkbonNummer;
    private Date ingedient;
    private Date goedgekeurd;
    private Date salarisronde;
    private String type; // IDeal, Werkbon

    //Loon
    private float uurloon;
    private float voorfietskosten;


    @Override
    public int getId() {
        return id;
    }

    @Override
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getSalarisronde() {
        return salarisronde;
    }

    public void setSalarisronde(Date salarisronde) {
        this.salarisronde = salarisronde;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
