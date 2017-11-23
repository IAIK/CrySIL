package at.tugraz.iaik.skytrust.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import at.tugraz.iaik.skytrust.R;
import at.tugraz.iaik.skytrust.database.webservice.WebserviceEntry;

/**
 * Adapter to display all known webservices from the database in a list on the UI
 */
public class WebserviceListAdapter extends SimpleCursorAdapter {

    private LayoutInflater inflater;

    public WebserviceListAdapter(Context context, Cursor cursor) {
        super(context, 0, cursor, new String[]{}, new int[]{}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            if (parent instanceof Spinner) {
                view = inflater.inflate(R.layout.spinner_webservice_item, parent, false);
            } else {
                view = inflater.inflate(R.layout.list_webservice_item, parent, false);
            }
        }
        Cursor cursor = (Cursor) getItem(position);

        String title = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_TITLE));
        ((TextView) view.findViewById(R.id.webservicelist_item_title)).setText(title);

        String host = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_HOSTNAME));
        int port = cursor.getInt(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_PORT));
        String hostAndPort = String.format("%s:%d",host, port);
        ((TextView) view.findViewById(R.id.webservicelist_item_address)).setText(hostAndPort);

        String skytrustId = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_SKYTRUSTID));
        ((TextView) view.findViewById(R.id.webservicelist_item_id)).setText(skytrustId);

        String status = cursor.getString(cursor.getColumnIndex(WebserviceEntry.COLUMN_NAME_STATUS));
        if (status != null) {
            status = status.toLowerCase();
        }
        ((TextView) view.findViewById(R.id.webservicelist_item_status)).setText(status);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
