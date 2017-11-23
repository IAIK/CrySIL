package at.tugraz.iaik.skytrust.database.accounts;

import android.content.ContentValues;
import android.provider.BaseColumns;

/**
 * Base class for the different account types, like OAuth or username/password
 */
public abstract class AbstractAccountInfo implements BaseColumns {

    public static final String TABLE_NAME = "accounts";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String COLUMN_NAME_ENTRY_TYPE = "entry_type";

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_ENTRY_TYPE + " INTEGER " +
            " )";
    
    private long id;
    protected AccountInfoType type;

    public void setId(long id) {
        this.id = id;
    }

    public AccountInfoType getType() {
        return type;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(_ID, id);
        return values;
    }
}