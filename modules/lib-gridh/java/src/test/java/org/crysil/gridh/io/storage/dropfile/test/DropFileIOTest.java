package org.crysil.gridh.io.storage.dropfile.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.crysil.gridh.io.storage.dropfile.DropFileInputStream;
import org.crysil.gridh.io.storage.dropfile.DropFileOutputStream;
import org.crysil.gridh.io.storage.dropfile.DropfileURI;
import org.crysil.gridh.io.util.ProgressListener;
import org.crysil.gridh.test.TestUtils;
import org.crysil.logging.Logger;
import org.testng.annotations.Test;

public class DropFileIOTest implements ProgressListener<Float> {

  private static final String DROPFILE_TEST = "dropfileTest";

  @Test(invocationCount = 10, singleThreaded = true, description = "DropFile Upload/Download Test")
  public void testReguarDownload() throws IOException {
    Logger.info("Upload/Download Test");
    final byte[] reference = TestUtils.genRandom();
    final DropfileURI uploadedURI = upload(reference);
    final DropFileInputStream dropFileInputStream = new DropFileInputStream(uploadedURI);
    dropFileInputStream.addProgressListener(this);
    Logger.info("Starting Download");
    final byte[] check = IOUtils.toByteArray(dropFileInputStream);
    dropFileInputStream.close();
    assertEquals(reference, check);
  }

  @Test(invocationCount = 10, singleThreaded = true, description = "DropFile Upload Test")
  public void testRegularUpload() throws IOException, InterruptedException {
    Logger.info("Upload Test");
    final byte[] rand = TestUtils.genRandom();
    final DropfileURI uploadedURI = upload(rand);
    assertTrue(downloadAndCompare(uploadedURI, rand));

  }

  private DropfileURI upload(final byte[] data) throws IOException {
    final DropFileOutputStream dropOut = new DropFileOutputStream();
    dropOut.addProgressListener(this);
    Logger.info("Starting upload of {} bytes of random data...", data.length);
    dropOut.write(data);
    dropOut.close();

    final DropfileURI uploadedURI = dropOut.getUri();
    Logger.info("Download URL: {}", uploadedURI.getSchemeURI());
    return uploadedURI;
  }

  private boolean downloadAndCompare(final DropfileURI uri, final byte[] reference) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder("curl", "-d", "dl_file_name=" + uri.getFileIdentifier(),
        uri.getSchemeURI(), "-v");
    processBuilder.directory(new File(System.getProperty("java.io.tmpdir")));
    Logger.info("Getting Download Location");
    Process wget = processBuilder.start();
    BufferedReader br = new BufferedReader(new InputStreamReader(wget.getErrorStream()));
    String aLine = null;
    String location = null;
    while ((aLine = br.readLine()) != null) {
      System.err.println(aLine);
      if (aLine.startsWith("< Location:")) {
        location = aLine.substring("< Location: ".length(), aLine.length());
      }
    }
    br.close();
    wget.waitFor();
    Logger.info("Location retrieval exit code: {}", wget.exitValue());
    if (wget.exitValue() != 0) {
      throw new IOException("Could not retreive DL location");
    }
    if (location == null) {
      throw new IOException("Could not retreive DL location");
    }
    Logger.info("DL Location: {}", location);
    final String downloadURL = "https://dropfile.to" + location;
    Logger.info("DL URL: {}", downloadURL);
    processBuilder = new ProcessBuilder("curl", downloadURL, "-o", DROPFILE_TEST);
    processBuilder.directory(new File(System.getProperty("java.io.tmpdir")));

    Logger.info("Starting Download...");
    wget = processBuilder.start();

    br = new BufferedReader(new InputStreamReader(wget.getErrorStream()));
    while ((aLine = br.readLine()) != null) {
      System.err.println(aLine);
    }
    br.close();
    wget.waitFor();

    Logger.info("Download finished: (exit code: {})", wget.exitValue());
    if (wget.exitValue() != 0) {
      throw new IOException("Could not Download File");
    }
    final File dropFile = new File(System.getProperty("java.io.tmpdir") + File.separator + DROPFILE_TEST);

    final FileInputStream fileInputStream = new FileInputStream(dropFile);
    if (!dropFile.exists()) {
      fileInputStream.close();
      throw new IOException();
    }
    final long len = dropFile.length();
    if (len != reference.length) {
      Logger.error("Length Mismatch: Extected {}, found {}", reference.length, len);
      dropFile.delete();
      fileInputStream.close();
      return false;
    }
    final byte[] readIn = new byte[(int) len];
    IOUtils.readFully(fileInputStream, readIn);
    fileInputStream.close();
    dropFile.delete();
    return Arrays.equals(reference, readIn);
  }

  @Override
  public void updateProgress(final Float update) {
    System.err.print("\rIO: " + (update < 0 ? (((-1) * update) + "kiB") : ((int) (100 * update) + "%")) + "   ");
  }

  @Override
  public void finished() {
    System.err.println("\rIO: 100%");
    Logger.info("Transfer complete");
  }
}
