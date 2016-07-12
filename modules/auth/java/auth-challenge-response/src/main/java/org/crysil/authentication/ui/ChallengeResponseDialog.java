package org.crysil.authentication.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.crysil.authentication.authplugins.challengeresponse.AuthChallengeResponse;

public class ChallengeResponseDialog extends JFrame implements IAuthUI<String, Serializable> {
  private static final long       serialVersionUID     = 1L;

  private ActionPerformedCallback callbackAuthenticate = null;
  private JPanel                  contentPane;
  private JTextArea               labelProseChallenge;
  private JTextField              textResult;

  /**
   * Launch the application.
   */
  @Override
  public void present() {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        setVisible(true);
      }
    });
  }

  public ChallengeResponseDialog() {

  }

  /**
   * Create the frame.
   */
  public ChallengeResponseDialog(final String challenge, final boolean isQuestion) {
    final Map<String, Serializable> values = new HashMap<>();
    values.put(AuthChallengeResponse.K_CHALLENGE, challenge);
    values.put(AuthChallengeResponse.K_ISQUESTION, isQuestion);
    init(values);
  }

  @Override
  public void init(final Map<String, Serializable> values) {
    setResizable(false);
    setTitle("Challenge-Response Authentication");
    setType(Type.NORMAL);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    setBounds(100, 100, 351, 199);
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    final JLabel lblUsername = new JLabel("Do the following and fill in the result:");
    lblUsername.setFont(new Font("Tahoma", Font.BOLD, 13));

    labelProseChallenge = new JTextArea((String) values.get(AuthChallengeResponse.K_CHALLENGE));
    labelProseChallenge.setLineWrap(true);
    labelProseChallenge.setWrapStyleWord(true);
    labelProseChallenge.setEditable(false);
    labelProseChallenge.setFont(new Font("Tahoma", Font.PLAIN, 13));

    final JLabel lblResult = new JLabel("Result:");
    lblResult.setFont(new Font("Tahoma", Font.BOLD, 13));

    textResult = new JPasswordField();
    textResult.setFont(new Font("Tahoma", Font.PLAIN, 13));
    textResult.setColumns(10);

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
        .addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup().addGap(21).addGroup(gl_contentPane
            .createParallelGroup(Alignment.LEADING)
            .addComponent(labelProseChallenge, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(lblResult, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                    .addGap(37)
                    .addComponent(textResult, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE))
                .addComponent(lblUsername, GroupLayout.PREFERRED_SIZE, 291, GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(27, Short.MAX_VALUE)));
    gl_contentPane
        .setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
            gl_contentPane.createSequentialGroup().addGap(21).addComponent(lblUsername)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(labelProseChallenge, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_contentPane.createSequentialGroup().addGap(3).addComponent(lblResult,
                        GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
                    .addComponent(textResult, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnAuthenticate)
                .addContainerGap()));
    contentPane.setLayout(gl_contentPane);
  }

  @Override
  public String getAuthValue() {
    return textResult.getText();
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
  public void dismiss() {
    dispose();
  }

}
