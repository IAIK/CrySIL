package org.crysil.decentral.exceptions.irrecoverable;

import org.crysil.decentral.exceptions.DecentralException;

public class IrrecoverableDecentralException extends DecentralException {

  private static final long serialVersionUID = 1L;

  public IrrecoverableDecentralException(final Throwable cause) {
    super(cause);
  }

  public IrrecoverableDecentralException(final String msg) {
    super(msg);
  }

}
