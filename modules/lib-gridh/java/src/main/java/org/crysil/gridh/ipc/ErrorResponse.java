package org.crysil.gridh.ipc;

public class ErrorResponse extends GridhResponse {

  private final Throwable cause;

  public ErrorResponse(Throwable cause) {
    super(GridhResponseType.ERROR);
    this.cause = cause;
  }

  public Throwable getCause() {
    return cause;
  }

}
