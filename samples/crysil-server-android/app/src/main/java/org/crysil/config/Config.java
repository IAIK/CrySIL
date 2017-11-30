package org.crysil.config;

import org.crysil.authentication.auth_android.authplugins.Auth1ClickConfirmation;
import org.crysil.authentication.auth_android.gatekeeperplugin.OneClickAuthPlugin;
import org.crysil.authentication.auth_android.ui.AndroidConfirmationNotificationHandler;
import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gatekeeper.AuthProcess;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyRequest;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyResponse;

/**
 * Created by freimair on 30.11.17.
 */

public class Config implements Configuration {
    @Override
    public AuthProcess getAuthProcess(Request request, Gatekeeper gatekeeper) throws AuthenticationFailedException {
        if (request.getPayload().getType().equals("decryptRequest")) {
            IdentifierAuthInfo expectedResult = new IdentifierAuthInfo();
            expectedResult.setIdentifier("allow");
            return new AuthProcess(request, new OneClickAuthPlugin(new IdentifierAuthType(),expectedResult));
        }

        // any other request is for free
        return new AuthProcess(request);
    }
}
