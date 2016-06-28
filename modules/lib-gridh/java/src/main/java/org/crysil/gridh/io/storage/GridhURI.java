package org.crysil.gridh.io.storage;

import java.io.IOException;

//TODO extend URI, devise URI scheme
public class GridhURI {

  public static final String   PREFIX_HOST = "https://berndpruenster.org/gridh/";
  private static final char    URI_SEP       = '/';
  private final StorageURI uri;
  private final String         nodeName;

  public GridhURI(StorageURI uri, String nodeName) {
    this.uri = uri;
    this.nodeName = nodeName;
  }

  public GridhURI(String uriString) throws IOException {
    if (!uriString.toLowerCase().startsWith(PREFIX_HOST))
      throw new IOException(uriString + " is not a valid Grið URI");
    String stripped = uriString.substring(PREFIX_HOST.length());
    String uri = stripped.substring(stripped.indexOf("/") + 1);
    nodeName = stripped.substring(0, stripped.indexOf("/"));
    try {
      this.uri = StorageURI.createFromUri(uri);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  public String toString() {
    return PREFIX_HOST + nodeName + URI_SEP + uri.toString();
  }

  public StorageURI getStorageFileURI() {
    return uri;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public StorageInputStream createInputStream() throws IOException {
    return uri.createInputStream();
  }

  public String getNodeName() {
    return nodeName;
  }
}
