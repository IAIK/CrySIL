package org.crysil.gridh.io.storage.local;

import java.io.File;
import java.io.IOException;

import org.crysil.gridh.io.storage.StorageInputStream;
import org.crysil.gridh.io.storage.StorageURI;

public class LocalFileURI extends StorageURI {

  public static final String PREFIX      = "local";
  static final String        PREFIX_FILE = "file:/";

  public LocalFileURI(String uri) {
    super(PREFIX, PREFIX_FILE, uri);
  }

  public LocalFileURI(File file) {
    this(file.toURI().toString());
  }

  @SuppressWarnings("unchecked")
  @Override
  public StorageInputStream<LocalFileURI> createInputStream() throws IOException {
    return new LocalFileInputStream(this);
  }

}
