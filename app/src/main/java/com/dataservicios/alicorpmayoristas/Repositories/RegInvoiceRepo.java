package com.dataservicios.alicorpmayoristas.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.dataservicios.alicorpmayoristas.Model.RegInvoice;
import com.dataservicios.alicorpmayoristas.SQLite.DatabaseHelper;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime on 28/11/2016.
 */

public class RegInvoiceRepo extends DatabaseHelper {
    private static final String LOG = RegInvoiceRepo.class.getSimpleName();

    public RegInvoiceRepo(Context context) {
        super(context);
    }


    public int update(RegInvoice regInvoice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, regInvoice.getId());
        // updating row
        return db.update(TABLE_REG_INVOICE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(regInvoice.getId()) });
    }

    /**
     *
     * @param regInvoice
     * @return
     */
    public long insert(RegInvoice regInvoice) {
        long todo_id;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        // values.put(KEY_ID, audit.getId());
        values.put(KEY_STORE_ID, regInvoice.getStore_id());
        values.put(KEY_NAME_FILE, regInvoice.getFile());
        values.put(KEY_MONTO, regInvoice.getMonto());
        values.put(KEY_RAZON_SOCIAL, regInvoice.getRazonSocial());

        todo_id = db.insert(TABLE_REG_INVOICE, null, values);
        db.close();
        return todo_id;
    }


    public List<RegInvoice> getAllRegInvoice() {
        List<RegInvoice> regInvoice = new ArrayList<RegInvoice>();
        String selectQuery = "SELECT  * FROM " + TABLE_REG_INVOICE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RegInvoice pd = new RegInvoice();
                int id = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ID)));
                pd.setId(id);
                pd.setStore_id(c.getInt(c.getColumnIndex(KEY_STORE_ID)));
                pd.setFile((c.getString(c.getColumnIndex(KEY_NAME_FILE))));
                pd.setMonto((c.getString(c.getColumnIndex(KEY_MONTO))));
                pd.setRazonSocial((c.getString(c.getColumnIndex(KEY_RAZON_SOCIAL))));

                regInvoice.add(pd);
            } while (c.moveToNext());
        }
        return regInvoice;
    }


    public RegInvoice getRegInvoice(long idRegInvoice) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_REG_INVOICE + " WHERE "
                + KEY_ID + " = " + idRegInvoice;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        RegInvoice pd = new RegInvoice();
        if (c.moveToFirst()) {

            int id = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ID)));
            pd.setId(id);
            pd.setStore_id(c.getInt(c.getColumnIndex(KEY_STORE_ID)));
            pd.setFile((c.getString(c.getColumnIndex(KEY_NAME_FILE))));
            pd.setMonto((c.getString(c.getColumnIndex(KEY_MONTO))));
            pd.setRazonSocial((c.getString(c.getColumnIndex(KEY_RAZON_SOCIAL))));

        }
        c.close();
        db.close();
        return pd;
    }


    public RegInvoice getFirstRegInvoice() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_REG_INVOICE  + " LIMIT 1  ";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        RegInvoice pd = new RegInvoice();
        if(c != null)
        {
            if (c.moveToFirst()) {
                int id = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ID)));
                pd.setId(id);
                pd.setStore_id(c.getInt(c.getColumnIndex(KEY_STORE_ID)));
                pd.setFile((c.getString(c.getColumnIndex(KEY_NAME_FILE))));
                pd.setMonto((c.getString(c.getColumnIndex(KEY_MONTO))));
                pd.setRazonSocial((c.getString(c.getColumnIndex(KEY_RAZON_SOCIAL))));

            }
        }

        c.close();
        db.close();
        return pd;
    }


    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REG_INVOICE, null, null );

    }


    public void deleteForId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REG_INVOICE, KEY_ID + " = ? ",  new String[] { String.valueOf(id) } );

    }

}

