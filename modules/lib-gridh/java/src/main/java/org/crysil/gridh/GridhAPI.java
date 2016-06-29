package org.crysil.gridh;

import java.io.InputStream;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CryptoException;
import org.crysil.actor.invertedtrust.InvertedTrustActor;
import org.crysil.cms.CmsEnvelopedInputStream;
import org.crysil.commons.Module;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.gridh.exceptions.irrecoverable.IrrecoverableGridhException;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class GridhAPI {

  private final Module node;
  private final InvertedTrustActor  actor;

  public GridhAPI(final Module node, final InvertedTrustActor actor) {
    this.actor = actor;
    this.node = node;
  }

  public WrappedKey generateWrappedKey(final AuthInfo authInfo) throws IrrecoverableGridhException {
    try {
      return actor.genWrappedKey(authInfo);
    } catch (final UnsupportedRequestException e) {
      throw new IrrecoverableGridhException(e);
    }
  }

  public InputStream setupDecryptionStream(final InputStream in, final String destination,
      final WrappedKey wrappedKey) throws IrrecoverableGridhException {

    CmsEnvelopedInputStream cmsIn;
    try {
      cmsIn = InvertedTrustActor.genCMSInputStream(in, node, wrappedKey, destination);
    } catch (final CMSException e) {
      Throwable t = e;
      do {
        if (t instanceof CryptoException) {
          throw new IrrecoverableGridhException(t);
        }
      } while ((t = e.getCause()) != null);
      throw new IrrecoverableGridhException(e);
    }
    return cmsIn;

  }
}
