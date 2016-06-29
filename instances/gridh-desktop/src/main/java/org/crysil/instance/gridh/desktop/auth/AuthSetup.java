package org.crysil.instance.gridh.desktop.auth;

import java.util.ArrayList;
import java.util.List;

import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.authplugins.AuthPSK;
import org.crysil.authentication.authplugins.AuthUsernameAndPassword;
import org.crysil.authentication.authplugins.challengeresponse.AuthChallengeResponse;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.authentication.ui.PSKDialog;
import org.crysil.authentication.ui.UsernameAndPasswordDialog;

public abstract class AuthSetup {

  public static InterceptorAuth<AutomaticAuthSelector> setupInterceptor() {
    final List<AuthHandlerFactory<?, ?, ?>> authPluginFactories = new ArrayList<>();

    authPluginFactories
        .add(new AuthUsernameAndPassword.Factory<>(UsernameAndPasswordDialog.class));
    authPluginFactories.add(new AuthPSK.Factory<>(PSKDialog.class));
    authPluginFactories
        .add(new AuthChallengeResponse.Factory<>(AuthChallengeResponseSheet.class));
    final InterceptorAuth<AutomaticAuthSelector> interceptor = new InterceptorAuth<>(
        AutomaticAuthSelector.class);
    interceptor.setAuthenticationPlugins(authPluginFactories);
    return interceptor;
  }

}
