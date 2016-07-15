package org.crysil.instance.demo;

import java.util.LinkedList;
import java.util.List;

import org.crysil.actor.staticKeyEncryption.StaticKeyEncryptionActor;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.authplugins.AuthDebugNoAuth;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.authentication.ui.SwingAuthenticationSelector;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.gatekeeper.debugnoauth.DebugNoAuthConfiguration;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;

public class Demo {

  public static void main(final String[] args) throws CrySILException {

    // no extra JCE provider required for these basic operations
    final StaticKeyEncryptionActor actor = new StaticKeyEncryptionActor();

    // A configuration tells the gatekeeper how to guard the submodule attached
    // to it
    final DebugNoAuthConfiguration conf = new DebugNoAuthConfiguration();

    // This baby here is supposed to guard the actor;
    // every request passes through it and is checked against the provided
    // configuration to see whether an authentication process needs to be
    // started to actually perform the requested command -- or even deny it in
    // general
    final Gatekeeper cerberus = new Gatekeeper(conf);

    // everything is a module, but some modules are also interlinks
    // other modules can be attached to interlinks
    cerberus.attach(actor);

    // up to now an actor is set up which understand some commands (in this case
    // only very basic commands
    // this actor is protected by a gatekeeper

    // In order to understand the auth challenges issued by the gatekeeper, we
    // need to add an interceptor to the pipeline
    final InterceptorAuth<SwingAuthenticationSelector> auth = new InterceptorAuth<>(
        SwingAuthenticationSelector.class);

    // add plugins to handle different auth types
    final List<AuthHandlerFactory<?, ?, ?>> authPluginFactories = new LinkedList<>();
    authPluginFactories.add(new AuthDebugNoAuth.Factory<>(null));
    // new AuthUsernameAndPassword.Factory<>(UsernameAndPasswordDialog.class));
    auth.setAuthenticationPlugins(authPluginFactories);

    // TODO: do not attach directly, but attach some communications module which
    // talks to a receiver on some remote host (setting this up is also part of
    // the TODO)
    auth.attach(cerberus);

    // prepare a request
    final Request request = new Request();
    request.setHeader(new StandardHeader());
    final PayloadDiscoverKeysRequest payload = new PayloadDiscoverKeysRequest();
    payload.setRepresentation(KeyRepresentation.HANDLE);
    request.setPayload(payload);
    final Response authedRequest = auth.take(request);
    System.out.println(authedRequest);
  }

}
