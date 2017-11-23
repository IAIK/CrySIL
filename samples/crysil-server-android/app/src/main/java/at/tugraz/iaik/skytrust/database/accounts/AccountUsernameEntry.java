package at.tugraz.iaik.skytrust.database.accounts;

import android.content.ContentValues;

/**
 * Account: Username and password
 */
public class AccountUsernameEntry extends AbstractAccountInfo {

    public static final String TABLE_NAME = "username_password_account_infos";

    public static final String COLUMN_NAME_USERNAME = "username";

    public static final String COLUMN_NAME_PASSWORD = "password";

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY REFERENCES " + AbstractAccountInfo.TABLE_NAME + " ON DELETE CASCADE, " +
            COLUMN_NAME_USERNAME + " TEXT," +
            COLUMN_NAME_PASSWORD + " TEXT" +
            " )";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String[] ALL_COLUMNS = new String[]{_ID, COLUMN_NAME_USERNAME, COLUMN_NAME_PASSWORD};

    private String username;
    private String password;

    public AccountUsernameEntry(String username, String password) {
        //TODO: Store a hash and salt instead of the plain password
        this.username = username;
        this.password = password;
        type = AccountInfoType.USERNAME_ACCOUNT;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();
        values.put(COLUMN_NAME_USERNAME, username);
        values.put(COLUMN_NAME_PASSWORD, password);
        return values;
    }
}

