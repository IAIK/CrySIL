package org.crysil.gridh.io.storage.dropfile;

import java.io.IOException;

import org.crysil.gridh.io.storage.StorageInputStream;
import org.crysil.gridh.io.storage.StorageURI;

public class DropfileURI extends StorageURI {

  public static final String  PREFIX       = "dropfile";
  private static final String PREFIX_HTTPS = "https://dropfile.to/";

  public DropfileURI(final String uri) {
    super(PREFIX, PREFIX_HTTPS, uri);
  }

  @SuppressWarnings("unchecked")
  @Override
  public StorageInputStream<DropfileURI> createInputStream() throws IOException {
    return new DropFileInputStream(this);
  }
}
