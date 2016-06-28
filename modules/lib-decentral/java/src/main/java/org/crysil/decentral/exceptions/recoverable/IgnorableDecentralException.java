package org.crysil.decentral.exceptions.recoverable;

public class IgnorableDecentralException extends RecoverableDecentralException {

  private static final long serialVersionUID = 4236539876311299750L;

  public IgnorableDecentralException(final Throwable cause) {
    super(cause);
  }

  public IgnorableDecentralException(final String msg) {
    super(msg);
  }

}
