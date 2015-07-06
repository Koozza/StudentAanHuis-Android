package com.thijsdev.studentaanhuis.Kalender;

import android.content.Context;
import android.content.SharedPreferences;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.Afspraak;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.Database.Klant;
import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.RetryCallbackFailure;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KalenderHelper {
    final KalenderHTTPHandler kalenderHTTPHandler = new KalenderHTTPHandler();
    DatabaseHandler databaseHandler;

    private Context context;
    private Date startDate;
    private Date stopDate;

    //Callbacks
    Callback itemAddedCallback, itemUpdatedCallback = null;

    //Temp Variables
    ArrayList<String> updatedKlanten = new ArrayList<>();

    public KalenderHelper(Context _context) {
        context = _context;
        databaseHandler = DatabaseHandler.getInstance(context);
    }

    public void getStartAndEndDate() {
        //Check Last Run
        SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
        String KalenderLastRun = sharedpreferences.getString("KalenderLastRun", "");


        if(KalenderLastRun.equals("")) {
            //Never ran before, run from first date in LoonList till current date + X time
            startDate = Calendar.getInstance().getTime();
            for(LoonMaand loonMaand : databaseHandler.getLoonMaanden()) {
                if(loonMaand.getDatum().before(startDate))
                    startDate = loonMaand.getDatum();
            }
        }else{
            //Ran before, run again from this date till current date + X time
            try {
                startDate = DatabaseHandler.databaseDateFormat.parse(KalenderLastRun);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Set stopDate
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        stopDate = cal.getTime();
    }

    public int countKalenderPages() {
        return weeksBetween(startDate, stopDate);
    }


    public void processKalenderItems(final Callback itemFinished, final Callback finished) {
        nextPage(startDate, 0, context, itemFinished, finished);
    }

    private void nextPage(final Date date, final int index, final Context context, final Callback itemFinished, final Callback callback) {
        kalenderHTTPHandler.getWeek(date, new Callback() {
            @Override
            public void onTaskCompleted(Object... results) {
                processPage((String) results[0], date, itemFinished);
                itemFinished.onTaskCompleted();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_MONTH, 7);
                if(cal.getTime().before(stopDate)) {
                    nextPage(cal.getTime(), index + 1, context, itemFinished, callback);
                } else {
                    //Set Last run
                    SharedPreferences sharedpreferences = context.getSharedPreferences("SAH_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putString("KalenderLastRun", "");
                    edit.commit();

                    callback.onTaskCompleted();
                }

            }
        }, new RetryCallbackFailure(10));
    }

    private void processPage(String source, Date date, Callback itemFinished) {
        Document doc = Jsoup.parse(source);
        Elements appointments = doc.getElementsByClass("appointment");

        //Get all appointments for this week
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 7);

        List<Afspraak> afsprakenThisWeek = databaseHandler.getAfsprakenBetween(date, cal.getTime());

        for(Element appointment : appointments) {
            if(appointment.getElementsByClass("customer").size() == 0)
                continue;

            Pattern p = Pattern.compile("(.+) \\((\\d+ \\d+)\\)");
            Matcher m = p.matcher(appointment.getElementsByClass("customer").get(0).text());
            m.find();

            //Create/Update customer
            Klant DBklant = databaseHandler.getKlant(m.group(2));
            Klant klant;
            if(DBklant == null) {
                klant = new Klant();
                klant.setNaam(m.group(1));
                klant.setKlantnummer(m.group(2));
            }else{
                klant = DBklant;
            }


            if(!updatedKlanten.contains(m.group(2)) || DBklant == null) {
                //format and set the adress
                String address = "";
                for (Element addressline : appointment.getElementsByClass("address")) {
                    if (!addressline.text().trim().equals(""))
                        address += addressline.text().trim() + " ";
                }
                klant.setAdres(address.trim());

                //set email if found
                if (appointment.getElementsByClass("email").size() > 0)
                    klant.setEmail(appointment.getElementsByClass("email").get(0).child(0).text());

                //set telephone numbers if found
                if (appointment.getElementsByClass("phone").size() > 0)
                    if (!appointment.getElementsByClass("phone").get(0).text().replace("(", "").replace(")", "").equals(""))
                        klant.setTel1(appointment.getElementsByClass("phone").get(0).text().replace("(", "").replace(")", ""));

                if (appointment.getElementsByClass("phone").size() > 1)
                    if (!appointment.getElementsByClass("phone").get(1).text().replace("(", "").replace(")", "").equals(""))
                        klant.setTel2(appointment.getElementsByClass("phone").get(1).text().replace("(", "").replace(")", ""));

                updatedKlanten.add(klant.getKlantnummer());
            }

            if(DBklant == null)
                databaseHandler.addKlant(klant);
            else
                databaseHandler.updateKlant(klant);

            //Process appointment
            Afspraak afspraak = null;
            String[] times = appointment.child(0).text().split("-");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int test = appointment.siblingIndex();

            for(Afspraak a : afsprakenThisWeek) {
                //Controleren of deze afspraak er al is; zo ja: removen uit deze lijst. Alles wat over is; wordt geremoved (is kennelijk aangepast of verplaatst).
                if(a.getKlant().getKlantnummer() == klant.getKlantnummer() && timeFormat.format(a.getStart()).equals(times[0].trim())) {
                    //Deze a afspraak bestaat al. Removen uit array, als gevonden zetten en stoppen met zoeken.
                    afspraak = a;
                    break;
                }
            }

            boolean isUpdate = false;
            if(afspraak != null) {
                isUpdate = true;
                afsprakenThisWeek.remove(afspraak);
            } else {
                afspraak = new Afspraak();
                afspraak.setKlant(klant);
            }

            //Set or update fields
            if (appointment.getElementsByClass("pin").size() > 0)
                if(!appointment.getElementsByClass("pin").get(0).text().equals("PIN:"))
                    afspraak.setPin(appointment.getElementsByClass("pin").get(0).text().split(" ")[1]);

            afspraak.setTags(appointment.getElementsByTag("div").get(appointment.getElementsByTag("div").size()-2).text());
            afspraak.setOmschrijving(appointment.getElementsByTag("div").get(appointment.getElementsByTag("div").size() - 1).text());
            try {
                Calendar afspraakDatum = Calendar.getInstance();
                afspraakDatum.setTime(date);
                afspraakDatum.add(Calendar.DAY_OF_MONTH, (appointment.siblingIndex() + 1) / 2 - 1); //Kennelijk is sibling index dag * 2 - 1

                afspraak.setStart(DatabaseHandler.databaseDateFormat.parse(dateFormat.format(afspraakDatum.getTime()) + " " + times[0].trim() + ":00"));
                afspraak.setEnd(DatabaseHandler.databaseDateFormat.parse(dateFormat.format(afspraakDatum.getTime()) + " " + times[1].trim() + ":00"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(isUpdate)
                databaseHandler.updateAfspraak(afspraak);
            else
                databaseHandler.addAfspraak(afspraak);
        }

        //Delete all moved / removed appointments
        for(Afspraak a : afsprakenThisWeek) {
            databaseHandler.deleteAfspraak(a);
        }
    }

    private Calendar getDatePart(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    private int weeksBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        int weeksBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 7);
            weeksBetween++;
        }
        return weeksBetween;
    }
}
