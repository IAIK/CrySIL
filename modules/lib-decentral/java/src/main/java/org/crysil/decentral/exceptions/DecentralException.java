package org.crysil.decentral.exceptions;

public abstract class DecentralException extends Throwable {
  private static final long serialVersionUID = -3993915181292478078L;

  public DecentralException(final Throwable cause) {
    super(cause);
  }

  public DecentralException(final String msg) {
    super(msg);
  }
}
