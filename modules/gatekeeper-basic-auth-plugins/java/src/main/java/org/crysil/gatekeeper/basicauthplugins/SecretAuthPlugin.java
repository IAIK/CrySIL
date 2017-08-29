package org.crysil.gatekeeper.basicauthplugins;

import java.util.LinkedList;
import java.util.List;

import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.credentials.SecretAuthInfo;
import org.crysil.protocol.payload.auth.credentials.SecretAuthType;

public class SecretAuthPlugin
		extends AuthPlugin<SecretAuthType, SecretAuthResult> {
	private final SecretAuthType authType;
	private final SecretAuthInfo expectedResult;

	public SecretAuthPlugin(final SecretAuthType authTypeWhithChallenge, final SecretAuthInfo expectedResult) {
		this.authType = authTypeWhithChallenge;
    this.expectedResult = expectedResult;
  }

  @Override
	public SecretAuthType getAuthType() {
		return authType;
  }

  @Override
	protected boolean isValid(final SecretAuthResult result) {
		return expectedResult.getSecret().equals(result.getResult());
  }

  @Override
  public Response generateAuthChallenge(final Request request) {
    final PayloadAuthResponse payload = new PayloadAuthResponse();
    final List<AuthType> auth = new LinkedList<>();
		auth.add(authType);
    payload.setAuthTypes(auth);
    return new Response(request.getHeader().clone(), payload);
  }

  @Override
	protected SecretAuthResult getResponsetoChallenge(final PayloadAuthRequest responseToChallenge) {
    final AuthInfo authInfo = responseToChallenge.getAuthInfo();
		if (!(authInfo instanceof SecretAuthInfo)) {
      return null;
    }
		return new SecretAuthResult(((SecretAuthInfo) authInfo).getSecret());

  }
}
