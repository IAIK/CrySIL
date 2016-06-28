package org.crysil.comm.tor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.crysil.communications.json.JsonUtils;
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

  protected String send(final OutputStream outs, final InputStream ins, final String content)
      throws IrrecoverableDecentralException {
    try {

      final BufferedWriter bOut = new BufferedWriter(new OutputStreamWriter(outs, "UTF-8"));
      final BufferedReader bIn = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
      bOut.write(content);
      Logger.debug("Wrote everything, closing....");
      bOut.write("\n");
      bOut.write(0); // Workaround for SilverTunnel Sockets
      bOut.write("\n");
      bOut.flush();

      Logger.debug("Data is on the way");
      final String in = strFromReader(bIn);

      return in;
    } catch (final Exception e) {
      // Whatever happens, we're boned
      throw new IrrecoverableDecentralException(e);
    }

  }

  protected void respond(final OutputStream out, final InputStream in) throws IOException {
    final BufferedWriter bOut = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    final BufferedReader bIn = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    final Request req = JsonUtils.fromJson(strFromReader(bIn), Request.class);
    final Response response = actor.take(req);
    Logger.debug("Got response from actor, sending into network...");
    bOut.write(JsonUtils.toJson(response));
    bOut.write("\n");
    bOut.write(0); // Workaround for SilverTunnel Sockets
    bOut.write("\n");
    bOut.flush();
  }

}
