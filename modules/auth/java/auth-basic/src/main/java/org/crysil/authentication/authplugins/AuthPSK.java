package org.crysil.authentication.authplugins;

import java.awt.EventQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.crysil.authentication.AuthenticationPlugin;
import org.crysil.authentication.AuthenticationPluginException;
import org.crysil.authentication.AuthenticationPluginFactory;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.SecretAuthInfo;
import org.crysil.protocol.payload.auth.credentials.SecretAuthType;

public class AuthPSK<T extends IAuthUI<char[], Void>> implements AuthenticationPlugin {

  private final Response crysilResponse;
  private final AuthType authType;
  private final Class<T> dialogType;

  public static class Factory<T extends IAuthUI<char[], Void>>
      implements AuthenticationPluginFactory<char[], Void, T> {

    private final Class<T> dialogType;

    public Factory(final Class<T> dialogType) {
      this.dialogType = dialogType;
    }

    @Override
    public AuthenticationPlugin createInstance(final Response crysilResponse, final AuthType authType,
        final Class<T> dialogType) throws AuthenticationPluginException {
      if (!canTake(crysilResponse, authType)) {
        throw new AuthenticationPluginException("Invalid authType");
      }

      return new AuthPSK<T>(crysilResponse, authType, dialogType);
    }

    @Override
    public boolean canTake(final Response crysilResponse, final AuthType authType)
        throws AuthenticationPluginException {
      return (authType instanceof SecretAuthType);
    }

    @Override
    public Class<T> getDialogType() {
      return dialogType;
    }
  }

  public AuthPSK(final Response crysilResponse, final AuthType authType, final Class<T> dialogType) {
    this.crysilResponse = crysilResponse;
    this.authType = authType;
    this.dialogType = dialogType;
  }

  @Override
  public Request authenticate() throws AuthenticationPluginException {
    final CountDownLatch sync = new CountDownLatch(1);
    final AtomicReference<String> psk = new AtomicReference<>();

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          final T authUi = dialogType.newInstance();
          authUi.setCallbackAuthenticate(new ActionPerformedCallback() {
            @Override
            public void actionPerformed() {
              psk.set(new String(authUi.getAuthValue()));
              authUi.dismiss();
              sync.countDown();
            }
          });
          authUi.present();
        } catch (final InstantiationException e) {
          sync.countDown();
          e.printStackTrace();
        } catch (final IllegalAccessException e) {
          sync.countDown();
          e.printStackTrace();
        }
      }
    });

    try {
      sync.await();
    } catch (final InterruptedException e) {
      throw new AuthenticationPluginException("Error waiting for secret dialog", e);
    }

    final Request authRequest = new Request();

    final Header header = new StandardHeader();
    header.setCommandId(crysilResponse.getHeader().getCommandId());
    authRequest.setHeader(header);

    final SecretAuthInfo authInfo = new SecretAuthInfo();
    authInfo.setSecret(psk.get());
    final PayloadAuthRequest authRequestPayload = new PayloadAuthRequest();
    authRequestPayload.setAuthInfo(authInfo);
    authRequest.setPayload(authRequestPayload);

    return (authRequest);
  }

  @Override
  public String getFriendlyName() {
    return "Pre-Shared Secret";
  }

  @Override
  public boolean authenticatesAuthomatically() {
    return false;
  }
}
