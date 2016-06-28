package org.crysil.gridh.io.util;

public interface ProgressListener<T> {

  public void updateProgress(T update);
  
  public void finished();
  
}
