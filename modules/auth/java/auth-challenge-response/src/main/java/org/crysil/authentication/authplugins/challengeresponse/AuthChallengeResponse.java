package org.crysil.authentication.authplugins.challengeresponse;

import java.awt.EventQueue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthException;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthInfo;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthType;
public class AuthChallengeResponse<T extends IAuthUI<String, Serializable>> implements AuthHandler {

  private final Response     crysilResponse;
  private final AuthType     authType;
  private final Class<T>     dialogType;

  public static final String K_CHALLENGE = "challenge";
  public static final String K_ISQUESTION = "question";

  public static class Factory<T extends IAuthUI<String, Serializable>>
      implements AuthHandlerFactory<String, Serializable, T> {

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

      return new AuthChallengeResponse<>(crysilResponse, authType, dialogType);
    }

    @Override
    public boolean canTake(final Response crysilResponse, final AuthType authType)
        throws AuthException {
      return (authType instanceof ChallengeResponseAuthType);
    }

    @Override
    public Class<T> getDialogType() {
      return dialogType;
    }
  }

  public AuthChallengeResponse(final Response crysilResponse, final AuthType authType,
      final Class<T> dialogType) {
    this.crysilResponse = crysilResponse;
    this.authType = authType;
    this.dialogType = dialogType;
  }

  @Override
  public Request authenticate() throws AuthException {
    final CountDownLatch sync = new CountDownLatch(1);
    final AtomicReference<String> result = new AtomicReference<>();

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {

          final T authUi = dialogType.newInstance();
          final Map<String, Serializable> values = new HashMap<>();
          values.put(K_CHALLENGE, ((ChallengeResponseAuthType) authType).getChallenge());
          values.put(K_ISQUESTION, ((ChallengeResponseAuthType) authType).isQuestion());
          authUi.init(values);

          authUi.setCallbackAuthenticate(new ActionPerformedCallback() {
            @Override
            public void actionPerformed() {
              result.set(authUi.getAuthValue());
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
      throw new AuthException("Error waiting for automated prose dialog", e);
    }

    final Request authRequest = new Request();


    authRequest.setHeader(crysilResponse.getHeader());

    final ChallengeResponseAuthInfo authInfo = new ChallengeResponseAuthInfo();
    authInfo.setResponseString(result.get());
    final PayloadAuthRequest authRequestPayload = new PayloadAuthRequest();
    authRequestPayload.setAuthInfo(authInfo);
    authRequest.setPayload(authRequestPayload);

    return authRequest;
  }

  @Override
  public String getFriendlyName() {
    return "Challenge-Response Auth";
  }

  @Override
  public boolean authenticatesAuthomatically() {
    return false;
  }
}
