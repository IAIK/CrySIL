package org.crysil.decentral.exceptions.recoverable;

import org.crysil.decentral.exceptions.DecentralException;

public class RecoverableDecentralException extends DecentralException {

  private static final long serialVersionUID = -6983895381331251988L;

  public RecoverableDecentralException(final Throwable cause) {
    super(cause);
  }

  public RecoverableDecentralException(final String msg) {
    super(msg);
  }

}
