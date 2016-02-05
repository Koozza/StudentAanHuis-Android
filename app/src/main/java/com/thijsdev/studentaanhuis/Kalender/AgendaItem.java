package com.thijsdev.studentaanhuis.Kalender;

import com.thijsdev.studentaanhuis.Database.Afspraak;

import java.util.ArrayList;
import java.util.List;

public class AgendaItem {
    int hour = 0;
    List<Afspraak> afspraken = new ArrayList<>();


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public List<Afspraak> getAfspraken() {
        return afspraken;
    }

    public void setAfspraken(List<Afspraak> afspraken) {
        this.afspraken = afspraken;
    }

    public void addAfspraak(Afspraak afspraak) {
        this.afspraken.add(afspraak);
    }
}

