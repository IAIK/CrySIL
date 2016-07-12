package org.crysil.authplugins.oauth;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.crysil.authentication.AuthException;
import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.oauth.OAuthAuthInfo;
import org.crysil.protocol.payload.auth.oauth.OAuthAuthType;

public class AuthOAuth<T extends IAuthUI<String, String>> implements AuthHandler {
  public static final String K_URL = "url";

  private final AuthType     authType;

  private final Class<T>     dialogType;

  public static class Factory<T extends IAuthUI<String, String>>
      implements AuthHandlerFactory<String, String, T> {

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

      return new AuthOAuth<>(authType, dialogType);
    }

    @Override
    public boolean canTake(final Response crysilResponse, final AuthType authType) throws AuthException {
      return (authType instanceof OAuthAuthType);
    }

    @Override
    public Class<T> getDialogType() {
      return dialogType;
    }
  }

  public AuthOAuth(final AuthType authType, final Class<T> dialogType) {
    this.authType = authType;
    this.dialogType = dialogType;
  }

  @Override
  public AuthInfo authenticate() throws AuthException {
    final CountDownLatch sync = new CountDownLatch(1);
    final AtomicReference<String> token = new AtomicReference<>();
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {

          final T authUi = dialogType.newInstance();
          final Map<String, String> values = new HashMap<>();
          values.put(K_URL, ((OAuthAuthType) authType).getUrl());
          authUi.init(values);
          authUi.setCallbackAuthenticate(new ActionPerformedCallback() {
            @Override
            public void actionPerformed() {
              token.set(authUi.getAuthValue());
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
      throw new AuthException("Error while waiting for oauth dialog", e);
    }

    final OAuthAuthInfo info = new OAuthAuthInfo();
    info.setAuthorizationCode(token.get());

    return info;
  }

  @Override
  public String getFriendlyName() {
    return "OAuth";
  }

  @Override
  public boolean authenticatesAuthomatically() {
    return false;
  }
}
