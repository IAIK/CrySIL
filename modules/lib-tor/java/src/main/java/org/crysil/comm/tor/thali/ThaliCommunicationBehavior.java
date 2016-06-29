package org.crysil.comm.tor.thali;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.crysil.comm.tor.TorCommunicationBehavior;
import org.crysil.decentral.DecentralNodeActor;
import org.crysil.decentral.concurrent.ExecutorService;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.decentral.exceptions.recoverable.RecoverableDecentralException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

import io.nucleo.net.HiddenServiceDescriptor;
import io.nucleo.net.TorNode;

public class ThaliCommunicationBehavior extends TorCommunicationBehavior {

  private static final int              NUM_TRIES   = 15;
  private static final int              RETRY_SLEEP = 500;
  private boolean                       running;
  @SuppressWarnings("rawtypes")
  private final TorNode                 torNode;
  private final HiddenServiceDescriptor hiddenService;

  public ThaliCommunicationBehavior(final DecentralNodeActor<Response, Request> actor,
      final HiddenServiceDescriptor hiddenService, @SuppressWarnings("rawtypes") final TorNode torNode)
      throws IrrecoverableDecentralException {
    super(actor);
    running = true;
    this.hiddenService = hiddenService;
    this.torNode = torNode;
  }

  @Override
  public Response sendBlocking(final String rcpt, final Request content)
      throws RecoverableDecentralException, IrrecoverableDecentralException {
    final String[] addrString = rcpt.trim().split(":");
    for (int i = 0; i < NUM_TRIES; ++i) {

      try {
        final Socket sock = torNode.connectToHiddenService(addrString[0], Integer.parseInt(addrString[1]));
        final Response resp = send(sock.getOutputStream(), sock.getInputStream(), content);
        sock.close();
        return resp;
      } catch (final UnknownHostException exx) {
        try {
          Logger.debug("Try {} connecting to {} failed, retrying...", i + 1, rcpt);
          Thread.sleep(RETRY_SLEEP);
          continue;
        } catch (final InterruptedException e) {

        }
      } catch (final Exception e) {
        // Whatever else happens, we're boned
        throw new IrrecoverableDecentralException(e);
      }
    }
    throw new IrrecoverableDecentralException("Proxy Error connecting to " + rcpt);
  }

  @Override
  public void run() {
    try {

      while (running) {
        final Socket con = hiddenService.getServerSocket().accept();
        ExecutorService.submitLongRunning(new Callable<Void>() {
          @Override
          public Void call() throws Exception {
            try {
              respond(con.getOutputStream(), con.getInputStream());
              con.close();
            } catch (final Exception e) {
              // TODO proper exception handling
              e.printStackTrace();
            }
            return null;
          }
        });
      }

    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public boolean checkAlive() {
    return true; // TODO, CHECKALIVE
  }

  @Override
  public void shutdown() {
    running = false;
    try {
      hiddenService.getServerSocket().close();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
