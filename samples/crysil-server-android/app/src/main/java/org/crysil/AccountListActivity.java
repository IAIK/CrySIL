package org.crysil;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.google.common.collect.Lists;

import java.util.List;

import org.crysil.database.DatabaseHandler;
import org.crysil.database.accounts.AbstractAccountInfo;
import org.crysil.database.keys.KeyEntry;
import org.crysil.utils.AccountCursorTreeAdapter;
import org.crysil.authentication.auth_android.ui.CurrentActivityTracker;

/**
 * Shows a list of all accounts stored in the database. Enables a contextual action bar to delete them or assign keys
 * to them for access.
 */
public class AccountListActivity extends ExpandableListActivity {

    private final static String TAG = AccountListActivity.class.getSimpleName();

    private AccountCursorTreeAdapter listAdapter;
    private ExpandableListView listViewAccounts;
    private ActionMode actionMode;

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        private CharSequence[] mKeys;
        private long[] mKeyIds;
        private boolean[] mKeysSelected;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_account_list_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        private void prepareKeyLists() {
            DatabaseHandler databaseHandler = new DatabaseHandler(AccountListActivity.this);
            Cursor cursorKeys = databaseHandler.getKeyCursor();
            mKeys = new CharSequence[cursorKeys.getCount()];
            mKeyIds = new long[cursorKeys.getCount()];
            mKeysSelected = new boolean[cursorKeys.getCount()];
            while (cursorKeys.moveToNext()) {
                int cursorPosition = cursorKeys.getPosition();
                String keyAlias = cursorKeys.getString(cursorKeys.getColumnIndex(KeyEntry.COLUMN_NAME_ALIAS));
                long keyId = cursorKeys.getLong(cursorKeys.getColumnIndex(KeyEntry._ID));
                mKeys[cursorPosition] = keyAlias;
                mKeyIds[cursorPosition] = keyId;

                int flatPos = listViewAccounts.getCheckedItemPosition();
                Cursor cursor = (Cursor) listViewAccounts.getItemAtPosition(flatPos);
                long accountId = cursor.getLong(cursor.getColumnIndex(AbstractAccountInfo._ID));
                Cursor cursorMapping = databaseHandler.getAccountMappingCursorForKeys(keyId, accountId);
                if (cursorMapping.moveToFirst()) {
                    mKeysSelected[cursorPosition] = cursorMapping.getLong(0) > 0;
                }
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
                    new AlertDialog.Builder(AccountListActivity.this).setMessage(
                            getString(R.string.accountlist_delete_confirm)).setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHandler databaseHandler = new DatabaseHandler(AccountListActivity.this);
                                    int flatPos = listViewAccounts.getCheckedItemPosition();
                                    Cursor cursor = (Cursor) listViewAccounts.getItemAtPosition(flatPos);
                                    long id = cursor.getLong(cursor.getColumnIndex(AbstractAccountInfo._ID));
                                    if (id > -1) {
                                        databaseHandler.deleteAccountInfo(id);
                                        listAdapter.notifyDataSetChanged();
                                        mode.finish();
                                    }
                                    databaseHandler.close();
                                }
                            }).setNegativeButton(android.R.string.no, null).show();
                    return true;
                case R.id.action_account_assign_key:
                    if (listAdapter == null) {
                        return false;
                    }
                    prepareKeyLists();
                    final List<Long> dialogKeysSelected = Lists.newArrayList();
                    final List<Long> dialogKeysUnselected = Lists.newArrayList();
                    new AlertDialog.Builder(AccountListActivity.this).setTitle(getString(R.string.accountlist_map_keys))
                            // We can't show a message!
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHandler databaseHandler = new DatabaseHandler(AccountListActivity.this);
                                    int flatPos = listViewAccounts.getCheckedItemPosition();
                                    Cursor cursor = (Cursor) listViewAccounts.getItemAtPosition(flatPos);
                                    long accountId = cursor.getLong(cursor.getColumnIndex(AbstractAccountInfo._ID));
                                    for (long keyId : dialogKeysSelected) {
                                        Log.d(TAG, String.format("Inserting new mapping for key=%d, " + "account=%d",
                                                keyId, accountId));
                                        databaseHandler.insertAccountKeyMapping(keyId, accountId);
                                    }
                                    for (long keyId : dialogKeysUnselected) {
                                        Log.d(TAG, String.format("Deleting mapping for key=%d, " + "account=%d", keyId,
                                                accountId));
                                        databaseHandler.deleteAccountKeyMapping(keyId, accountId);
                                    }
                                    databaseHandler.close();
                                    mode.finish();
                                }
                            }).setNegativeButton(android.R.string.no, null).setMultiChoiceItems(mKeys, mKeysSelected,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    long listViewPos = ((AlertDialog) dialog).getListView().getItemIdAtPosition(which);
                                    long keyId = mKeyIds[((int) listViewPos)];
                                    if (isChecked) {
                                        dialogKeysSelected.add(keyId);
                                    } else if (dialogKeysSelected.contains(keyId)) {
                                        dialogKeysSelected.remove(keyId);
                                    }
                                    if (!isChecked) {
                                        dialogKeysUnselected.add(keyId);
                                    } else if (dialogKeysUnselected.contains(keyId)) {
                                        dialogKeysUnselected.remove(keyId);
                                    }
                                    Log.d(TAG, String.format("Which=%d, listViewPos=%d, keyId=%d, checked=%b", which,
                                            listViewPos, keyId, isChecked));
                                }
                            }).show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            listViewAccounts.setItemChecked(listViewAccounts.getCheckedItemPosition(), false);
            listViewAccounts.invalidate();
        }
    };
    private ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent,
                                    View view,
                                    int groupPosition,
                                    int childPosition,
                                    long id) {
            int position = listViewAccounts.getFlatListPosition(
                    listViewAccounts.getPackedPositionForChild(groupPosition, childPosition));
            if (actionMode != null) {
                listViewAccounts.setItemChecked(position, true);
                return true;
            }
            return false;
        }
    };
    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            long packedPosition = listViewAccounts.getExpandableListPosition(position);
            int itemType = ExpandableListView.getPackedPositionType(packedPosition);
            if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                return false;
            } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                listViewAccounts.setItemChecked(position, true);
                if (actionMode != null) {
                    return false;
                }
                actionMode = AccountListActivity.this.startActionMode(actionModeCallback);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        listAdapter = new AccountCursorTreeAdapter(this, databaseHandler.getAccountTypesCursor());
        setListAdapter(listAdapter);

        listViewAccounts = getExpandableListView();
        listViewAccounts.setOnItemLongClickListener(onItemLongClickListener);
        listViewAccounts.setOnChildClickListener(onChildClickListener);
        listViewAccounts.setAdapter(listAdapter);
        for (int group = 0; group < listAdapter.getGroupCount(); ++group) {
            listViewAccounts.expandGroup(group);
        }
        databaseHandler.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CurrentActivityTracker.onActivityResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CurrentActivityTracker.onActivityStop(this);
    }
}
