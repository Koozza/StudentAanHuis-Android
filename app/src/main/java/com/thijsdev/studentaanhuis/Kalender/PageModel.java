package com.thijsdev.studentaanhuis.Kalender;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PageModel {

    private int index;
    private Date datum;
    private String klant;

    public Context context;
    public TextView kalenderDatum;
    public LinearLayoutManager mLayoutManager;
    public RecyclerView recyclerView;
    public KalenderAdapter kalenderAdapter;
    public LinearLayout layout;


    public PageModel(int index) {
        this.index = index;
        setIndex(index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        setDatum(index);
        setKlant(String.valueOf(index));
    }

    public String getDatum(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, new Locale("nl", "NL"));
        return dateFormat.format(datum);
    }

    private void setDatum(int index) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, index);
        datum = c.getTime();
    }

    public String getKlant() {
        return klant;
    }

    public void setKlant(String klant) {
        this.klant = klant;
    }
}