package org.crysil.gridh.io.storage.web;

import java.io.IOException;

import org.crysil.gridh.io.storage.StorageInputStream;
import org.crysil.gridh.io.storage.StorageURI;

public class HttpsGetURI extends StorageURI {

  public static final String  PREFIX       = "https";
  private static final String PREFIX_HTTPS = "https:";

  public HttpsGetURI(String uri) {
    super(PREFIX, PREFIX_HTTPS, uri);
  }

  @SuppressWarnings("unchecked")
  @Override
  public StorageInputStream<HttpsGetURI> createInputStream() throws IOException {
    return new HttpsGetInputStream(this);
  }
}
