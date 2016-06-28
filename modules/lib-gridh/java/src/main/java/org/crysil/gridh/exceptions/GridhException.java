package org.crysil.gridh.exceptions;

public abstract class GridhException extends Throwable {

  private static final long serialVersionUID = 5429138293781743779L;

  public GridhException(Throwable t) {
    super(t);
  }

  public GridhException(String msg) {
    super(msg);
  }
}
