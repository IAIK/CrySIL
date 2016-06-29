package org.crysil.actor.invertedtrust;

import java.io.IOException;

import org.bouncycastle.cms.CMSException;
import org.crysil.actor.storage.CryptoContainer;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyRequest;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyResponse;

public class ExtractStickyPolicy implements Command {

  private final SingleKeyStore keyStore;

  public ExtractStickyPolicy(final SingleKeyStore keyStore) {
    this.keyStore = keyStore;
  }

  @Override
  public PayloadResponse perform(final PayloadRequest input) throws CrySILException {
    final PayloadExtractStickyPolicyRequest request = (PayloadExtractStickyPolicyRequest) input;
    try {
      final CryptoContainer container = keyStore.extractKey(request.getKeys().get(0));

      final PayloadExtractStickyPolicyResponse response = new PayloadExtractStickyPolicyResponse();
      response.setAuthInfo(container.getStickyPolicy());
      return response;
    } catch (ClassNotFoundException | IOException | CMSException e) {
      throw new UnknownErrorException();
    }
  }

}
