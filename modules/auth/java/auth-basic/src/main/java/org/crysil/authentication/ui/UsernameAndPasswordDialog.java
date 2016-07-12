package org.crysil.authentication.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class UsernameAndPasswordDialog extends JFrame implements IAuthUI<char[][], Void> {
  private static final long       serialVersionUID     = 1L;

  private ActionPerformedCallback callbackAuthenticate = null;
  private JPanel                  contentPane;
  private JTextField              textUsername;
  private JPasswordField          textPassword;

  /**
   * Launch the application.
   */
  @Override
  public void present() {

    setCallbackAuthenticate(new ActionPerformedCallback() {
      @Override
      public void actionPerformed() {
        dispose();
      }
    });
    setVisible(true);

  }

  public UsernameAndPasswordDialog() {
    init(null);
  }

  @Override
  public void init(final Map<String, Void> nothig) {
    setResizable(false);
    setTitle("Username and Password Authentication");
    setType(Type.NORMAL);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    setBounds(100, 100, 351, 199);
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);

    final JLabel lblUsername = new JLabel("Username:");
    lblUsername.setFont(new Font("Tahoma", Font.BOLD, 13));

    textUsername = new JTextField();
    textUsername.setFont(new Font("Tahoma", Font.PLAIN, 13));
    textUsername.setColumns(10);

    final JLabel lblPassword = new JLabel("Password:");
    lblPassword.setFont(new Font("Tahoma", Font.BOLD, 13));

    textPassword = new JPasswordField();
    textPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
    textPassword.setColumns(10);

    final JButton btnAuthenticate = new JButton("Authenticate");
    btnAuthenticate.setFont(new Font("Tahoma", Font.PLAIN, 15));
    btnAuthenticate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent evt) {
        callbackAuthenticate.actionPerformed();
      }
    });

    final GroupLayout gl_contentPane = new GroupLayout(contentPane);
    gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_contentPane
            .createSequentialGroup().addGap(21).addGroup(gl_contentPane
                .createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                    .addGap(37)
                    .addComponent(textPassword, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(lblUsername, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                    .addGap(37)
                    .addComponent(textUsername, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(13, Short.MAX_VALUE))
        .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
            .addContainerGap(111, Short.MAX_VALUE).addComponent(btnAuthenticate).addGap(105)));
    gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_contentPane.createSequentialGroup().addGap(21)
            .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblUsername)
                .addComponent(textUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE))
            .addGap(18)
            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup().addGap(3).addComponent(lblPassword,
                    GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
                .addComponent(textPassword, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED, 34, Short.MAX_VALUE).addComponent(btnAuthenticate)
            .addContainerGap()));
    contentPane.setLayout(gl_contentPane);
  }

  @Override
  public ActionPerformedCallback getCallbackAuthenticate() {
    return callbackAuthenticate;
  }

  @Override
  public void setCallbackAuthenticate(final ActionPerformedCallback callbackAuthenticate) {
    this.callbackAuthenticate = callbackAuthenticate;
  }

  @Override
  public char[][] getAuthValue() {
    final char[][] res = {
        textUsername.getText().toCharArray(),
        textPassword.getPassword() };
    return res;
  }

  @Override
  public void dismiss() {
    dispose();
  }

}
