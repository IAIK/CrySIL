package org.crysil.communications.websocket;

import java.security.KeyStore;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;

/**
 * Simply uses {@link WebsocketX509TrustManager} to trust server certificates
 */
public class WebsocketTrustManagerFactory extends SimpleTrustManagerFactory {

    @Override
    protected void engineInit(KeyStore keyStore) throws Exception {

    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {

    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{new WebsocketX509TrustManager()};
    }
}
