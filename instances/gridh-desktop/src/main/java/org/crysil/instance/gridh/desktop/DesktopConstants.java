package org.crysil.instance.gridh.desktop;

import java.io.File;

public interface DesktopConstants {
  public static final String DIR_CONF      = System.getProperty("user.home") + File.separator + ".gridh";
  public static final String FILE_KEYSTORE = DIR_CONF + File.separator + "gridh.uber";
  public static final String FILE_CONFIG   = DIR_CONF + File.separator + "gridh.properties";
  public static final String DIR_HIDDEN    = DIR_CONF + File.separator + "tor";

  public static final String CONF_PORT               = "port";
  public static final String CONF_PORT_DEFAULT       = "55555";
  public static final String CONF_TOR_NATIVE         = "useNativeTor";
  public static final String CONF_TOR_NATIVE_DEFAULT = "true";
  public static final String CONF_LAST_DIR           = "lastdir";
  public static final String CONF_LAST_DIR_DEFAULT   = System.getProperty("user.home");

  public static enum ErrorCode {
    SETUP_FATAL,
    ERR_GRIDH,
    ERR_CRYSIL,
    ERR_KEYSTORE,
    ERR_CONFIG,
    ERR_INTERNAL,

    ;

    public int toErrorCode() {
      return 255 - this.ordinal();
    }
  }

}
