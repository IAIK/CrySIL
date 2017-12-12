package org.crysil.database.accounts;

import android.provider.BaseColumns;

/**
 * Table to hold the possible account types, needed for {@link org.crysil.utils.AccountCursorTreeAdapter}
 */
public class AccountTypes implements BaseColumns {

    public static final String TABLE_NAME = "account_types";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String COLUMN_NAME_TYPE = "type";

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_TYPE + " INTEGER " +
            " )";

    public static final String[] ALL_COLUMNS = new String[]{_ID, COLUMN_NAME_TYPE};
}
