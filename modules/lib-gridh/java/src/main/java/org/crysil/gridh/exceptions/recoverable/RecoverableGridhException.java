package org.crysil.gridh.exceptions.recoverable;

import org.crysil.gridh.exceptions.GridhException;

public class RecoverableGridhException extends GridhException {

  private static final long serialVersionUID = 1498223629442227551L;

  public RecoverableGridhException(Throwable t) {
    super(t);
  }

  public RecoverableGridhException(String msg) {
    super(msg);
  }

}
