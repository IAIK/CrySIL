package org.crysil.authentication.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.crysil.authentication.AuthException;
import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthenticationSelector;
import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;

public class InterceptorAuth<T extends IAuthenticationSelector> extends OneToOneInterlink implements Module {
  private List<AuthHandlerFactory<?, ?, ?>> authPluginFactories = new ArrayList<>();
  private final Class<T>                    selectorType;

  public InterceptorAuth(final Class<T> selectorType) {
    this.selectorType = selectorType;
  }

  public void setAuthenticationPlugins(final List<AuthHandlerFactory<?, ?, ?>> authPluginFactories) {
    this.authPluginFactories = authPluginFactories;
  }

  @Override
  public Response take(final Request request) throws CrySILException {
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

  public Response intercept(final Response crysilResponse) throws CrySILException {
    Logger.debug("Intercepting {}", crysilResponse.getBlankedClone());
    final List<AuthHandler> myAuthPlugins = new ArrayList<>();

    for (final AuthType authType : ((PayloadAuthResponse) crysilResponse.getPayload()).getAuthTypes()) {
      for (final AuthHandlerFactory factory : authPluginFactories) {

        try {
          if (factory.canTake(crysilResponse, authType)) {
            final AuthHandler authPlugin = factory.createInstance(crysilResponse, authType,
                factory.getDialogType());

            myAuthPlugins.add(authPlugin);
          }
        } catch (final AuthException e) {
          throw new ResponseInterceptorException("Error forwarding request");
        }

      }
    }

    if (myAuthPlugins.isEmpty()) {
      Logger.error("No suitable auth plugin availabe to deal with {}", crysilResponse.getBlankedClone());
      throw new ResponseInterceptorException("No suitable authentication plugin available");
    }

    try {

      final PayloadAuthRequest authRequestPayload = new PayloadAuthRequest();
      authRequestPayload.setAuthInfo(presentSelector(myAuthPlugins).authenticate());
      final Request request = new Request(crysilResponse.getHeader().clone(), authRequestPayload);
      request.getHeader().responseToRequestPath();
      return getAttachedModule().take(request);

    } catch (InterruptedException | AuthException | UnsupportedRequestException e) {
      throw new ResponseInterceptorException("Error selecting authentication plugin");
    }
  }

  private AuthHandler presentSelector(final List<AuthHandler> authPlugins) throws InterruptedException {

    final AtomicReference<AuthHandler> authPlugin = new AtomicReference<>();

    if (authPlugins.size() == 1 && authPlugins.get(0).authenticatesAuthomatically()) {
      return authPlugins.get(0);
    }

		try {
			Executors.newSingleThreadExecutor().submit(new Runnable() {
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
							}
						});
						selector.present();

					} catch (final InstantiationException e) {
						e.printStackTrace();
					} catch (final IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}).get();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    return authPlugin.get();
  }

  public boolean canTake(final Response crysilResponse) {
    return (crysilResponse.getPayload() instanceof PayloadAuthResponse);
  }

}
