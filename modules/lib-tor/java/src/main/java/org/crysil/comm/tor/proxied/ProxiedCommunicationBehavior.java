package org.crysil.comm.tor.proxied;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.crysil.comm.tor.TorCommunicationBehavior;
import org.crysil.communications.json.JsonUtils;
import org.crysil.decentral.DecentralNodeActor;
import org.crysil.decentral.concurrent.ExecutorService;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.decentral.exceptions.recoverable.RecoverableDecentralException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import com.runjva.sourceforge.jsocks.protocol.SocksSocket;

public class ProxiedCommunicationBehavior extends TorCommunicationBehavior {

  private static final String PROXY_LOCALHOST = "127.0.0.1";
  private static final int    NUM_TRIES       = 15;
  private static final int    RETRY_SLEEP     = 500;
  private final Socks5Proxy   proxy;
  private final int           serverPort;
  private final boolean       running;

  public ProxiedCommunicationBehavior(final DecentralNodeActor<Response, Request> actor, final int proxyPort,
      final int serverPort) throws IrrecoverableDecentralException {
    super(actor);
    running = true;
    this.serverPort = serverPort;
    try {
      this.proxy = new Socks5Proxy(PROXY_LOCALHOST, proxyPort);
    } catch (final UnknownHostException e) {
      // if this ever happens, something is fishy
      throw new IrrecoverableDecentralException(e);
    }
    proxy.resolveAddrLocally(false);
  }

  @Override
  public Response sendBlocking(final String rcpt, final Request content)
      throws RecoverableDecentralException, IrrecoverableDecentralException {
    final String[] addrString = rcpt.trim().split(":");
    for (int i = 0; i < NUM_TRIES; ++i) {

      try (final SocksSocket ssock = new SocksSocket(proxy, addrString[0], Integer.parseInt(addrString[1]))) {

        final String resp = send(ssock.getOutputStream(), ssock.getInputStream(), JsonUtils.toJson(content));
        ssock.close();
        return JsonUtils.fromJson(resp, Response.class);
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

    try (final ServerSocket sock = new ServerSocket()) {

      sock.bind(new InetSocketAddress(serverPort));
      while (running) {
        final Socket con = sock.accept();
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
    // TODO Auto-generated method stub

  }

}
