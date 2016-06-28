package org.crysil.gridh.io.storage.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.crysil.gridh.io.storage.StorageInputStream;
import org.crysil.gridh.io.storage.dropfile.InsecureHTTPClientFactory;
import org.crysil.logging.Logger;

public class HttpsGetInputStream extends StorageInputStream<HttpsGetURI> {

  private final HttpClient httpClient;

  private final InputStream in;

  private long totalSize, transferred;

  public HttpsGetInputStream(final HttpsGetURI uri) throws IOException {
    this(uri, true);
  }

  public HttpsGetInputStream(final HttpsGetURI uri, final boolean secure) throws IOException {
    super(uri);
    if (secure) {
      httpClient = new DefaultHttpClient();
    } else{
      Logger.warn("Trusting all certificates for {}! Do not use for production code!", uri.getSchemeURI());
      httpClient = InsecureHTTPClientFactory.createHTTPClient();
    }
    transferred = 0;
    try {
      in = setupStream();
    } catch (final IllegalStateException e) {
      throw new IOException(e);
    }
  }

  private InputStream setupStream() throws IOException {
    final HttpGet httpGet = new HttpGet(uri.getSchemeURI());

    final HttpResponse resp = httpClient.execute(httpGet);
    if (resp.getStatusLine().getStatusCode() == 200) {
      try {
        totalSize = Long.parseLong(resp.getHeaders(HttpHeaders.CONTENT_LENGTH)[0].getValue());
        fireProgressUpdate(0);
      } catch (final Exception e) {
        Logger.error("Could not get content length");
        totalSize = -1;
        fireProgressUpdate(-1);
      }
      return resp.getEntity().getContent();
    }
    throw new IOException("Cannot get Data for Identifier " + uri + ": " + resp.getStatusLine().getStatusCode());
  }

  @Override
  public int read() throws IOException {
    transferred++;
    final int read = in.read();

    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  @Override
  public int read(final byte[] b) throws IOException {
    final int read = in.read(b);
    transferred += read;

    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    final int read = in.read(b, off, len);
    transferred += read;

    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  @Override
  public void fireProgressUpdate(final float f) {
    super.fireProgressUpdate(f);
    if (f == 1) {
      fireProgressFinished();
    }
  }

  @Override
  public void close() throws IOException {
    fireProgressFinished();
    in.close();
  }

}
