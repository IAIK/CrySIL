package at.tugraz.iaik.skytrust.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.iaik.skytrust.database.accounts.AbstractAccountInfo;
import at.tugraz.iaik.skytrust.database.accounts.AccountGoogleEntry;
import at.tugraz.iaik.skytrust.database.accounts.AccountInfoType;
import at.tugraz.iaik.skytrust.database.accounts.AccountTypes;
import at.tugraz.iaik.skytrust.database.accounts.AccountUsernameEntry;
import at.tugraz.iaik.skytrust.database.keys.KeyEntry;
import at.tugraz.iaik.skytrust.database.mappings.AccountKeyMappingEntry;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntry;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntryStatus;

/**
 * Used to handle all access to the database (Webservices, keys, accounts, mappings for them)
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String PLACEHOLDER = "=?";
    private static final String DATABASE_NAME = "Skytrust.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AbstractAccountInfo.STMT_CREATE_TABLE);
        sqLiteDatabase.execSQL(AccountGoogleEntry.STMT_CREATE_TABLE);
        sqLiteDatabase.execSQL(AccountUsernameEntry.STMT_CREATE_TABLE);
        sqLiteDatabase.execSQL(KeyEntry.STMT_CREATE_TABLE);
        sqLiteDatabase.execSQL(AccountKeyMappingEntry.STMT_CREATE_TABLE);
        sqLiteDatabase.execSQL(WebserviceEntry.STMT_CREATE_TABLE);
        createAccountInfoTypeTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createAccountInfoTypeTable(sqLiteDatabase);
        }
    }

    private void createAccountInfoTypeTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AccountTypes.STMT_CREATE_TABLE);
        for (AccountInfoType type : AccountInfoType.values()) {
            ContentValues values = new ContentValues();
            values.put(AccountTypes.COLUMN_NAME_TYPE, type.ordinal());
            sqLiteDatabase.insert(AccountTypes.TABLE_NAME, null, values);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void insertAccountInfo(AbstractAccountInfo accountInfo) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AbstractAccountInfo.COLUMN_NAME_ENTRY_TYPE, accountInfo.getType().ordinal());

        long id = database.insert(AbstractAccountInfo.TABLE_NAME, null, values);
        accountInfo.setId(id);

        String tableName = AccountInfoType.getTableNameForType(accountInfo.getType());
        database.insert(tableName, null, accountInfo.getContentValues());
    }

    public int deleteAccountInfo(long accountInfoId) {
        return getWritableDatabase().delete(AbstractAccountInfo.TABLE_NAME, AbstractAccountInfo._ID + PLACEHOLDER,
                new String[]{Long.toString(accountInfoId)});
    }

    public long insertKey(String alias) {
        ContentValues values = new ContentValues();
        values.put(KeyEntry.COLUMN_NAME_ALIAS, alias);
        return getWritableDatabase().insert(KeyEntry.TABLE_NAME, null, values);
    }

    public int deleteKey(String alias) {
        return getWritableDatabase().delete(KeyEntry.TABLE_NAME, KeyEntry.COLUMN_NAME_ALIAS + PLACEHOLDER,
                new String[]{alias});
    }

    public void insertWebserviceInfo(WebserviceEntry entry) {
        ContentValues values = entry.getContentValues();
        getWritableDatabase().insert(WebserviceEntry.TABLE_NAME, null, values);
    }

    public boolean updateWebserviceInfo(Long id, String skytrustId, WebserviceEntryStatus status) {
        ContentValues values = new ContentValues();
        values.put(WebserviceEntry.COLUMN_NAME_SKYTRUSTID, skytrustId);
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

    public int getAccountGoogleCount() {
        return getReadableDatabase().query(AccountGoogleEntry.TABLE_NAME,
                new String[]{AccountGoogleEntry._ID, AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS}, null, null, null,
                null, AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS, null).getCount();
    }

    public int getAccountUsernameCount() {
        return getReadableDatabase().query(AccountUsernameEntry.TABLE_NAME,
                new String[]{AccountUsernameEntry._ID, AccountUsernameEntry.COLUMN_NAME_USERNAME}, null, null, null,
                null, AccountUsernameEntry.COLUMN_NAME_USERNAME, null).getCount();
    }

    public int getKeyCount() {
        return getReadableDatabase().query(KeyEntry.TABLE_NAME, KeyEntry.ALL_COLUMNS, null, null, null, null,
                KeyEntry.COLUMN_NAME_ALIAS, null).getCount();
    }

    public Cursor getAccountTypesCursor() {
        return getReadableDatabase().query(AccountTypes.TABLE_NAME, AccountTypes.ALL_COLUMNS, null, null, null, null,
                AccountTypes.COLUMN_NAME_TYPE, null);
    }

    public Cursor getAccountGoogleCursor() {
        return getReadableDatabase().query(AccountGoogleEntry.TABLE_NAME, AccountGoogleEntry.ALL_COLUMNS, null, null,
                null, null, AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS, null);
    }

    public Cursor getAccountGoogleByAddress(String mailAddress) {
        return getReadableDatabase().query(AccountGoogleEntry.TABLE_NAME, AccountGoogleEntry.ALL_COLUMNS,
                AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS + PLACEHOLDER, new String[]{mailAddress}, null, null,
                null);
    }

    public Cursor getAccountUsernameCursor() {
        return getReadableDatabase().query(AccountUsernameEntry.TABLE_NAME, AccountUsernameEntry.ALL_COLUMNS, null,
                null, null, null, AccountUsernameEntry.COLUMN_NAME_USERNAME, null);
    }

    public Cursor getAccountUsernameByUsernamePassword(String username, String password) {
        return getReadableDatabase().query(AccountUsernameEntry.TABLE_NAME, AccountUsernameEntry.ALL_COLUMNS,
                AccountUsernameEntry.COLUMN_NAME_USERNAME + PLACEHOLDER + " AND " +
                        AccountUsernameEntry.COLUMN_NAME_PASSWORD + PLACEHOLDER, new String[]{username, password}, null,
                null, null);
    }

    public Cursor getKeyCursor() {
        return getReadableDatabase().query(KeyEntry.TABLE_NAME, KeyEntry.ALL_COLUMNS, null, null, null, null,
                KeyEntry.COLUMN_NAME_ALIAS, null);
    }

    public Cursor getKeyByAlias(String alias) {
        return getReadableDatabase().query(KeyEntry.TABLE_NAME, KeyEntry.ALL_COLUMNS,
                KeyEntry.COLUMN_NAME_ALIAS + PLACEHOLDER, new String[]{alias}, null, null, null);
    }

    public Cursor getKeyById(long id) {
        return getReadableDatabase().query(KeyEntry.TABLE_NAME, KeyEntry.ALL_COLUMNS, KeyEntry._ID + PLACEHOLDER,
                new String[]{String.valueOf(id)}, null, null, null);
    }

    public Cursor getKeysByIds(ArrayList<String> keyIds) {
        return getReadableDatabase().query(KeyEntry.TABLE_NAME, KeyEntry.ALL_COLUMNS,
                KeyEntry._ID + " IN (" + DatabaseHandler.makePlaceholders(keyIds.size()) + ")",
                keyIds.toArray(new String[keyIds.size()]), null, null, null);
    }

    public Cursor getWebserviceCursor() {
        return getReadableDatabase().query(WebserviceEntry.TABLE_NAME, WebserviceEntry.ALL_COLUMNS, null, null, null,
                null, WebserviceEntry.COLUMN_NAME_TITLE, null);
    }

    public Cursor getAccountMappingCursorForAccounts(long accountId, long[] keyIds) {
        int selectedAccountSize = keyIds.length;
        String[] paramMapping = new String[1 + selectedAccountSize];
        paramMapping[0] = Long.toString(accountId);
        for (int i = 0; i < selectedAccountSize; i++) {
            paramMapping[i + 1] = Long.toString(keyIds[i]);
        }
        return getReadableDatabase().rawQuery(
                "SELECT count(" + AccountKeyMappingEntry.COLUMN_NAME_KEY_ID + ") FROM " + AccountKeyMappingEntry.TABLE_NAME +
                        " WHERE " + AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID + PLACEHOLDER + " AND " +
                        AccountKeyMappingEntry.COLUMN_NAME_KEY_ID + " IN " +
                        "(" + DatabaseHandler.makePlaceholders(selectedAccountSize) + ")", paramMapping);
    }

    public Cursor getAccountMappingCursorForKeys(long keyId, long accountId) {
        String[] paramMapping = new String[2];
        paramMapping[0] = Long.toString(keyId);
        paramMapping[1] = Long.toString(accountId);
        return getReadableDatabase().rawQuery(
                "SELECT count(" + AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID + ") FROM " + AccountKeyMappingEntry.TABLE_NAME +
                        " WHERE " + AccountKeyMappingEntry.COLUMN_NAME_KEY_ID + PLACEHOLDER + " AND " +
                        AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID + PLACEHOLDER, paramMapping);
    }

    public Cursor getAccountMappingByUserId(long userId) {
        return getReadableDatabase().query(AccountKeyMappingEntry.TABLE_NAME, AccountKeyMappingEntry.ALL_COLUMNS,
                AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID + PLACEHOLDER, new String[]{String.valueOf(userId)}, null,
                null, null);
    }

    public Cursor getAccountMappingByAccountIdKeyId(long userId, long keyId) {
        return getReadableDatabase().query(AccountKeyMappingEntry.TABLE_NAME, AccountKeyMappingEntry.ALL_COLUMNS,
                AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID + PLACEHOLDER + " " +
                        "AND " +
                        AccountKeyMappingEntry.COLUMN_NAME_KEY_ID + PLACEHOLDER,
                new String[]{String.valueOf(userId), String.valueOf(keyId)}, null, null, null);
    }

    public long insertAccountKeyMapping(long keyId, long accountId) {
        ContentValues values = new ContentValues();
        values.put(AccountKeyMappingEntry.COLUMN_NAME_KEY_ID, keyId);
        values.put(AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID, accountId);
        return getWritableDatabase().insert(AccountKeyMappingEntry.TABLE_NAME, null, values);
    }

    public boolean deleteAccountKeyMapping(long keyId, long accountId) {
        return getWritableDatabase().delete(AccountKeyMappingEntry.TABLE_NAME,
                String.format("%s=? AND %s=?", AccountKeyMappingEntry.COLUMN_NAME_KEY_ID,
                        AccountKeyMappingEntry.COLUMN_NAME_ACCOUNT_ID),
                new String[]{Long.toString(keyId), Long.toString(accountId)}) == 1;
    }

    private static String makePlaceholders(int len) {
        if (len < 0) {
            throw new RuntimeException("No placeholders");
        } else if (len == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
