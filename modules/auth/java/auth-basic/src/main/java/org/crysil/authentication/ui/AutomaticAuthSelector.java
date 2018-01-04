package org.crysil.authentication.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.crysil.authentication.AuthHandler;

public class AutomaticAuthSelector implements IAuthenticationSelector {

  private final Set<AuthHandler> authPlugins;
  private ActionPerformedCallback         authpluginSelected;

  public AutomaticAuthSelector() {
    authPlugins = new HashSet<>();
  }

  @Override
public void setAuthPluginSelected(final ActionPerformedCallback authPluginSelected) {
    this.authpluginSelected = authPluginSelected;
  }

  @Override
public AuthHandler getSelectedAuthenticationPlugin() {
    return authPlugins.iterator().next();
  }

  @Override
public void dismiss() {
  }

  @Override
public void setAuthenticationPlugins(final Collection<AuthHandler> authPlugins) {
    this.authPlugins.clear();
    for (final AuthHandler plugin : authPlugins) {
      this.authPlugins.add(plugin);
    }
  }

  @Override
public void present() {
    authpluginSelected.actionPerformed();
  }

}
