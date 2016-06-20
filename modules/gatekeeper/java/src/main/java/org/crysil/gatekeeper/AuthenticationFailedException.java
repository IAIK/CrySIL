package org.crysil.gatekeeper;

import org.crysil.errorhandling.CrySILException;

public class AuthenticationFailedException extends CrySILException {

  @Override
  public int getErrorCode() {
    // TODO Auto-generated method stub
    return 609;
  }

}
