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

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PITEMS + "(id INTEGER PRIMARY KEY,adres TEXT,beschrijving TEXT,type TEXT,deadline DATETIME,beschikbaar INTEGER,lat REAL,lng REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PITEMS);
        onCreate(db);
    }

    /**
     * All CRUD for Prikbord Items
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

        // Inserting Row
        db.insert(TABLE_PITEMS, null, values);
        db.close();
    }

    // Get Single Item
    PrikbordItem getPrikbordItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PITEMS, new String[] { "id",
                        "adres", "beschrijving", "type" , "deadline" , "beschikbaar" }, "id=?",
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

        return item;
    }

    // Get All Items
    List<PrikbordItem> getPrikbordItems() {
        List<PrikbordItem> contactList = new ArrayList<PrikbordItem>();
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

                contactList.add(item);
            } while (cursor.moveToNext());
        }


        return contactList;
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
}
