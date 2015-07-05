package com.thijsdev.studentaanhuis.Kalender;

import android.content.Context;
import android.content.SharedPreferences;

import com.thijsdev.studentaanhuis.Callback;
import com.thijsdev.studentaanhuis.Database.DatabaseHandler;
import com.thijsdev.studentaanhuis.Database.Klant;
import com.thijsdev.studentaanhuis.Database.LoonMaand;
import com.thijsdev.studentaanhuis.RetryCallbackFailure;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
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

    public KalenderHelper(Context _context) {
        context = _context;
        databaseHandler = DatabaseHandler.getInstance(context);

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

    public int countLoonItems() {
        return weeksBetween(startDate, stopDate);
    }


    public void processKalenderItems(final Callback itemFinished, final Callback finished) {
        nextPage(startDate, 0, context, itemFinished, finished);
    }

    private void nextPage(final Date date, final int index, final Context context, final Callback itemFinished, final Callback callback) {
        if(date.before(stopDate)) {
            kalenderHTTPHandler.getWeek(date, new Callback() {
                @Override
                public void onTaskCompleted(Object... results) {
                    processPage((String) results[0], date, itemFinished);

                    //isFinalLoonUpdate(totalItems, callback);
                    itemFinished.onTaskCompleted();

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.add(Calendar.DAY_OF_MONTH, 7);
                    nextPage(cal.getTime(), index + 1, context, itemFinished, callback);
                }
            }, new RetryCallbackFailure(10));
        }
    }

    private void processPage(String source, Date date, Callback itemFinished) {
        Document doc = Jsoup.parse(source);
        Elements appointments = doc.getElementsByClass("appointment");

        for(Element appointment : appointments) {
            if(appointment.getElementsByClass("customer").size() == 0)
                continue;

                Pattern p = Pattern.compile("(.+) \\((\\d+ \\d+)\\)");
                Matcher m = p.matcher(appointment.getElementsByClass("customer").get(0).text());
                m.find();

                //Create customer if it didn't exist
                if(databaseHandler.getKlant(m.group(2)) == null) {
                    Klant klant = new Klant();
                    klant.setNaam(m.group(1));
                    klant.setKlantnummer(m.group(2));

                    //format and set the adress
                    String address = "";
                    for(Element addressline : appointment.getElementsByClass("address")) {
                        if(!addressline.text().trim().equals(""))
                            address += addressline.text().trim() + " ";
                    }
                    klant.setAdres(address.trim());

                    //set email if found
                    if(appointment.getElementsByClass("email").size() > 0)
                        klant.setEmail(appointment.getElementsByClass("email").get(0).child(0).text());

                    //set telephone numbers if found
                    if(appointment.getElementsByClass("phone").size() > 0)
                        if(!appointment.getElementsByClass("phone").get(0).text().replace("(","").replace(")","").equals(""))
                            klant.setTel1(appointment.getElementsByClass("phone").get(0).text().replace("(","").replace(")",""));

                    if(appointment.getElementsByClass("phone").size() > 1)
                        if(!appointment.getElementsByClass("phone").get(1).text().replace("(","").replace(")","").equals(""))
                            klant.setTel2(appointment.getElementsByClass("phone").get(1).text().replace("(", "").replace(")", ""));

                    databaseHandler.addKlant(klant);
                }
        }
    }

    private Calendar getDatePart(Date date){
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    private int weeksBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        int daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 7);
            daysBetween++;
        }
        return daysBetween;
    }
}
