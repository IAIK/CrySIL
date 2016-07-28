package org.crysil.instance.u2f.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.crysil.instance.u2f.database.webservice.WebserviceEntry;
import org.crysil.instance.u2f.database.webservice.WebserviceEntryStatus;

/**
 * Used to handle all access to the database (Webservices)
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String PLACEHOLDER = "=?";
    private static final String DATABASE_NAME = "Crysil.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(WebserviceEntry.STMT_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void insertWebserviceInfo(WebserviceEntry entry) {
        ContentValues values = entry.getContentValues();
        getWritableDatabase().insert(WebserviceEntry.TABLE_NAME, null, values);
    }

    public boolean updateWebserviceInfo(Long id, String crysilId, WebserviceEntryStatus status) {
        ContentValues values = new ContentValues();
        values.put(WebserviceEntry.COLUMN_NAME_CRYSILID, crysilId);
        values.put(WebserviceEntry.COLUMN_NAME_STATUS, status.toString());
        return getWritableDatabase().update(WebserviceEntry.TABLE_NAME, values, WebserviceEntry._ID + PLACEHOLDER,
                new String[]{id.toString()}) == 1;
    }

    public boolean updateWebserviceInfo(Long id, String title, String address) {
        ContentValues values = new ContentValues();
        values.put(WebserviceEntry.COLUMN_NAME_TITLE, title);
        values.put(WebserviceEntry.COLUMN_NAME_HOSTNAME, address);
        return getWritableDatabase().update(WebserviceEntry.TABLE_NAME, values, WebserviceEntry._ID + PLACEHOLDER,
                new String[]{id.toString()}) == 1;
    }

    public int deleteWebserviceInfo(long id) {
        return getWritableDatabase().delete(WebserviceEntry.TABLE_NAME, WebserviceEntry._ID + PLACEHOLDER,
                new String[]{Long.toString(id)});
    }

    public Cursor getWebserviceInfo(long id) {
        return getReadableDatabase().query(WebserviceEntry.TABLE_NAME, WebserviceEntry.ALL_COLUMNS,
                WebserviceEntry._ID + PLACEHOLDER, new String[]{Long.toString(id)}, null, null,
                WebserviceEntry.COLUMN_NAME_TITLE, null);
    }

    public Cursor getWebserviceInfoForIP(String ip) {
        return getReadableDatabase().query(WebserviceEntry.TABLE_NAME, WebserviceEntry.ALL_COLUMNS,
                WebserviceEntry.COLUMN_NAME_IP + PLACEHOLDER, new String[]{ip}, null, null,
                WebserviceEntry.COLUMN_NAME_TITLE, null);
    }

    public Cursor getWebserviceInfoForHostname(String hostname) {
        return getReadableDatabase().query(WebserviceEntry.TABLE_NAME, WebserviceEntry.ALL_COLUMNS,
                WebserviceEntry.COLUMN_NAME_HOSTNAME + PLACEHOLDER, new String[]{hostname}, null, null,
                WebserviceEntry.COLUMN_NAME_TITLE, null);
    }

    public Cursor getWebserviceCursor() {
        return getReadableDatabase().query(WebserviceEntry.TABLE_NAME, WebserviceEntry.ALL_COLUMNS, null, null, null,
                null, WebserviceEntry.COLUMN_NAME_TITLE, null);
    }
}
