package org.crysil.gridh.ipc;

import java.io.File;

public class DecryptResponse extends GridhResponse {

  private File outputDir;

  public DecryptResponse(File outputDirectory) {
    super(GridhResponseType.DECRYPT);
    this.outputDir = outputDirectory;
  }

  public File getOutputDir() {
    return outputDir;
  }

}
