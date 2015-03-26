package com.thijsdev.studentaanhuis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SAHInfo";
    private static final String TABLE_PITEMS = "PrikbordItems";
    private static final String TABLE_WERKGEBIEDEN = "Werkgebieden";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PITEMS + "(id INTEGER PRIMARY KEY,adres TEXT,beschrijving TEXT,type TEXT,deadline DATETIME,beschikbaar INTEGER,lat REAL,lng REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);


        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WERKGEBIEDEN + "(id INTEGER PRIMARY KEY,naam TEXT,adres TEXT,straal TEXT,actief INTEGER,lat REAL,lng REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PITEMS);
        onCreate(db);
    }

    /**
     * All CRUD functions for Prikbord Items
     */

    //New item
    void addPrikbordItem(PrikbordItem item) {
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
    PrikbordItem getPrikbordItem(int id) {
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
    List<PrikbordItem> getPrikbordItems() {
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
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }

    /**
     * All CRUD functions for Werkgebieden
     */

    //New item
    void addWerkgebied(Werkgebied item) {
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
    Werkgebied getWerkgebied(int id) {
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
    List<Werkgebied> getWerkgebieden() {
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
    List<Werkgebied> getActiveWerkgebieden() {
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
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }
}
