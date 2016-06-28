package org.crysil.gridh;

import java.io.InputStream;
import org.bouncycastle.cms.CMSException;
import org.crysil.actor.invertedtrust.InvertedTrustActor;
import org.crysil.cms.CmsEnvelopedInputStream;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.gridh.exceptions.irrecoverable.IrrecoverableGridhException;
import org.crysil.modules.decentral.DecentralCrysilNode;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class GridhAPI{

  private final DecentralCrysilNode node;
  private final InvertedTrustActor  actor;

  public GridhAPI(final DecentralCrysilNode node, final InvertedTrustActor actor) {
    this.actor = actor;
    this.node = node;
  }

  public WrappedKey generateWrappedKey(final AuthInfo authInfo) throws IrrecoverableGridhException {
    node.setDestinationNode(DecentralCrysilNode.DST_LOCAL);
    try {
      return actor.genWrappedKey();
    } catch (final UnsupportedRequestException e) {
      throw new IrrecoverableGridhException(e);
    }
  }

  public InputStream setupDecryptionStream(final InputStream in, final String destination,
      final WrappedKey wrappedKey) throws IrrecoverableGridhException {

    node.setDestinationNode(destination);
    CmsEnvelopedInputStream cmsIn;
    try {
      cmsIn = InvertedTrustActor.genCMSInputStream(in, node, wrappedKey);
    } catch (final CMSException e) {
      throw new IrrecoverableGridhException(e);
    }
    node.releaseDestinationNode();
    return cmsIn;

  }
}
