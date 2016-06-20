package org.crysil.authentication.ui;

import java.util.Collection;

import org.crysil.authentication.AuthenticationPlugin;

public interface IAuthenticationSelector {

  public void setAuthenticationPlugins(Collection<AuthenticationPlugin> authPlugins);

  public void setAuthPluginSelected(ActionPerformedCallback authPluginSelected);

  public AuthenticationPlugin getSelectedAuthenticationPlugin();

  public void dismiss();

  public void present();

}
