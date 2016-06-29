package org.crysil.instance.gridh.desktop.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.pivot.wtk.Window;
import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthenticationSelector;

public class AutomaticAuthSelector implements IAuthenticationSelector {

  private static Window mainWindow;

  public static void setMainWindow(final Window main) {
    mainWindow = main;
  }

  public static Window getMainWindow() {
    return mainWindow;
  }

  private final Set<AuthHandler> authPlugins;
  private ActionPerformedCallback         authpluginSelected;

  public AutomaticAuthSelector() {
    authPlugins = new HashSet<AuthHandler>();
  }

  public void setAuthPluginSelected(final ActionPerformedCallback authPluginSelected) {
    this.authpluginSelected = authPluginSelected;
  }

  public AuthHandler getSelectedAuthenticationPlugin() {
    return authPlugins.iterator().next();
  }

  public void dismiss() {
  }

  public void setAuthenticationPlugins(final Collection<AuthHandler> authPlugins) {
    this.authPlugins.clear();
    for (final AuthHandler plugin : authPlugins) {
      this.authPlugins.add(plugin);
    }
  }

  public void present() {
    authpluginSelected.actionPerformed();
  }

}
