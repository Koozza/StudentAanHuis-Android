package com.thijsdev.studentaanhuis.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper  {
    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "SAHInfo";
    private static final String TABLE_PITEMS = "PrikbordItems";
    private static final String TABLE_WERKGEBIEDEN = "Werkgebieden";
    private static final String TABLE_LOONMAAND = "LoonMaand";
    private static final String TABLE_KLANTEN = "Klanten";
    private static final String TABLE_AFSPRAKEN = "Afspraken";

    private static DatabaseHandler mInstance = null;


    public static final DateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context);
        }
        return mInstance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WERKGEBIEDEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOONMAAND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KLANTEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AFSPRAKEN);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PITEMS + "(id INTEGER PRIMARY KEY,adres TEXT,beschrijving TEXT,type TEXT,deadline DATETIME,beschikbaar INTEGER,lat REAL,lng REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);


        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WERKGEBIEDEN + "(id INTEGER PRIMARY KEY,naam TEXT,adres TEXT,straal TEXT,actief INTEGER,lat REAL,lng REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);


        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOONMAAND + "(_id INTEGER PRIMARY KEY, naam TEXT, isuitbetaald Integer, iscompleet Integer, datum DATETIME, loon REAL, mogelijkloon REAL, loonanderemaand REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);


        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_KLANTEN + "(_id INTEGER PRIMARY KEY, klantnummer TEXT, naam TEXT, adres TEXT, email TEXT, tel1 TEXT, tel2 TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);


        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_AFSPRAKEN + "(_id INTEGER PRIMARY KEY, klant TEXT, omschrijving TEXT, tags TEXT, pin TEXT, start DATETIME, end DATETIME)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    public void RecreateDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        dropTables(db);
        createTables(db);
    }


    public void clearDatabase()
    {
        deleteAllPrikbordItems();
        deleteAllWerkgebieden();
        deleteAllLoonMaandItems();
        deleteAllKlanten();
    }

    /**
     * All CRUD functions for Prikbord Items
     */

    //New item
    public void addPrikbordItem(PrikbordItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", item.getId());
        values.put("beschrijving", item.getBeschrijving());
        values.put("type", item.getType());
        values.put("adres", item.getAdres());
        values.put("deadline", item.getDeadline());
        values.put("beschikbaar", item.getBeschikbaar());
        values.put("lat", item.getLat());
        values.put("lng", item.getLng());

        // Inserting Row
        db.insert(TABLE_PITEMS, null, values);
        db.close();
    }

    // Get Single Item
    public PrikbordItem getPrikbordItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PITEMS, new String[] { "id",
                        "adres", "beschrijving", "type" , "deadline" , "beschikbaar", "lat" , "lng" }, "id=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            return null;

        PrikbordItem item = new PrikbordItem();
        item.setId(cursor.getInt(0));
        item.setAdres(cursor.getString(1));
        item.setBeschrijving(cursor.getString(2));
        item.setType(cursor.getString(3));
        item.setDeadline(cursor.getString(4));
        item.setBeschikbaar(cursor.getInt(5));
        item.setLat(cursor.getDouble(6));
        item.setLng(cursor.getDouble(7));

        return item;
    }

    // Get All Items
    public List<PrikbordItem> getPrikbordItems() {
        List<PrikbordItem> prikbordItemsList = new ArrayList<PrikbordItem>();
        String selectQuery = "SELECT  * FROM " + TABLE_PITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PrikbordItem item = new PrikbordItem();
                item.setId(cursor.getInt(0));
                item.setAdres(cursor.getString(1));
                item.setBeschrijving(cursor.getString(2));
                item.setType(cursor.getString(3));
                item.setDeadline(cursor.getString(4));
                item.setBeschikbaar(cursor.getInt(5));
                item.setLat(cursor.getDouble(6));
                item.setLng(cursor.getDouble(7));

                prikbordItemsList.add(item);
            } while (cursor.moveToNext());
        }


        return prikbordItemsList;
    }
    // Updating item
    public int updatePrikbordItem(PrikbordItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("adres", item.getAdres());
        values.put("beschrijving", item.getBeschrijving());
        values.put("type", item.getType());
        values.put("deadline", item.getDeadline());
        values.put("beschikbaar", item.getBeschikbaar());
        values.put("lat", item.getLat());
        values.put("lng", item.getLng());

        return db.update(TABLE_PITEMS, values, "id = ?",
                new String[] { String.valueOf(item.getId()) });
    }

    // Deleting item
    public void deletePrikbordItem(PrikbordItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PITEMS, "id = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Deleting all items
    public void deleteAllPrikbordItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PITEMS);
        db.close();
    }

    /**
     * All CRUD functions for Werkgebieden
     */

    //New item
    public void addWerkgebied(Werkgebied item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", item.getId());
        values.put("naam", item.getNaam());
        values.put("adres", item.getAdres());
        values.put("straal", item.getStraal());
        values.put("actief", item.getActief());
        values.put("lat", item.getLat());
        values.put("lng", item.getLng());

        // Inserting Row
        db.insert(TABLE_WERKGEBIEDEN, null, values);
        db.close();
    }

    // Get Single Item
    public Werkgebied getWerkgebied(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WERKGEBIEDEN, new String[] { "id",
                        "naam", "adres", "straal" , "actief" , "lat" , "lng" }, "id=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            return null;

        Werkgebied item = new Werkgebied();
        item.setId(cursor.getInt(0));
        item.setNaam(cursor.getString(1));
        item.setAdres(cursor.getString(2));
        item.setStraal(cursor.getString(3));
        item.setActief(cursor.getInt(4));
        item.setLat(cursor.getDouble(5));
        item.setLng(cursor.getDouble(6));

        return item;
    }

    // Get All Items
    public List<Werkgebied> getWerkgebieden() {
        List<Werkgebied> werkgebiedList = new ArrayList<Werkgebied>();
        String selectQuery = "SELECT  * FROM " + TABLE_WERKGEBIEDEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Werkgebied item = new Werkgebied();
                item.setId(cursor.getInt(0));
                item.setNaam(cursor.getString(1));
                item.setAdres(cursor.getString(2));
                item.setStraal(cursor.getString(3));
                item.setActief(cursor.getInt(4));
                item.setLat(cursor.getDouble(5));
                item.setLng(cursor.getDouble(6));

                werkgebiedList.add(item);
            } while (cursor.moveToNext());
        }


        return werkgebiedList;
    }

    // Get Active All Items
    public List<Werkgebied> getActiveWerkgebieden() {
        List<Werkgebied> werkgebiedList = new ArrayList<Werkgebied>();
        String selectQuery = "SELECT  * FROM " + TABLE_WERKGEBIEDEN + " WHERE actief=1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Werkgebied item = new Werkgebied();
                item.setId(cursor.getInt(0));
                item.setNaam(cursor.getString(1));
                item.setAdres(cursor.getString(2));
                item.setStraal(cursor.getString(3));
                item.setActief(cursor.getInt(4));
                item.setLat(cursor.getDouble(5));
                item.setLng(cursor.getDouble(6));

                werkgebiedList.add(item);
            } while (cursor.moveToNext());
        }


        return werkgebiedList;
    }
    // Updating item
    public int updateWerkgebied(Werkgebied item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("naam", item.getNaam());
        values.put("adres", item.getAdres());
        values.put("straal", item.getStraal());
        values.put("actief", item.getActief());
        values.put("lat", item.getLat());
        values.put("lng", item.getLng());

        return db.update(TABLE_WERKGEBIEDEN, values, "id = ?",
                new String[] { String.valueOf(item.getId()) });
    }

    // Deleting item
    public void deleteWerkgebied(Werkgebied item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WERKGEBIEDEN, "id = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Deleting all items
    public void deleteAllWerkgebieden() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_WERKGEBIEDEN);
        db.close();
    }

    /**
     * All CRUD functions for Loon Maand
     */

    //New item
    public void addLoonMaand(LoonMaand item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("naam", item.getNaam());
        values.put("isuitbetaald", item.isUitbetaald());
        values.put("iscompleet", item.isCompleet());
        values.put("datum", databaseDateFormat.format(item.getDatum()));
        values.put("loon", item.getLoon());
        values.put("mogelijkloon", item.getLoonMogelijk());
        values.put("loonanderemaand", item.getLoonAndereMaand());

        // Inserting Row
        db.insert(TABLE_LOONMAAND, null, values);
        db.close();
    }

    // Get Single Item
    public LoonMaand getLoonMaand(String naam) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOONMAAND, new String[] { "_id",
                        "naam", "isuitbetaald", "iscompleet", "datum", "loon", "mogelijkloon", "loonanderemaand" }, "naam=?",
                new String[] { naam }, null, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            return null;

        LoonMaand item = new LoonMaand();
        item.setId(cursor.getInt(0));
        item.setNaam(cursor.getString(1));
        item.setIsUitbetaald(cursor.getInt(2) == 1);
        item.setIsCompleet(cursor.getInt(3) == 1);
        try {
            item.setDatum(databaseDateFormat.parse(cursor.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        item.setLoon(cursor.getDouble(5));
        item.setLoonMogelijk(cursor.getDouble(6));
        item.setLoonAndereMaand(cursor.getDouble(7));

        return item;
    }

    // Get All Items
    public List<LoonMaand> getLoonMaanden() {
        List<LoonMaand> loonmaandList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOONMAAND;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LoonMaand item = new LoonMaand();
                item.setId(cursor.getInt(0));
                item.setNaam(cursor.getString(1));
                item.setIsUitbetaald(cursor.getInt(2) == 1);
                item.setIsCompleet(cursor.getInt(3) == 1);
                try {
                    item.setDatum(databaseDateFormat.parse(cursor.getString(4)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setLoon(cursor.getDouble(5));
                item.setLoonMogelijk(cursor.getDouble(6));
                item.setLoonAndereMaand(cursor.getDouble(7));

                loonmaandList.add(item);
            } while (cursor.moveToNext());
        }


        return loonmaandList;
    }
    // Updating item
    public int updateLoonMaand(LoonMaand item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("naam", item.getNaam());
        values.put("isuitbetaald", item.isUitbetaald());
        values.put("iscompleet", item.isCompleet());
        values.put("datum", databaseDateFormat.format(item.getDatum()));
        values.put("loon", item.getLoon());
        values.put("mogelijkloon", item.getLoonMogelijk());
        values.put("loonanderemaand", item.getLoonAndereMaand());

        return db.update(TABLE_LOONMAAND, values, "naam = ?",
                new String[] { String.valueOf(item.getNaam()) });
    }

    // Deleting all items
    public void deleteAllLoonMaandItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_LOONMAAND);
        db.close();
    }

    /**
     * All CRUD functions for Klanten
     */

    //New item
    public void addKlant(Klant item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("klantnummer", item.getKlantnummer());
        values.put("naam", item.getNaam());
        values.put("adres", item.getAdres());
        values.put("email", item.getEmail());
        values.put("tel1", item.getTel1());
        values.put("tel2", item.getTel2());

        // Inserting Row
        db.insert(TABLE_KLANTEN, null, values);
        db.close();
    }

    // Get Single Item
    public Klant getKlant(String klantnummer) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_KLANTEN, new String[]{"_id",
                        "klantnummer", "naam", "adres", "email", "tel1", "tel2"}, "klantnummer=?",
                new String[]{klantnummer}, null, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            return null;

        Klant item = new Klant();
        item.setId(cursor.getInt(0));
        item.setKlantnummer(cursor.getString(1));
        item.setNaam(cursor.getString(2));
        item.setAdres(cursor.getString(3));
        item.setEmail(cursor.getString(4));
        item.setTel1(cursor.getString(5));
        item.setTel2(cursor.getString(6));

        return item;
    }

    // Get All Items
    public List<Klant> getKlanten() {
        List<Klant> klantList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_KLANTEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Klant item = new Klant();
                item.setId(cursor.getInt(0));
                item.setKlantnummer(cursor.getString(1));
                item.setNaam(cursor.getString(2));
                item.setAdres(cursor.getString(3));
                item.setEmail(cursor.getString(4));
                item.setTel1(cursor.getString(5));
                item.setTel2(cursor.getString(6));

                klantList.add(item);
            } while (cursor.moveToNext());
        }


        return klantList;
    }
    // Updating item
    public int updateKlant(Klant item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("klantnummer", item.getKlantnummer());
        values.put("naam", item.getNaam());
        values.put("adres", item.getAdres());
        values.put("email", item.getEmail());
        values.put("tel1", item.getTel1());
        values.put("tel2", item.getTel2());

        return db.update(TABLE_KLANTEN, values, "klantnummer = ?",
                new String[] { String.valueOf(item.getKlantnummer()) });
    }

    // Deleting all items
    public void deleteAllKlanten() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_KLANTEN);
        db.close();
    }

    /**
     * All CRUD functions for Afspraken
     */

    //New item
    public void addAfspraak(Afspraak item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("klant", item.getKlant().getKlantnummer());
        values.put("omschrijving", item.getOmschrijving());
        values.put("tags", item.getTags());
        values.put("pin", item.getPin());
        values.put("start", databaseDateFormat.format(item.getStart()));
        values.put("end", databaseDateFormat.format(item.getEnd()));

        // Inserting Row
        db.insert(TABLE_AFSPRAKEN, null, values);
        db.close();
    }

    // Get Single Item
    public Afspraak getAfspraak(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AFSPRAKEN, new String[] { "_id",
                        "klant", "omschrijving", "tags", "pin", "start" , "end" }, "_id=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            return null;

        Afspraak item = new Afspraak();
        item.setId(cursor.getInt(0));
        item.setKlant(getKlant(cursor.getString(1)));
        item.setOmschrijving(cursor.getString(2));
        item.setTags(cursor.getString(3));
        item.setPin(cursor.getString(4));
        try {
            item.setStart(databaseDateFormat.parse(cursor.getString(5)));
            item.setEnd(databaseDateFormat.parse(cursor.getString(6)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return item;
    }

    // Get All Items
    public List<Afspraak> getAfspraken() {
        List<Afspraak> afsprakenList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_AFSPRAKEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Afspraak item = new Afspraak();
                item.setId(cursor.getInt(0));
                item.setKlant(getKlant(cursor.getString(1)));
                item.setOmschrijving(cursor.getString(2));
                item.setTags(cursor.getString(3));
                item.setPin(cursor.getString(4));
                try {
                    item.setStart(databaseDateFormat.parse(cursor.getString(5)));
                    item.setEnd(databaseDateFormat.parse(cursor.getString(6)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                afsprakenList.add(item);
            } while (cursor.moveToNext());
        }


        return afsprakenList;
    }

    // Get Single Item by Date
    public List<Afspraak> getAfsprakenForDate(String date) {
        List<Afspraak> afsprakenList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+TABLE_AFSPRAKEN+" WHERE start BETWEEN \""+date+" 00:00:00\" AND \""+date+" 23:59:59\";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Afspraak item = new Afspraak();
                item.setId(cursor.getInt(0));
                item.setKlant(getKlant(cursor.getString(1)));
                item.setOmschrijving(cursor.getString(2));
                item.setTags(cursor.getString(3));
                item.setPin(cursor.getString(4));
                try {
                    item.setStart(databaseDateFormat.parse(cursor.getString(5)));
                    item.setEnd(databaseDateFormat.parse(cursor.getString(6)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                afsprakenList.add(item);
            } while (cursor.moveToNext());
        }


        return afsprakenList;
    }

    // Get All Items
    public List<Afspraak> getAfsprakenBetween(Date date1, Date date2) {
        List<Afspraak> afsprakenList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_AFSPRAKEN + " WHERE start BETWEEN \"" + databaseDateFormat.format(date1) + "\" AND \"" + databaseDateFormat.format(date2) + "\";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Afspraak item = new Afspraak();
                item.setId(cursor.getInt(0));
                item.setKlant(getKlant(cursor.getString(1)));
                item.setOmschrijving(cursor.getString(2));
                item.setTags(cursor.getString(3));
                item.setPin(cursor.getString(4));
                try {
                    item.setStart(databaseDateFormat.parse(cursor.getString(5)));
                    item.setEnd(databaseDateFormat.parse(cursor.getString(6)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                afsprakenList.add(item);
            } while (cursor.moveToNext());
        }


        return afsprakenList;
    }

    // Updating item
    public int updateAfspraak(Afspraak item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("klant", item.getKlant().getKlantnummer());
        values.put("omschrijving", item.getOmschrijving());
        values.put("tags", item.getTags());
        values.put("pin", item.getPin());
        values.put("start", databaseDateFormat.format(item.getStart()));
        values.put("end", databaseDateFormat.format(item.getEnd()));

        return db.update(TABLE_AFSPRAKEN, values, "_id = ?",
                new String[] { String.valueOf(item.getId()) });
    }

    // Deleting item
    public void deleteAfspraak(Afspraak item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AFSPRAKEN, "_id = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Deleting all items
    public void deleteAllAfspraken() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_AFSPRAKEN);
        db.close();
    }
}
