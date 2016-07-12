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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class PSKDialog extends JFrame implements IAuthUI<char[], Void> {
  private static final long       serialVersionUID     = 1L;

  private ActionPerformedCallback callbackAuthenticate = null;
  private JPanel                  contentPane;
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

  /**
   * Create the frame.
   */
  public PSKDialog() {
    init(null);
  }

  @Override
  public void init(final Map<String, Void> nothing) {
    setResizable(false);
    setTitle("Pre-Shared Secret Authentication");
    setType(Type.NORMAL);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    setBounds(100, 100, 351, 199);
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);

    final JLabel lblSecret = new JLabel("Enter the pre-shared secret.");
    lblSecret.setFont(new Font("Tahoma", Font.PLAIN, 13));

    final JLabel lblPassword = new JLabel("Secret:");
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
    gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_contentPane.createSequentialGroup().addContainerGap(119, Short.MAX_VALUE)
            .addComponent(btnAuthenticate).addGap(105))
        .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_contentPane.createSequentialGroup().addGap(21).addComponent(lblSecret,
                    GroupLayout.PREFERRED_SIZE, 291, GroupLayout.PREFERRED_SIZE))
                .addGroup(Alignment.LEADING,
                    gl_contentPane.createSequentialGroup().addGap(21)
                        .addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                        .addGap(37).addComponent(textPassword, GroupLayout.PREFERRED_SIZE, 171,
                            GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(27, Short.MAX_VALUE)));
    gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_contentPane.createSequentialGroup().addGap(21).addComponent(lblSecret).addGap(18)
            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup().addGap(3).addComponent(lblPassword,
                    GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
                .addComponent(textPassword, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED, 43, Short.MAX_VALUE).addComponent(btnAuthenticate)
            .addContainerGap()));
    contentPane.setLayout(gl_contentPane);
  }

  /*
   * (non-Javadoc)
   *
   * @see at.iaik.crysil.element.authentication.ui.IAuthDialog#
   * getCallbackAuthenticate()
   */
  @Override
  public ActionPerformedCallback getCallbackAuthenticate() {
    return callbackAuthenticate;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * at.iaik.crysil.element.authentication.ui.IAuthDialog#
   * setCallbackAuthenticate(at.iaik.crysil.element.authentication
   * .ui.ActionPerformedCallback)
   */
  @Override
  public void setCallbackAuthenticate(final ActionPerformedCallback callbackAuthenticate) {
    this.callbackAuthenticate = callbackAuthenticate;
  }

  @Override
  public char[] getAuthValue() {
    return textPassword.getPassword();
  }

  @Override
  public void dismiss() {
    dispose();
  }

}
