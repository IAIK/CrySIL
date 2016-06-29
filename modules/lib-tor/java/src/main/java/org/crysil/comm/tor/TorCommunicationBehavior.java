package org.crysil.comm.tor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.crysil.decentral.DecentralNodeActor;
import org.crysil.decentral.comm.CommunicationBehavior;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.decentral.exceptions.recoverable.IgnorableDecentralException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

public abstract class TorCommunicationBehavior
    implements CommunicationBehavior<String, String, Response, Request>, Runnable {

  protected final DecentralNodeActor<Response, Request> actor;

  public TorCommunicationBehavior(final DecentralNodeActor<Response, Request> actor)
      throws IrrecoverableDecentralException {
    this.actor = actor;
  }

  public abstract void shutdown();

  /**
   * not used
   */
  @Override
  public Object reply(final String sender, final Object request) throws IgnorableDecentralException {
    return null;
  }

  protected static String strFromReader(final BufferedReader rd) throws IOException {

    final StringBuilder bld = new StringBuilder();
    String aLine = null;
    while ((aLine = rd.readLine()) != null) {
      // Workaround; Silvertunnel Sockets behave strange
      if ((aLine.charAt(aLine.length() - 1) == 0) && (aLine.length() == 1)) {
        break;
      }
      bld.append(aLine);
      bld.append("\n");
    }
    // Logger.debug(bld.toString());
    final String string = bld.toString();
    return string.trim();
  }

  protected Response send(final OutputStream outs, final InputStream ins, final Request content)
      throws IrrecoverableDecentralException {
    try {
      final ObjectOutputStream objOut = new ObjectOutputStream(outs);
      objOut.writeObject(content);
      objOut.flush();
      Logger.debug("Data is on the way");
      final ObjectInputStream objIn = new ObjectInputStream(ins);
      return (Response) objIn.readObject();
    } catch (final Exception e) {
      // Whatever happens, we're boned
      throw new IrrecoverableDecentralException(e);
    }

  }

  protected void respond(final OutputStream out, final InputStream in) throws IOException {
    final ObjectOutputStream objOut = new ObjectOutputStream(out);
    final ObjectInputStream objIn = new ObjectInputStream(in);
    Request req;
    try {
      req = (Request) objIn.readObject();
    } catch (final ClassNotFoundException e) {
      throw new IOException(e);
    }
    final Response response = actor.take(req);
    Logger.debug("Got response from actor, sending into network...");
    objOut.writeObject(response);
    objOut.flush();
  }

}
