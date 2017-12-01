package org.crysil.database.keys;

import android.provider.BaseColumns;

/**
 * Represents a key from the key store to map accounts to it.
 * Keys from the key store without an entry in the database are not visible to CrySIL.
 */
public class KeyEntry implements BaseColumns {

    public static final String TABLE_NAME = "keys";

    public static final String COLUMN_NAME_ALIAS = "alias";

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_ALIAS + " TEXT " +
            " )";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String[] ALL_COLUMNS = new String[]{_ID, COLUMN_NAME_ALIAS};
}
