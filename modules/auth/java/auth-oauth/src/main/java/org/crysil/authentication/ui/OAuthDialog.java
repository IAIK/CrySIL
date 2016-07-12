package org.crysil.authentication.ui;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.crysil.authplugins.oauth.AuthOAuth;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class OAuthDialog extends JFrame implements IAuthUI<String, String> {
  private static final long       serialVersionUID     = 1L;

  private ActionPerformedCallback callbackAuthenticate = null;
  private JPanel                  contentPane;
  private String                  token;

  private final static String     magicKeyword         = "code=";

  /**
   * Launch the application.
   */
  @Override
  public void present() {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          final OAuthDialog frame = new OAuthDialog(
              "https://oauth.iaik.tugraz.at/authorization/authorize?response_type=code&client_id=3d5c675487e50b9bcd45aa1bc18a30&redirect_uri=http://localhost:8081/admin/login/oauth_handysignatur&scope=identity_link");
          frame.setCallbackAuthenticate(new ActionPerformedCallback() {
            @Override
            public void actionPerformed() {
              frame.dispose();
            }
          });
          frame.setVisible(true);
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public OAuthDialog() {
  }

  /**
   * Create the frame.
   *
   * @param url
   */
  public OAuthDialog(final String url) {
    final Map<String, String> values = new HashMap<>();
    values.put(AuthOAuth.K_URL, url);
    init(values);
  }

  @Override
  public void init(final Map<String, String> values) {
    setResizable(false);
    setTitle("OAuth Authentication");
    setType(Type.NORMAL);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setBounds(100, 100, 700, 600);
    setLocationRelativeTo(null);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    final String url = values.get(AuthOAuth.K_URL);
    final JFXPanel fxPanel = new JFXPanel();
    contentPane.add(fxPanel);

    Platform.runLater(new Runnable() {

      @Override
      public void run() {
        final WebView browser = new WebView();
        final WebEngine engine = browser.getEngine();
        engine.load(url);

        final StackPane sp = new StackPane();
        sp.getChildren().add(browser);
        final Scene root = new Scene(sp);
        fxPanel.setScene(root);

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
          @Override
          public void changed(final ObservableValue<? extends State> ov, final State oldState,
              final State newState) {
            System.out.println(newState.toString() + " - " + engine.getLocation());
          }
        });

        engine.getLoadWorker().messageProperty().addListener(new ChangeListener<String>() {

          @Override
          public void changed(final ObservableValue<? extends String> arg0, final String arg1,
              final String arg2) {
            System.out.println(arg1 + " -> " + arg2);

            if (arg2.startsWith("Loading ")) {
              if (arg2.contains(magicKeyword)) {
                System.out.println("found magic keyword");
                engine.getLoadWorker().cancel();
                token = arg2.substring(arg2.indexOf(magicKeyword) + magicKeyword.length());
                System.out.println(token);
                callbackAuthenticate.actionPerformed();
              }
            }
          }
        });

        engine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {

          @Override
          public void changed(final ObservableValue<? extends Throwable> arg0, final Throwable arg1,
              final Throwable arg2) {
            arg2.printStackTrace();
          }
        });
      }
    });

    contentPane.setVisible(true);
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
  public String getAuthValue() {
    return token;
  }

  @Override
  public void dismiss() {
    dispose();
  }

}
