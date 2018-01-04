package org.crysil.authentication.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class SecretDialog extends JFrame implements IAuthUI<char[][], Void> {
  private static final long       serialVersionUID     = 1L;

  private ActionPerformedCallback callbackAuthenticate = null;
  private JPanel                  contentPane;
	private JTextField textSecret;

  /**
   * Launch the application.
   */
  @Override
  public void present() {

		// setCallbackAuthenticate(new ActionPerformedCallback() {
		// @Override
		// public void actionPerformed() {
		// dispose();
		// }
		// });
    setVisible(true);

  }

  public SecretDialog() {
    init(null);
  }

  @Override
  public void init(final Map<String, Void> nothig) {
    setResizable(false);
		setTitle("Secret Authentication");
    setType(Type.NORMAL);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    setBounds(100, 100, 351, 199);
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);

		final JLabel lblSecret = new JLabel("Secret:");
		lblSecret.setFont(new Font("Tahoma", Font.BOLD, 13));

		textSecret = new JTextField();
		textSecret.setFont(new Font("Tahoma", Font.PLAIN, 13));
		textSecret.setColumns(10);

		textSecret.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode())
					callbackAuthenticate.actionPerformed();

			}
		});

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
										.addComponent(lblSecret, GroupLayout.PREFERRED_SIZE, 83,
												GroupLayout.PREFERRED_SIZE)
                    .addGap(37)
										.addComponent(textSecret, GroupLayout.PREFERRED_SIZE, 171,
												GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(13, Short.MAX_VALUE))
        .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
            .addContainerGap(111, Short.MAX_VALUE).addComponent(btnAuthenticate).addGap(105)));
    gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_contentPane.createSequentialGroup().addGap(21)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblSecret)
								.addComponent(textSecret, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE))
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
				textSecret.getText().toCharArray() };
    return res;
  }

  @Override
  public void dismiss() {
    dispose();
  }

}
