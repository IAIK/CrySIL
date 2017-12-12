package org.crysil.database.accounts;

import android.content.ContentValues;

/**
 * Account: Google mail address (OAuth)
 */
public class AccountGoogleEntry extends AbstractAccountInfo {

    public static final String TABLE_NAME = "google_account_infos";

    public static final String COLUMN_NAME_GMAIL_ADDRESS = "gmail_address";

    public static final String STMT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY REFERENCES " + AbstractAccountInfo.TABLE_NAME + " ON DELETE CASCADE, " +
            COLUMN_NAME_GMAIL_ADDRESS + " TEXT" +
            " )";

    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String[] ALL_COLUMNS = new String[]{_ID, COLUMN_NAME_GMAIL_ADDRESS};

    private String gmailAddress;

    public AccountGoogleEntry(String gmailAddress) {
        type = AccountInfoType.GOOGLE_ACCOUNT;
        this.gmailAddress = gmailAddress;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = super.getContentValues();
        values.put(COLUMN_NAME_GMAIL_ADDRESS, gmailAddress);
        return values;
    }
}
