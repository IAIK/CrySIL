package org.crysil.gridh.io.container.web.test;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.crysil.gridh.io.storage.web.HttpsGetInputStream;
import org.crysil.gridh.io.storage.web.HttpsGetURI;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HTTPSInputStreamTest {

  @DataProvider(name = "imgur")
  public Object[][] createData1() {

    return new Object[][] { { "https://i.imgur.com/JOz5kCR.jpg", "1451e64dd436d3e9f97614015855248c" },
        { "https://i.imgur.com/OFFYyMc.gif", "b94d115cf548fff79e93cbeea7245b16" }, };
  }

  @Test(dataProvider = "imgur")
  public void testHTTPSInputStream(String url, String hash) throws IOException, NoSuchAlgorithmException {
    HttpsGetInputStream in = new HttpsGetInputStream(new HttpsGetURI(url), false);
    MessageDigest md = MessageDigest.getInstance("MD5");
    final byte[] download = IOUtils.toByteArray(in);
    final byte[] digest = md.digest(download);
    assertEquals(digest, hexStringToByteArray(hash));
    in.close();
  }

  // from http://www.java2s.com/Code/Java/Data-Type/hexStringToByteArray.htm
  private static byte[] hexStringToByteArray(String s) {
    byte[] b = new byte[s.length() / 2];
    for (int i = 0; i < b.length; i++) {
      int index = i * 2;
      int v = Integer.parseInt(s.substring(index, index + 2), 16);
      b[i] = (byte) v;
    }
    return b;
  }
}
