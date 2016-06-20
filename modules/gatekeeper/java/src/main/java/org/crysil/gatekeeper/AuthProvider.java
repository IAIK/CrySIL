package org.crysil.gatekeeper;

public interface AuthProvider {
  public boolean isValid(AuthResult result);
}
