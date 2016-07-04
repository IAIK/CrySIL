package org.crysil.gridh.io.storage.dropfile;

import java.io.IOException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
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

  static HttpClient createHttpClient(){
    final HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
    return new DefaultHttpClient(params);
  }
}
