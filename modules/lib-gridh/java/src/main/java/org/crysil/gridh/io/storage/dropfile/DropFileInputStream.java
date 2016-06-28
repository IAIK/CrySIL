package org.crysil.gridh.io.storage.dropfile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.crysil.gridh.io.storage.StorageInputStream;
import org.crysil.logging.Logger;

public class DropFileInputStream extends StorageInputStream<DropfileURI> {

  private static final String BASE_URL = "https://d1.dropfile.to";

  private final HttpClient httpClient;

  private final InputStream in;

  private long totalSize, transferred;

  public DropFileInputStream(final DropfileURI uri) throws IOException {
    super(uri);
    // TODO remove
    httpClient = InsecureHTTPClientFactory.createHTTPClient();
    transferred = 0;
    try {
      in = setupStream();
    } catch (final IllegalStateException e) {
      throw new IOException(e);
    }
  }

  private InputStream setupStream() throws IOException {
    final HttpPost httppost = new HttpPost(BASE_URL);
    final List<NameValuePair> params = new ArrayList<>(1);
    params.add(new BasicNameValuePair("dl_file_name", uri.getFileIdentifier()));
    httppost.setEntity(new UrlEncodedFormEntity(params));
    HttpResponse resp = httpClient.execute(httppost);
    Logger.debug("Response Code: {}", resp.getStatusLine().getStatusCode());
    printHeaders(resp);

    if (resp.getStatusLine().getStatusCode() == 302) {
      final String location = resp.getLastHeader("Location").getValue();
      resp.getAllHeaders();
      Logger.debug("Location: {}", location);
      resp.getEntity().consumeContent();
      final HttpGet get = new HttpGet(BASE_URL + location);
      resp = httpClient.execute(get);
      if (resp.getStatusLine().getStatusCode() != 200) {
        throw new IOException("Cannot get Data for Identifier " + uri + ": " + resp.getStatusLine().getStatusCode());
      }
      try {
        totalSize = Long.parseLong(resp.getHeaders(HttpHeaders.CONTENT_LENGTH)[0].getValue());
        fireProgressUpdate(0);
      } catch (final Exception e) {
        Logger.error("Could not get content length");
        totalSize = -1;
        fireProgressUpdate(-1);
      }
      return resp.getEntity().getContent();
    } else if (resp.getStatusLine().getStatusCode() == 200) {
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

  private void printHeaders(final HttpResponse resp) {
    final Header[] allHeaders = resp.getAllHeaders();
    for (final Header h : allHeaders) {
      Logger.debug(h.getName() + ": " + h.getValue());
    }
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
