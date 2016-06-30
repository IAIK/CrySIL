package org.crysil.gatekeeper.challengeresponse;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gatekeeper.AuthProcess;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthInfo;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthType;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyRequest;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyResponse;

public class ChallengeResponseConfiguration implements Configuration {

  @Override
  public AuthProcess getAuthProcess(final Request request, final Gatekeeper gatekeeper)
      throws AuthenticationFailedException {
    if (request.getPayload().getType().equals("decryptRequest")) {
      final PayloadDecryptRequest req = (PayloadDecryptRequest) request.getPayload();
      if (req.getDecryptionKey().getType().equals("wrappedKey")) {
        final PayloadExtractStickyPolicyRequest policy = new PayloadExtractStickyPolicyRequest();
        policy.setWrappedKey((WrappedKey) req.getDecryptionKey());
        final Request policyRequest = new Request(request.getHeader().clone(),policy);
        Response extractedPolicy;
        try {
          extractedPolicy = gatekeeper.getAttachedModule().take(policyRequest);
        } catch (final CrySILException e) {
          throw new AuthenticationFailedException();
        }
        if (extractedPolicy.getPayload().getType().equals("extractStickyPolicyResponse")) {
          final PayloadExtractStickyPolicyResponse policyResponse = (PayloadExtractStickyPolicyResponse) extractedPolicy
              .getPayload();
          final AuthInfo authInfo = policyResponse.getAuthInfo();
          if (authInfo != null) {
            if (authInfo.getType().equals("challengeRepsonse")) {
              final ChallengeResponseAuthType challenge = new ChallengeResponseAuthType();
              final ChallengeResponseAuthInfo challengeResponseAuthInfo = (ChallengeResponseAuthInfo) authInfo;
              if (challengeResponseAuthInfo.getExpiryDate() < System.currentTimeMillis()) {
                throw new AuthenticationFailedException();
              }
              challenge.setChallenge(challengeResponseAuthInfo.getChallengeString());
              challenge.setQuestion(challengeResponseAuthInfo.isQuestion());
              return new AuthProcess(request,
                  new ChallengeResponseAuthPlugin(challenge, (ChallengeResponseAuthInfo) authInfo));
            }
            // unsupported auth type
          }
        }
        // unsupported auth type
        throw new AuthenticationFailedException();
      }
    }

    // no sticky policy found
    return new AuthProcess(request);
  }

}