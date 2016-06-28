package org.crysil.decentral.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorService {

  private static java.util.concurrent.ExecutorService quick;
  private static java.util.concurrent.ExecutorService longer;

  static {
    quick = Executors.newCachedThreadPool();
    longer = Executors.newCachedThreadPool();
  }

  public static <T> Future<T> submitQuickAsync(final Callable<T> task) {
    return quick.submit(task);
  }

  public static <T> Future<T> submitLongRunning(final Callable<T> task) {
    return longer.submit(task);
  }
}
