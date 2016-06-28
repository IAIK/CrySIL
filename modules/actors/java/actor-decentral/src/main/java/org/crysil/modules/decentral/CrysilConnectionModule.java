package org.crysil.modules.decentral;

import org.crysil.commons.Module;
import org.crysil.decentral.DecentralNodeActor;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.NotAcceptableException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.status.PayloadStatus;

public class CrysilConnectionModule implements DecentralNodeActor<Response, Request> {

  private final Module actor;

  public CrysilConnectionModule(final Module actor) {
    this.actor = actor;
  }

  /**
   * Allows only white-listed requests This is another reason why this class
   * does not implements the standard crysil
   * Actor interface: This class shall not be used as an actor, but only on the
   * receiving end of a p2p-connection
   */
  @Override
  public Response take(final Request sRequest) {
    final PayloadRequest payload = sRequest.getPayload();
    if ((payload instanceof PayloadAuthRequest) || (payload instanceof PayloadDecryptRequest)) {
      try {
        return actor.take(sRequest);
			} catch (final CrySILException e) {

        e.printStackTrace();
        final Response response = new Response();

        final Header header = new StandardHeader();
        header.setCommandId(sRequest.getHeader().getCommandId());
        response.setHeader(header);
        final PayloadStatus responsePayload = new PayloadStatus();
        responsePayload.setCode(e.getErrorCode());
        response.setPayload(responsePayload);
        return response;
      }
    }
    final Response response = new Response();
    final Header header = new StandardHeader();
    header.setCommandId(sRequest.getHeader().getCommandId());
    response.setHeader(header);
    final PayloadStatus responsePayload = new PayloadStatus();
    responsePayload.setCode(new NotAcceptableException().getErrorCode());
    response.setPayload(responsePayload);
    return response;
  }

}
