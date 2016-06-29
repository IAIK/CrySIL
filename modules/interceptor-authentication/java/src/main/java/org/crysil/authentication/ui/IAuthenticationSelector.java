package org.crysil.authentication.ui;

import java.util.Collection;

import org.crysil.authentication.AuthHandler;

public interface IAuthenticationSelector {

  public void setAuthenticationPlugins(Collection<AuthHandler> authPlugins);

  public void setAuthPluginSelected(ActionPerformedCallback authPluginSelected);

  public AuthHandler getSelectedAuthenticationPlugin();

  public void dismiss();

  public void present();

}
