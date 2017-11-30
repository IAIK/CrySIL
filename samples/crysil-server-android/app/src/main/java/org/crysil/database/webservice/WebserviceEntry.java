package org.crysil.database.webservice;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.Random;

/**
 * Database entry for a webservice (used for WebVPN and as the main communication peer over Websockets)
 */
public class WebserviceEntry implements BaseColumns {

    public static final String TABLE_NAME = "webservices";

    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_HOSTNAME = "hostname";
    public static final String COLUMN_NAME_IP = "ip";
    public static final String COLUMN_NAME_PORT = "port";
    public static final String COLUMN_NAME_PATH = "path";
    public static final String COLUMN_NAME_ALIAS = "certalias";
    public static final String COLUMN_NAME_SKYTRUSTID = "skytrustid";
    public static final String COLUMN_NAME_STATUS = "status";

    public static final String[] ALL_COLUMNS = new String[]{WebserviceEntry._ID, WebserviceEntry.COLUMN_NAME_TITLE, WebserviceEntry.COLUMN_NAME_HOSTNAME, WebserviceEntry.COLUMN_NAME_IP, WebserviceEntry.COLUMN_NAME_PORT, WebserviceEntry.COLUMN_NAME_PATH, WebserviceEntry.COLUMN_NAME_SKYTRUSTID, WebserviceEntry.COLUMN_NAME_STATUS, WebserviceEntry.COLUMN_NAME_ALIAS};

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_HOSTNAME + " TEXT, " +
            COLUMN_NAME_IP + " TEXT, " +
            COLUMN_NAME_PORT + " INTEGER, " +
            COLUMN_NAME_PATH + " TEXT, " +
            COLUMN_NAME_TITLE + " TEXT, " +
            COLUMN_NAME_ALIAS + " TEXT, " +
            COLUMN_NAME_SKYTRUSTID + " TEXT, " +
            COLUMN_NAME_STATUS + " TEXT " +
            " )";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final int DEFAULT_PORT = 443;

    private String title;
    private String hostname;
    private String ip;
    private int port;
    private String path;
    private String alias;
    private String skytrustId;
    private WebserviceEntryStatus status;

    public WebserviceEntry(String title, String hostname, String ip, int port, String path) {
        this.title = title;
        this.hostname = hostname;
        this.ip = ip;
        this.port = port == -1 ? DEFAULT_PORT : port;
        this.path = path;
        this.alias = String.format("%s-%s", title, randomString(6));
        this.skytrustId = "";
        this.status = WebserviceEntryStatus.UNKNOWN;
    }

    private static String randomString(int length) {
        char[] chars1 = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars1[random1.nextInt(chars1.length)]);
        }
        return sb.toString();
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_HOSTNAME, hostname);
        values.put(COLUMN_NAME_IP, ip);
        values.put(COLUMN_NAME_PORT, port);
        values.put(COLUMN_NAME_PATH, path);
        values.put(COLUMN_NAME_TITLE, title);
        values.put(COLUMN_NAME_ALIAS, alias);
        values.put(COLUMN_NAME_SKYTRUSTID, skytrustId);
        values.put(COLUMN_NAME_STATUS, status.toString());
        return values;
    }

}
