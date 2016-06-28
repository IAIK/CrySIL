package org.crysil.instance.gridh.desktop;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.Theme;
import org.crysil.decentral.concurrent.ExecutorService;
import org.crysil.instance.gridh.desktop.auth.AutomaticAuthSelector;
import org.crysil.logging.Logger;

/**
 * Hello world!
 *
 */
public class App implements Application {
  private GridhWindow window;
  static Frame         splash = null;

  public static void main(final String[] args) {
    final SplashScreen splash = SplashScreen.getSplashScreen();
    if (splash != null) {
      setupSplash(splash.getImageURL(), splash.getBounds());
    }
    Theme.getTheme().set(TextArea.class, FixedTextAreaSkin.class);
    final String props = Arrays.toString(System.getProperties().entrySet().toArray());
    Logger.info("System Properties:\n{}", props.replace(',', '\n').substring(1, props.length() - 1));
    final RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
    final List<String> arguments = runtimeMxBean.getInputArguments();
    Logger.info("VM Arguments: {}", Arrays.toString(arguments.toArray()));
    DesktopApplicationContext.main(App.class, args);
  }

  @Override
  public void resume() throws Exception {
  }

  @Override
  public boolean shutdown(final boolean arg0) throws Exception {
    // TODO Auto-generated method stub
    return false;
  }

  private static void setupSplash(final URL imageUrl, final Rectangle rectangle) {
    if (imageUrl == null) {
      return;
    }
    final Image bg;
    try {
      bg = ImageIO.read(imageUrl);
      splash = new Frame() {

        private static final long serialVersionUID = 1L;

        @Override
        public void paint(final Graphics g) {
          super.paint(g);
          g.drawImage(bg, 0, 0, null);
        }
      };
      splash.setIconImage(bg);
      splash.setAlwaysOnTop(true);
      splash.setUndecorated(true);
      splash.setBackground(new Color(0, 0, 0, 0));
      splash.setVisible(true);

      splash.setBounds(rectangle);
      splash.setLocationRelativeTo(null);
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void startup(final Display arg0, final Map<String, String> arg1) throws Exception {
    ExecutorService.submitLongRunning(new java.util.concurrent.Callable<Void>() {
      @Override
      public Void call() throws Exception {
        final BXMLSerializer bxmlSerializer = new BXMLSerializer();
        ExecutorService.submitLongRunning(new java.util.concurrent.Callable<Void>() {
          @Override
          public Void call() throws Exception {
            // TODO Auto-generated method stub
            window = (GridhWindow) bxmlSerializer.readObject(App.class, "gridhMain.xml");
            AutomaticAuthSelector.setMainWindow(window);
            EventQueue.invokeAndWait(new Runnable() {
              @Override
              public void run() {
                window.open(arg0);
                window.showMasterKeyInput(window.listeners);
                if (splash != null) {
                  try {
                    splash.dispose();
                  } catch (final Exception e) {
                  }
                }
              }
            });
            return null;
          }
        });
        return null;
      }
    });

  }

  @Override
  public void suspend() throws Exception {
    // TODO Auto-generated method stub

  }
}
