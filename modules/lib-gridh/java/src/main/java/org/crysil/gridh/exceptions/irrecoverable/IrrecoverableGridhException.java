package org.crysil.gridh.exceptions.irrecoverable;

import org.crysil.gridh.exceptions.GridhException;

public class IrrecoverableGridhException extends GridhException {

  private static final long serialVersionUID = -3290526136514449213L;

  public IrrecoverableGridhException(Throwable t) {
    super(t);
  }

  public IrrecoverableGridhException(String msg) {
    super(msg);
  }

}
