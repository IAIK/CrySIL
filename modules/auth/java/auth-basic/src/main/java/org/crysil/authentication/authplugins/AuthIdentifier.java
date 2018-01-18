package org.crysil.authentication.authplugins;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
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
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

public class AuthIdentifier<T extends IAuthUI<char[][], Void>> implements AuthHandler {
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

      return new AuthIdentifier<>(dialogType);
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

  public AuthIdentifier(final Class<T> dialogType) {
    this.dialogType = dialogType;
  }

  @Override
	public synchronized AuthInfo authenticate() throws AuthException {
		final CountDownLatch sync = new CountDownLatch(1);
    final AtomicReference<String> secret = new AtomicReference<>();

		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {

						final T authUi = dialogType.newInstance();
						authUi.setCallbackAuthenticate(new ActionPerformedCallback() {
							@Override
							public void actionPerformed() {
								final char[][] authValues = authUi.getAuthValue();
								secret.set(new String(authValues[0]));
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
		} catch (InvocationTargetException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			sync.await();
		} catch (final InterruptedException e) {
			throw new AuthException("Error waiting for secret dialog", e);
		}

		final IdentifierAuthInfo authInfo = new IdentifierAuthInfo();
		authInfo.setIdentifier(secret.get());
    return authInfo;
  }

  @Override
  public String getFriendlyName() {
		return "Identifier";
  }

  @Override
  public boolean authenticatesAuthomatically() {
    return false;
  }
}
