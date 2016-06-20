package org.crysil.authentication.interceptor;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.crysil.authentication.AuthenticationPlugin;
import org.crysil.authentication.AuthenticationPluginException;
import org.crysil.authentication.AuthenticationPluginFactory;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthenticationSelector;
import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;

public class InterceptorAuth<T extends IAuthenticationSelector> extends OneToOneInterlink implements Module {
  private List<AuthenticationPluginFactory<?, ?, ?>> authPluginFactories = new ArrayList<>();
  private final Class<T>                             selectorType;

  public InterceptorAuth(final Class<T> selectorType) {
    this.selectorType = selectorType;
  }

  public void setAuthenticationPlugins(final List<AuthenticationPluginFactory<?, ?, ?>> authPluginFactories) {
    this.authPluginFactories = authPluginFactories;
  }

  @Override
  public Response take(final Request request) throws UnsupportedRequestException {
    final Response resp = this.getAttachedModule().take(request);

    if (resp.getPayload() instanceof PayloadAuthResponse) {
      try {
        return intercept(resp);
      } catch (final ResponseInterceptorException e) {
        throw new UnsupportedRequestException();
      }
    }
    Logger.debug("Nothing to intercept for {}", resp.getBlankedClone());
    return resp;
  }

  public Response intercept(final Response crysilResponse) throws ResponseInterceptorException {
    Logger.debug("Intercepting {}", crysilResponse.getBlankedClone());
    final List<AuthenticationPlugin> myAuthPlugins = new ArrayList<>();

    for (final AuthType authType : ((PayloadAuthResponse) crysilResponse.getPayload()).getAuthTypes()) {
      for (final AuthenticationPluginFactory factory : authPluginFactories) {

        try {
          if (factory.canTake(crysilResponse, authType)) {
            final AuthenticationPlugin authPlugin = factory.createInstance(crysilResponse, authType,
                factory.getDialogType());

            myAuthPlugins.add(authPlugin);
          }
        } catch (final AuthenticationPluginException e) {
          throw new ResponseInterceptorException("Error forwarding request");
        }

      }
    }

    if (myAuthPlugins.isEmpty()) {
      Logger.error("No suitable auth plugin availabe to deal with {}", crysilResponse.getBlankedClone());
      throw new ResponseInterceptorException("No suitable authentication plugin available");
    }

    try {

      final Request authChalengeReply = showAuthenticationSelector(myAuthPlugins).authenticate();
      return getAttachedModule().take(authChalengeReply);

    } catch (InterruptedException | AuthenticationPluginException | UnsupportedRequestException e) {
      throw new ResponseInterceptorException("Error selecting authentication plugin");
    }
  }

  private AuthenticationPlugin showAuthenticationSelector(final List<AuthenticationPlugin> authPlugins)
      throws InterruptedException {

    final AtomicReference<AuthenticationPlugin> authPlugin = new AtomicReference<>();

    if (authPlugins.size() == 1 && authPlugins.get(0).authenticatesAuthomatically()) {
      return authPlugins.get(0);
    }
    final CountDownLatch latch = new CountDownLatch(1);

    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          final IAuthenticationSelector selector = selectorType.newInstance();
          selector.setAuthenticationPlugins(authPlugins);
          selector.setAuthPluginSelected(new ActionPerformedCallback() {
            @Override
            public void actionPerformed() {
              authPlugin.set(selector.getSelectedAuthenticationPlugin());
              selector.dismiss();
              latch.countDown();
            }
          });
          selector.present();

        } catch (final InstantiationException e) {
          latch.countDown();
          e.printStackTrace();
        } catch (final IllegalAccessException e) {
          latch.countDown();
          e.printStackTrace();
        }
      }
    });

    latch.await();

    return authPlugin.get();
  }

  public boolean canTake(final Response crysilResponse) {
    return (crysilResponse.getPayload() instanceof PayloadAuthResponse);
  }

}
