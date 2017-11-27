package org.crysil.database.mappings;

import android.provider.BaseColumns;

import org.crysil.database.accounts.AbstractAccountInfo;
import org.crysil.database.keys.KeyEntry;

/**
 * Mapping of one account to one key (if this exists, the account can access the key)
 */
public class AccountKeyMappingEntry implements BaseColumns {

    public static final String TABLE_NAME = "mappings";

    public static final String COLUMN_NAME_ACCOUNT_ID = "account_id";
    
    public static final String COLUMN_NAME_KEY_ID = "key_id";

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_ACCOUNT_ID + " INTEGER REFERENCES " + AbstractAccountInfo.TABLE_NAME + " ON DELETE CASCADE, " +
            COLUMN_NAME_KEY_ID + " INTEGER REFERENCES " + KeyEntry.TABLE_NAME + " ON DELETE CASCADE " +
            " )";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String[] ALL_COLUMNS = new String[]{_ID, COLUMN_NAME_ACCOUNT_ID, COLUMN_NAME_KEY_ID};
}
