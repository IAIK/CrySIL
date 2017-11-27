package org.crysil.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.crysil.R;
import org.crysil.database.DatabaseHandler;
import org.crysil.database.accounts.AccountGoogleEntry;
import org.crysil.database.accounts.AccountInfoType;
import org.crysil.database.accounts.AccountTypes;
import org.crysil.database.accounts.AccountUsernameEntry;

public class AccountCursorTreeAdapter extends CursorTreeAdapter {

    private final LayoutInflater inflater;
    private final DatabaseHandler databaseHandler;
    private final Context context;

    public AccountCursorTreeAdapter(Context context, Cursor baseCursor) {
        super(baseCursor, context, true);
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        if (groupCursor.getInt(groupCursor.getColumnIndex(
                AccountTypes.COLUMN_NAME_TYPE)) == AccountInfoType.GOOGLE_ACCOUNT.ordinal()) {
            return databaseHandler.getAccountGoogleCursor();
        } else {
            return databaseHandler.getAccountUsernameCursor();
        }
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        return inflater.inflate(R.layout.list_account_header, parent, false);
    }

    @Override
    protected void bindGroupView(View view, final Context context, Cursor cursor, boolean isExpanded) {
        if (view != null) {
            Button button = (Button) view.findViewById(R.id.accountlist_section_username_password_button);
            ImageView imageView = (ImageView) view.findViewById(R.id.accountlist_section_username_password_image);
            if (cursor.getInt(
                    cursor.getColumnIndex(AccountTypes.COLUMN_NAME_TYPE)) == AccountInfoType.GOOGLE_ACCOUNT.ordinal()) {
                bindGroupViewGoogle(button, imageView, this.context);
            } else {
                bindGroupViewUsername(button, imageView, this.context);
            }
        }
    }

    private void bindGroupViewUsername(Button button, ImageView imageView, final Context context) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View dialogView = inflater.inflate(R.layout.dialog_account_username, null);
                final EditText edUsername = (EditText) dialogView.findViewById(R.id.dialog_add_up_username);
                final EditText edPassword = (EditText) dialogView.findViewById(R.id.dialog_add_up_password);
                builder.setView(dialogView).setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseHandler databaseHandler = new DatabaseHandler(context);
                                String username = edUsername.getText().toString();
                                String password = edPassword.getText().toString();
                                if (!username.isEmpty() && !password.isEmpty()) {
                                    AccountUsernameEntry info = new AccountUsernameEntry(username, password);
                                    databaseHandler.insertAccountInfo(info);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, context.getResources().getString(
                                            R.string.accountlist_create_username_error), Toast.LENGTH_SHORT).show();
                                }
                                databaseHandler.close();
                            }
                        }).setNegativeButton(android.R.string.no, null);
                builder.show();
            }
        });
        button.setTag(AccountInfoType.getTableNameForType(AccountInfoType.USERNAME_ACCOUNT));
        imageView.setImageResource(R.drawable.account_username);
    }

    private void bindGroupViewGoogle(Button button, ImageView imageView, final Context context) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View dialogView = inflater.inflate(R.layout.dialog_account_google, null);
                final EditText edGmailAddress = (EditText) dialogView.findViewById(R.id.dialog_add_gmail_edit_text);
                builder.setView(dialogView).setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseHandler databaseHandler = new DatabaseHandler(context);
                                String email = edGmailAddress.getText().toString();
                                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    AccountGoogleEntry info = new AccountGoogleEntry(email);
                                    databaseHandler.insertAccountInfo(info);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context,
                                            context.getResources().getString(R.string.accountlist_create_google_error),
                                            Toast.LENGTH_SHORT).show();
                                }
                                databaseHandler.close();
                            }
                        }).setNegativeButton(android.R.string.no, null);
                builder.show();
            }
        });
        button.setTag(AccountInfoType.getTableNameForType(AccountInfoType.GOOGLE_ACCOUNT));
        imageView.setImageResource(R.drawable.account_google);
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        return inflater.inflate(R.layout.list_account_item, parent, false);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        if (view != null) {
            TextView text1 = (TextView) view.findViewById(R.id.username_entry);
            TextView text2 = (TextView) view.findViewById(R.id.password_entry);
            text1.setText("");
            text2.setText("");
            if (cursor.getColumnIndex(AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS) > -1) {
                text1.setText(cursor.getString(cursor.getColumnIndex(AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS)));
            }
            if (cursor.getColumnIndex(AccountUsernameEntry.COLUMN_NAME_USERNAME) > -1) {
                text1.setText(cursor.getString(cursor.getColumnIndex(AccountUsernameEntry.COLUMN_NAME_USERNAME)));
                text2.setText(cursor.getString(cursor.getColumnIndex(AccountUsernameEntry.COLUMN_NAME_PASSWORD)));
            }
        }
    }
}
