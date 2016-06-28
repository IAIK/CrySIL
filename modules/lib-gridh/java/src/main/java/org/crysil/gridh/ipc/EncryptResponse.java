package org.crysil.gridh.ipc;

import org.crysil.gridh.io.storage.GridhURI;
import org.crysil.protocol.payload.auth.AuthInfo;

public class EncryptResponse extends GridhResponse {

  private final GridhURI uri;
  private final AuthInfo  authInfo;

  public EncryptResponse(final GridhURI uri, final AuthInfo authInfo) {
    super(GridhResponseType.ENCRYPT);
    this.uri = uri;
    this.authInfo = authInfo;
  }

  public GridhURI getUri() {
    return uri;
  }

  public AuthInfo getAuthInfo() {
    return authInfo;
  }

}
