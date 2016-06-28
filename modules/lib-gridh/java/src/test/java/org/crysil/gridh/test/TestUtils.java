package org.crysil.gridh.test;

import java.util.Calendar;
import java.util.Random;

public abstract class TestUtils {

  public static byte[] genRandom() {
    final byte[] rand = new byte[/*1024 **/ new Random().nextInt(5 * 2048) + 1];
    new Random(Calendar.getInstance().getTimeInMillis()).nextBytes(rand);
    return rand;
  }

}
