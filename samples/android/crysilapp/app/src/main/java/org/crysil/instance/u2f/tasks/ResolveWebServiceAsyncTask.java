package org.crysil.instance.u2f.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crysil.instance.u2f.database.webservice.WebserviceEntry;

/**
 * User can enter IP address or hostname, this class resolves the other value
 */
public class ResolveWebServiceAsyncTask extends AsyncTask<String, Void, WebserviceEntry> {

    private static final String TAG = ResolveWebServiceAsyncTask.class.getSimpleName();

    private static Pattern PATTERN_IPV4 = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:(\\d+))?");
    private static Pattern PATTERN_HOST = Pattern.compile("([\\w\\.\\-]+)(:(\\d+))?");

    @Override
    protected WebserviceEntry doInBackground(String... strings) {
        if (strings.length < 2) {
            return null;
        }
        String title = strings[0];
        String address = strings[1];

        Matcher matcher = PATTERN_IPV4.matcher(address);
        int port = -1;
        InetAddress inetAddress = null;
        if (matcher.matches()) {
            try {
                inetAddress = InetAddress.getByName(matcher.group(1));
                if (matcher.group(3) != null) {
                    port = Integer.valueOf(matcher.group(3));
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "Could not resolve webservice", e);
                return null;
            }
        } else {
            matcher = PATTERN_HOST.matcher(address);
            if (matcher.matches()) {
                try {
                    inetAddress = InetAddress.getByName(matcher.group(1));
                    if (matcher.group(3) != null) {
                        port = Integer.valueOf(matcher.group(3));
                    }
                } catch (UnknownHostException e) {
                    Log.e(TAG, "Could not resolve webservice", e);
                    return null;
                }
            } else {
                Log.d(TAG, "No valid address: " + address);
                return null;
            }
        }
        return new WebserviceEntry(title, inetAddress.getHostName(), inetAddress.getHostAddress(), port);
    }
}
