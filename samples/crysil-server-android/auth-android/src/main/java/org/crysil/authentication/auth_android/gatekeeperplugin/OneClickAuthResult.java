package org.crysil.authentication.auth_android.gatekeeperplugin;

import org.crysil.gatekeeper.AuthResult;

/**
 * Created by freimair on 30.11.17.
 */

public class OneClickAuthResult extends AuthResult {
    private final String result;

    public OneClickAuthResult(final String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
