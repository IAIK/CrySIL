package org.crysil.authentication.interceptor;

import org.crysil.errorhandling.CrySILException;

public class ResponseInterceptorException extends CrySILException {

  private final String messag;

  public ResponseInterceptorException(final String msg) {
    this.messag = msg;
  }

  @Override
  public String getMessage() {
    return messag;
  }

  @Override
  public int getErrorCode() {
    return 609;
  }

}
