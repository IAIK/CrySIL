package org.crysil.protocol.payload.auth.debugnoauth;

import java.util.Arrays;

import org.crysil.protocol.payload.auth.AuthType;

public class DebugNoAuthType extends AuthType {

  public static final String TYPE_ID = "no-auth";

  @Override
  public AuthType getBlankedClone() {
    return this;
  }

  @Override
  public String getType() {
    return TYPE_ID;
  }

  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type});
  }

}
