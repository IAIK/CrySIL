package org.crysil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.common.collect.Lists;

import java.util.List;

import org.crysil.database.DatabaseHandler;
import org.crysil.database.accounts.AccountGoogleEntry;
import org.crysil.database.accounts.AccountUsernameEntry;
import org.crysil.database.keys.KeyEntry;
import org.crysil.tasks.KeyCreateAsyncTask;
import org.crysil.utils.KeyListAdapter;
import org.crysil.utils.KeyStoreHandler;

/**
 * Lists all keys in a simple list, with some details. Enables a context menu on long press (=selection) to delete
 * keys or map user accounts to them for access via SkyTrust methods
 *
 * @see org.crysil.utils.KeyListAdapter
 */
public class KeyListActivity extends AbstractActivity {

    private static final String TAG = KeyListActivity.class.getSimpleName();

    private static final int EDIT_ACTION = 42;

    private ListView listViewKeys;
    private KeyListAdapter listAdapter;
    private AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {

        private CharSequence[] mAccounts;
        private long[] mAccountIds;
        private boolean[] mAccountsSelected;

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_key_list_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        private void prepareKeyLists() {
            DatabaseHandler databaseHandler = new DatabaseHandler(KeyListActivity.this);
            Cursor cursorGoogle = databaseHandler.getAccountGoogleCursor();
            Cursor cursorUser = databaseHandler.getAccountUsernameCursor();
            int length = cursorGoogle.getCount() + cursorUser.getCount();
            mAccounts = new CharSequence[length];
            mAccountIds = new long[length];
            mAccountsSelected = new boolean[length];

            int position = 0;
            while (cursorGoogle.moveToNext()) {
                String gmailAddress = cursorGoogle.getString(
                        cursorGoogle.getColumnIndex(AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS));
                long accountId = cursorGoogle.getLong(cursorGoogle.getColumnIndex(AccountGoogleEntry._ID));
                mAccounts[position] = String.format("%s (OAuth)", gmailAddress);
                mAccountIds[position] = accountId;

                int selectedKeysSize = listViewKeys.getCheckedItemCount();
                if (selectedKeysSize > 0) {
                    Cursor cursorMapping = databaseHandler.getAccountMappingCursorForAccounts(accountId,
                            listViewKeys.getCheckedItemIds());
                    if (cursorMapping.moveToFirst()) {
                        mAccountsSelected[position] = cursorMapping.getLong(0) == selectedKeysSize;
                    }
                }
                position++;
            }
            while (cursorUser.moveToNext()) {
                String username = cursorUser.getString(
                        cursorUser.getColumnIndex(AccountUsernameEntry.COLUMN_NAME_USERNAME));
                long accountId = cursorUser.getLong(cursorUser.getColumnIndex(AccountUsernameEntry._ID));
                mAccounts[position] = String.format("%s (password)", username);
                mAccountIds[position] = accountId;

                int selectedKeysSize = listViewKeys.getCheckedItemCount();
                if (selectedKeysSize > 0) {
                    Cursor cursorMapping = databaseHandler.getAccountMappingCursorForAccounts(accountId,
                            listViewKeys.getCheckedItemIds());
                    if (cursorMapping.moveToFirst()) {
                        mAccountsSelected[position] = cursorMapping.getLong(0) == selectedKeysSize;
                    }
                }
                position++;
            }
            databaseHandler.close();
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    if (listAdapter == null) {
                        return false;
                    }
                    new AlertDialog.Builder(KeyListActivity.this).setMessage(
                            getString(R.string.keylist_delete_confirm)).setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHandler databaseHandler = new DatabaseHandler(KeyListActivity.this);
                                    for (long j : listViewKeys.getCheckedItemIds()) {
                                        Cursor cursor = databaseHandler.getKeyById(j);
                                        if (cursor.moveToFirst()) {
                                            String alias = cursor.getString(
                                                    cursor.getColumnIndex(KeyEntry.COLUMN_NAME_ALIAS));
                                            databaseHandler.deleteKey(alias);
                                            KeyStoreHandler.getInstance().deleteKey(alias);
                                        }
                                    }
                                    databaseHandler.close();
                                    refreshKeyList();
                                    mode.finish();
                                }
                            }).setNegativeButton(android.R.string.no, null).create().show();
                    return true;
                case R.id.action_key_assign_account:
                    if (listAdapter == null) {
                        return false;
                    }
                    prepareKeyLists();
                    final List<Long> dialogAccountsSelected = Lists.newArrayList();
                    final List<Long> dialogAccountsUnselected = Lists.newArrayList();
                    new AlertDialog.Builder(KeyListActivity.this).setTitle(getString(R.string.accountlist_map_keys))
                            // We can't show a message!
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHandler databaseHandler = new DatabaseHandler(KeyListActivity.this);
                                    for (long keyId : listViewKeys.getCheckedItemIds()) {
                                        for (long accountId : dialogAccountsSelected) {
                                            Log.d(TAG,
                                                    String.format("Inserting new mapping for key=%d, account=%d", keyId,
                                                            accountId));
                                            databaseHandler.insertAccountKeyMapping(keyId, accountId);
                                        }
                                        for (long accountId : dialogAccountsUnselected) {
                                            Log.d(TAG, String.format("Deleting mapping for key=%d, account=%d", keyId,
                                                            accountId));
                                            databaseHandler.deleteAccountKeyMapping(keyId, accountId);
                                        }
                                    }
                                    databaseHandler.close();
                                    mode.finish();
                                }
                            }).setNegativeButton(android.R.string.no, null).setMultiChoiceItems(mAccounts,
                            mAccountsSelected, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    long listViewPos = ((AlertDialog) dialog).getListView().getItemIdAtPosition(which);
                                    long accountId = mAccountIds[((int) listViewPos)];
                                    if (isChecked) {
                                        dialogAccountsSelected.add(accountId);
                                    } else if (dialogAccountsSelected.contains(accountId)) {
                                        dialogAccountsSelected.remove(accountId);
                                    }
                                    if (!isChecked) {
                                        dialogAccountsUnselected.add(accountId);
                                    } else if (dialogAccountsUnselected.contains(accountId)) {
                                        dialogAccountsUnselected.remove(accountId);
                                    }
                                    Log.d(TAG,
                                            String.format("Which=%d, listViewPos=%d, accountId=%d, checked=%b", which,
                                                    listViewPos, accountId, isChecked));
                                }
                            }).create().show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_list);

        listViewKeys = (ListView) findViewById(R.id.lvKeys);
        listViewKeys.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewKeys.setMultiChoiceModeListener(multiChoiceModeListener);
        refreshKeyList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_ACTION:
                if (resultCode == RESULT_OK) {
                    KeyCreateAsyncTask task = new KeyCreateAsyncTask(data, getApplicationContext(), this);
                    task.execute();
                }
                break;
            default:
                break;
        }
    }

    public void refreshKeyList() {
        DatabaseHandler databaseHandler = new DatabaseHandler(KeyListActivity.this);
        Cursor cursor = databaseHandler.getKeyCursor();
        if (listAdapter == null) {
            listAdapter = new KeyListAdapter(this, cursor);
            listViewKeys.setAdapter(listAdapter);
        } else {
            listAdapter.changeCursor(cursor);
        }
        databaseHandler.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_key_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_importkeystore:
                Intent importKeysIntent = new Intent(this, ImportKeyActivity.class);
                this.startActivity(importKeysIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btCreateKeyOnClick(View view) {
        Intent createKeyIntent = new Intent(this, CreateKeyActivity.class);
        this.startActivityForResult(createKeyIntent, EDIT_ACTION);
    }
}
