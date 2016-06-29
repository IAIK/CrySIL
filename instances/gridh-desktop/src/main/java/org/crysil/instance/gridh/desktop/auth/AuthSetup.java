package org.crysil.instance.gridh.desktop.auth;

import java.util.ArrayList;
import java.util.List;

import org.crysil.authentication.AuthenticationPluginFactory;
import org.crysil.authentication.authplugins.AuthPSK;
import org.crysil.authentication.authplugins.AuthUsernameAndPassword;
import org.crysil.authentication.authplugins.challengeresponse.AuthChallengeResponse;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.authentication.ui.PSKDialog;
import org.crysil.authentication.ui.UsernameAndPasswordDialog;

public abstract class AuthSetup {

  public static InterceptorAuth<AutomaticAuthSelector> setupInterceptor() {
    final List<AuthenticationPluginFactory<?, ?, ?>> authPluginFactories = new ArrayList<AuthenticationPluginFactory<?, ?, ?>>();

    authPluginFactories
        .add(new AuthUsernameAndPassword.Factory<UsernameAndPasswordDialog>(UsernameAndPasswordDialog.class));
    // authPluginFactories.add(new
    // AuthAutomatedProse.Factory<AutomatedProseChallengeDialog>(AutomatedProseChallengeDialog.class));
    authPluginFactories.add(new AuthPSK.Factory<PSKDialog>(PSKDialog.class));
    authPluginFactories
        .add(new AuthChallengeResponse.Factory<AuthChallengeResponseSheet>(AuthChallengeResponseSheet.class));
    final InterceptorAuth<AutomaticAuthSelector> interceptor = new InterceptorAuth<AutomaticAuthSelector>(
        AutomaticAuthSelector.class);
    interceptor.setAuthenticationPlugins(authPluginFactories);
    return interceptor;
  }

}
