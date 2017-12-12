package org.crysil.authentication.auth_android.authplugins;

import org.crysil.authentication.AuthException;
import org.crysil.authentication.auth_android.ui.AndroidConfirmationDialog;
import org.crysil.authentication.auth_android.ui.AndroidConfirmationNotificationHandler;
import org.crysil.authentication.auth_android.ui.CurrentActivityTracker;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;

import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.payload.auth.AuthInfo;

import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;
import org.crysil.protocol.payload.auth.credentials.UserPasswordAuthType;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Christoph Thaller on 03/01/15.
 */
public class Auth1ClickConfirmation<T extends IAuthUI<char[][], Void>> implements AuthHandler {
    private final Class<T> dialogType;

    public static class Factory<T extends IAuthUI<char[][], Void>>
            implements AuthHandlerFactory<char[][], Void, T> {

        private final Class<T> dialogType;

        public Factory(final Class<T> dialogType) {
            this.dialogType = dialogType;
        }

        @Override
        public AuthHandler createInstance(final Response crysilResponse, final AuthType authType,
                                          final Class<T> dialogType) throws AuthException {
            if (!canTake(crysilResponse, authType)) {
                throw new AuthException("Invalid authType");
            }

            return new Auth1ClickConfirmation<>(dialogType);
        }

        @Override
        public boolean canTake(final Response crysilResponse, final AuthType authType) throws AuthException {
            return (authType instanceof IdentifierAuthType);
        }

        @Override
        public Class<T> getDialogType() {
            return dialogType;
        }
    }


    public Auth1ClickConfirmation(final Class<T> dialogType) {
        this.dialogType = dialogType;
    }


    @Override
    public AuthInfo authenticate() throws AuthException {
        IAuthUI authdialog = new AndroidConfirmationDialog(CurrentActivityTracker.getActivity());
        authdialog.init(null);
        final CountDownLatch processing = new CountDownLatch(1);
        authdialog.setCallbackAuthenticate(new ActionPerformedCallback() {
            @Override
            public void actionPerformed() {
                processing.countDown();
            }
        });
        authdialog.present();
        String result = "decline";
        try {
            processing.await();
            result = (String) authdialog.getAuthValue();
        } catch(InterruptedException e) {
            throw new AuthException(e);
        }

        final IdentifierAuthInfo authInfo = new IdentifierAuthInfo();
        authInfo.setIdentifier(result);
        return authInfo;
    }

    @Override
        public String getFriendlyName() {
            return "1-Click Confirmation";
        }

    @Override
    public boolean authenticatesAuthomatically() {
        return false;
    }
}
