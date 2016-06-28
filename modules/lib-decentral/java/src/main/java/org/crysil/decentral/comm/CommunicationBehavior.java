package org.crysil.decentral.comm;

import java.io.Serializable;

import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.decentral.exceptions.recoverable.IgnorableDecentralException;
import org.crysil.decentral.exceptions.recoverable.RecoverableDecentralException;

public interface CommunicationBehavior<SENDER, RCPT, RESP extends Serializable, REQ extends Serializable> {

  /**
   * Sends a Serializable into the p2p network and blocks until a reply is
   * received or a timeout (2 minutes) occurs
   *
   * @param rcpt
   *          the recepient
   * @param content
   *          the content
   * @return the response
   * @throws RecoverableP2PException
   *           in case of a timeout or sending failed
   * @throws IrrecoverableP2PException
   *           in case sending is impossible
   */
  public RESP sendBlocking(RCPT rcpt, REQ content)
      throws RecoverableDecentralException, IrrecoverableDecentralException;

  /**
   * Callback function executed when a message is received
   *
   * @param sender
   *          the PeerAddress of the sending node
   * @param request
   *          The payload of the request
   * @return the response if any
   * @throws Exception
   *           in case no response can or should be returned
   */
  public Object reply(SENDER sender, Object request) throws IgnorableDecentralException;

  public boolean checkAlive();

}
