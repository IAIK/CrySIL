package org.crysil.authentication.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthenticationSelector;

public class SwingAuthenticationSelector extends JFrame implements IAuthenticationSelector {

  private static final long serialVersionUID = 1L;

  /**
   * Launch the application.
   */
  @Override
  public void present() {
    setVisible(true);
  }

  JComboBox<ComboBoxItem>         cmbAuthenticationMethods;
  private ActionPerformedCallback authPluginSelected;

  /**
   * Create the frame.
   */
  public SwingAuthenticationSelector() {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setTitle("Select authentication method");
    setResizable(false);
    setBounds(100, 100, 465, 247);
    setLocationRelativeTo(null);


    cmbAuthenticationMethods = new JComboBox<ComboBoxItem>();
    cmbAuthenticationMethods.setFont(new Font("Tahoma", Font.PLAIN, 16));
    final JButton btnAuthenticate = new JButton("Authenticate");
    btnAuthenticate.setFont(new Font("Tahoma", Font.PLAIN, 20));
    btnAuthenticate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent evt) {
        authPluginSelected.actionPerformed();
      }
    });

    final GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING,
            groupLayout.createSequentialGroup().addGap(59)
                .addComponent(cmbAuthenticationMethods, 0, 348, Short.MAX_VALUE).addGap(52))
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addGap(124)
            .addComponent(btnAuthenticate, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE).addGap(116)));
    groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup().addGap(52)
            .addComponent(cmbAuthenticationMethods, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE)
            .addGap(47)
            .addComponent(btnAuthenticate, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
            .addContainerGap(48, Short.MAX_VALUE)));
    getContentPane().setLayout(groupLayout);
  }

  @Override
  public void setAuthenticationPlugins(final Collection<AuthHandler> authPlugins) {
    cmbAuthenticationMethods.removeAllItems();

    for (final AuthHandler plugin : authPlugins) {
      cmbAuthenticationMethods.addItem(new ComboBoxItem(plugin));
    }

    if (cmbAuthenticationMethods.getItemCount() > 0) {
      cmbAuthenticationMethods.setSelectedIndex(0);
    }
  }

  @Override
  public AuthHandler getSelectedAuthenticationPlugin() {
    return ((ComboBoxItem) cmbAuthenticationMethods.getSelectedItem()).authPlugin;
  }

  public ActionPerformedCallback getAuthPluginSelected() {
    return authPluginSelected;
  }

  @Override
  public void setAuthPluginSelected(final ActionPerformedCallback authPluginSelected) {
    this.authPluginSelected = authPluginSelected;
  }

  public static class ComboBoxItem {
    public AuthHandler authPlugin;

    public ComboBoxItem(final AuthHandler authPlugin) {
      this.authPlugin = authPlugin;
    }

    @Override
    public String toString() {
      return authPlugin.getFriendlyName();
    }
  }

  @Override
  public void dismiss() {
    dispose();
  }

}
