package org.crysil.gridh.io.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.crysil.logging.Logger;

public abstract class TARIO {

  public static void freeze(final OutputStream outStream, final Set<File> input) throws IOException {
    final TarArchiveOutputStream out = new TarArchiveOutputStream(new XZCompressorOutputStream(outStream));
    for (final File f : input) {
      addFile(out, f.getCanonicalPath(), "");
    }
    Logger.debug("done, closing");
    out.close();
  }

  public static Set<File> thaw(final InputStream input, final File baseDir) throws IOException {
    final Set<File> result = new HashSet<File>();
    final TarArchiveInputStream in = new TarArchiveInputStream(new XZCompressorInputStream(input));
    ArchiveEntry entry;
    while ((entry = in.getNextEntry()) != null) {
      final File f = new File(baseDir.getCanonicalPath() + File.separator + entry.getName().replace('/', File.separatorChar));
      if (entry.isDirectory()) {
        f.mkdirs();
      } else {
        f.getParentFile().mkdirs();
        final FileOutputStream fOut = new FileOutputStream(f);
        IOUtils.copy(in, fOut);
        result.add(f);
        try {
          fOut.close();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    }
    in.close();
    return result;
  }

  private static void addFile(final TarArchiveOutputStream tOut, final String path, final String base) throws IOException {
    Logger.debug("Adding File {}/{}", base, path);
    final File f = new File(path);
    final String entryName = base + f.getName();
    final TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
    tOut.putArchiveEntry(tarEntry);

    if (f.isFile()) {
      final FileInputStream fStream = new FileInputStream(f);
      IOUtils.copy(fStream, tOut);
      tOut.closeArchiveEntry();
      try {
        fStream.close();
      } catch (final IOException e) {
       e.printStackTrace();
      }
    } else {
      tOut.closeArchiveEntry();
      final File[] listFiles = f.listFiles();
      if (listFiles != null) {
        for (final File child : listFiles) {
          addFile(tOut, child.getAbsolutePath(), entryName + "/");
        }
      }
    }
  }
}