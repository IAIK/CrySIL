package org.crysil.gridh.io.storage.dropfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;
import org.crysil.gridh.io.storage.StorageOutputStream;
import org.crysil.logging.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Uploads data to DropFile.to, needs a tmpFile, since the size must be known
 * prior to uploading
 *
 * @author Bernd Prünster
 *
 */
public class DropFileOutputStream extends StorageOutputStream<DropfileURI> {

  private DropfileURI            uri;

  private static final String    BASE_URL = "https://d1.dropfile.to/upload";

  private final FileOutputStream out;
  private final File             tmpFile;
  private final CountDownLatch   latch1, latch2;
  private final JsonParser       jsParser;
  private final Random           rnd;
  private final HttpClient       httpClient;

  public DropFileOutputStream() throws IOException {
    latch1 = new CountDownLatch(1);
    latch2 = new CountDownLatch(1);
    rnd = new Random();
    tmpFile = File.createTempFile(Integer.toString(rnd.nextInt(), Character.MAX_RADIX),
        Integer.toString(rnd.nextInt(), Character.MAX_RADIX));
    out = new FileOutputStream(tmpFile);
    jsParser = new JsonParser();
    httpClient = DropfileURI.createHttpClient();
    upload();
  }

  public class CountingHttpEntity extends HttpEntityWrapper {

    private final long totalLength;

    public CountingHttpEntity(final HttpEntity wrapped) {
      super(wrapped);
      this.totalLength = wrapped.getContentLength();
    }

    @Override
    public void writeTo(final OutputStream out) throws IOException {
      this.wrappedEntity
          .writeTo(out instanceof CountingOutputStream ? out : new CountingOutputStream(totalLength, out));
    }
  }

  private class CountingOutputStream extends FilterOutputStream {
    private long       transferred;
    private final long totalLength;

    public CountingOutputStream(final long totalLength, final OutputStream out) {
      super(out);
      this.transferred = 0;
      this.totalLength = totalLength;
      fireProgressUpdate(0);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
      // // NO, double-counting, as super.write(byte[], int, int) delegates to
      // write(int).
      // super.write(b, off, len);
      out.write(b, off, len);
      this.transferred += len;
      fireProgressUpdate(((float) transferred) / ((float) totalLength));
    }

    @Override
    public void write(final int b) throws IOException {
      out.write(b);
      this.transferred++;
      fireProgressUpdate(((float) transferred) / ((float) totalLength));
    }

  }

  private HttpPost prepare() {
    final HttpPost httppost = new HttpPost(BASE_URL);
    final FileBody bin = new FileBody(tmpFile);

    final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    entityBuilder.addPart("file", bin);

    httppost.setEntity(new CountingHttpEntity(entityBuilder.build()));
    return httppost;
  }

  private void upload() {
    final Runnable runnable = new Runnable() {
      @Override
      public void run() {
        Logger.debug("Upload running");
        try {
          latch1.await();
        } catch (final InterruptedException e1) {
          e1.printStackTrace();
        }
        final HttpPost httppost = prepare();
        HttpResponse response;
        try {
          response = httpClient.execute(httppost);
          Logger.debug("upload uri: {}", httppost.getURI());
          final String jsString = EntityUtils.toString(response.getEntity());
          Logger.debug(jsString);
          final JsonObject obj = jsParser.parse(jsString).getAsJsonObject();
          if (obj.get("status").getAsInt() != 0) {
            throw new IOException("Upload Failed");
          }
          final String urlString = obj.get("url").getAsString();
          uri = new DropfileURI(urlString);
        } catch (final ClientProtocolException e) {
          e.printStackTrace();
        } catch (final IOException e) {
          e.printStackTrace();
        }
        tmpFile.delete();
        latch2.countDown();
      }
    };
    new Thread(runnable) {
    }.start();

  }

  @Override
  public DropfileURI getUri() throws IllegalStateException {
    return uri;
  }

  @Override
  public void write(final byte[] b) throws IOException {
    out.write(b);
  }

  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    out.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public void write(final int b) throws IOException {
    out.write(b);
  }

  @Override
  public void close() throws IOException {
    try {
      out.close();
    } catch (final IOException e) {
      fireProgressFinished();
      throw e;
    }
    latch1.countDown();
    try {
      latch2.await();
    } catch (final InterruptedException e) {
      fireProgressFinished();
      e.printStackTrace();
    }
    fireProgressFinished();
    if (uri == null) {
      throw new IOException("Upload Failed");
    }
  }

  @Override
  public void fireProgressUpdate(final float f) {
    super.fireProgressUpdate(f);
    if (f == 1) {
      fireProgressFinished();
    }
  }

  public static String getFriendlyName() {
    return "Dropfile.to";
  }
}
