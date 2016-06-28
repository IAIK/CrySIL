package org.crysil.gridh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.crysil.actor.invertedtrust.InvertedTrustActor;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.commons.Module;
import org.crysil.decentral.DecentralNode;
import org.crysil.decentral.NodeState;
import org.crysil.decentral.NodeStateListener;
import org.crysil.decentral.concurrent.ExecutorService;
import org.crysil.gridh.exceptions.irrecoverable.IrrecoverableGridhException;
import org.crysil.gridh.io.container.CryptContainerInputStream;
import org.crysil.gridh.io.container.CryptContainerOutputStream;
import org.crysil.gridh.io.crypto.CMSDecryptionInputStream;
import org.crysil.gridh.io.crypto.CMSEncryptionOutputStream;
import org.crysil.gridh.io.storage.GridhURI;
import org.crysil.gridh.io.storage.StorageInputStream;
import org.crysil.gridh.io.storage.StorageOutputStream;
import org.crysil.gridh.io.storage.StorageURI;
import org.crysil.gridh.io.util.FileBuffer;
import org.crysil.gridh.io.util.ProgressListener;
import org.crysil.gridh.io.util.TARIO;
import org.crysil.gridh.ipc.DecryptResponse;
import org.crysil.gridh.ipc.EncryptResponse;
import org.crysil.gridh.ipc.ErrorResponse;
import org.crysil.gridh.ipc.GridhResponseListener;
import org.crysil.logging.Logger;
import org.crysil.modules.decentral.DecentralCrysilNode;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public abstract class GridhNode {

  private final Set<NodeStateListener> changeListeners;
  private final CryptoPipe             cryptoPipe;

  protected final DecentralCrysilNode  crysilNode;
  private final GridhAPI               api;
  private ProgressListener<Float>      cryptoProgressListener;
  private ProgressListener<Float>      storageProgressListener;
  private final GridhResponseListener  responseListener;
  private final InvertedTrustActor actor;

  public GridhNode(final DecentralCrysilNode crysilNode, final GridhResponseListener responseListener, final InvertedTrustActor actor) {
    this.responseListener = responseListener;
    this.changeListeners = new HashSet<>();
    this.crysilNode = crysilNode;
    this.actor=actor;
    cryptoPipe = new CryptoPipe();

    api = new GridhAPI(crysilNode,actor);
  }

  @SuppressWarnings("rawtypes")
  protected static DecentralCrysilNode setupCrysilNode(final DecentralNode node, final Module localActor,
      final InterceptorAuth interceptor) {

    final DecentralCrysilNode decentralcrysilNode = new DecentralCrysilNode(node, localActor);
    interceptor.attach(decentralcrysilNode);
    return decentralcrysilNode;
  }

  public void setCryptoProgressListener(final ProgressListener<Float> listener) {
    this.cryptoProgressListener = listener;
  }

  public void setStorageProgressListener(final ProgressListener<Float> listener) {
    this.storageProgressListener = listener;
  }

  public void shutdown() {
    crysilNode.getNode().shutdown();
  }

  public String getName() {
    return crysilNode.getNode().getName();
  }

  public boolean isRunning() {
    return crysilNode.getNode().getState() != NodeState.OFFLINE;
  }

  public NodeState getNetworkState() {
    return crysilNode.getNode().getState();
  }

  public void addChangeListener(final NodeStateListener l) {
    crysilNode.getNode().addChangeListener(l);
    changeListeners.add(l);
  }

  public void removeChangeListener(final NodeStateListener l) {
    crysilNode.getNode().removeChangeListener(l);
    changeListeners.remove(l);
  }

  /**
   * Dispatches a decrypt request to the CryptoPipe
   *
   * @param in
   *          the input stream to read the encrypted container from (will be
   *          closed automatically)
   * @param destination
   *          the identifier of the destination Gridh node to use for key
   *          decryption
   * @param outputDirectory
   *          the output directory to save the decrypted files to (a new
   *          directory will be created for every decryption)
   * @param tempDir
   *          the temporary directory used to store the encrypted container, can
   *          be null. This is useful for remote
   *          input, since the challenge-response procedure can block the
   *          InputStream for too long causing the remote
   *          end to close the connection. All temporary files are deleted upon
   *          successful decryption.
   */
  public <T extends StorageURI> void submitDecryptRequest(final StorageInputStream<T> in,
      final String destination, final File outputDirectory, final File tempDir) {
    ExecutorService.submitLongRunning(new Callable<Void>() {

      @Override
      public Void call() throws Exception {
        try {
          cryptoPipe.decrypt(in, destination, outputDirectory, tempDir);
          responseListener.parseResponse(new DecryptResponse(outputDirectory));
        } catch (final Throwable t) {
          responseListener.parseResponse(new ErrorResponse(t));
        }
        return null;
      }
    });
  }

  /**
   * Dispatches an encryption request to the internal CryptoPipe
   *
   * @param input
   *          the files to encrypt
   * @param out
   *          the StorageOutputStream to write the encrypted container to (will
   *          be closed automatically)
   * @param challenge
   *          the prose challenge used to protect the key
   * @param response
   *          the prose response
   * @param isQuestion
   *          is this challenge a question or task?
   * @throws IOException
   *           in case of an IO Error
   */
  @SuppressWarnings("unchecked")
  public <T extends StorageURI> void submitEncryptRequest(final Set<File> input,
      final StorageOutputStream<T> out, final AuthInfo authInfo) throws IOException {
    final Set<File> files = new HashSet<>();
    for (final File f : input) {
      if (f.exists()) {
        files.add(f);
      } else {
        Logger.info("WARN: File {}  does not exist! Omitting...", f.getName());
      }
    }
    if (files.isEmpty()) {
      throw new FileNotFoundException("Specified Files do not exist");
    }
    ExecutorService.submitLongRunning(new Callable<Void>() {

      @Override
      public Void call() throws Exception {
        try {
          final T uri = (T) cryptoPipe.encrypt(files, out, authInfo);
          responseListener.parseResponse(new EncryptResponse(new GridhURI(uri, getName()), authInfo));
        } catch (final Throwable t) {
          responseListener.parseResponse(new ErrorResponse(t));
        }
        return null;
      }

    });
  }

  private class CryptoPipe implements ProgressListener<Float> {

    public <T extends StorageURI> void decrypt(final StorageInputStream<T> dropIn, final String destination,
        final File outputDirectory, final File tempDir) throws IOException, IrrecoverableGridhException {

      final boolean needTemp = tempDir != null;

      if (storageProgressListener != null) {
        dropIn.addProgressListener(storageProgressListener);
      } else {
        dropIn.addProgressListener(this);
      }
      final CryptContainerInputStream ccIn = new CryptContainerInputStream(dropIn);
      final WrappedKey key = ccIn.getWrappedKey();
      CMSDecryptionInputStream cmsIn;
      if (needTemp) {
        tempDir.mkdirs();
        final File tempFile = new File(tempDir.getCanonicalPath() + File.separator + "GridhDLTemp."
            + GregorianCalendar.getInstance().getTimeInMillis());
        final FileBuffer buf = new FileBuffer(ccIn, tempFile);

        cmsIn = new CMSDecryptionInputStream(buf, key, api, destination);
      } else {
        cmsIn = new CMSDecryptionInputStream(ccIn, key, api, destination);
      }
      if (cryptoProgressListener != null) {
        cmsIn.addProgressListener(cryptoProgressListener);
      }

      final Set<File> files = TARIO.thaw(cmsIn, outputDirectory);
      // no need to close, thaw does it
      for (final File f : files) {
        Logger.info("Decrypted: {}", f.getName());
      }
      if (needTemp) {
        tempDir.delete();
      }

    }

    @SuppressWarnings("resource")
    public <T extends StorageURI> StorageURI encrypt(final Set<File> input,
        final StorageOutputStream<T> dropOut, final AuthInfo authInfo)
        throws IOException, IrrecoverableGridhException {

      final WrappedKey wrappedKey = api.generateWrappedKey(authInfo);
      if (storageProgressListener != null) {
        dropOut.addProgressListener(storageProgressListener);
//      } else {
        dropOut.addProgressListener(this);
      }
      final CryptContainerOutputStream ccOut = new CryptContainerOutputStream(wrappedKey, dropOut);
      final CMSEncryptionOutputStream cmsOut = new CMSEncryptionOutputStream(ccOut,
          wrappedKey,actor);
      if (cryptoProgressListener != null) {
        cmsOut.addProgressListener(cryptoProgressListener);
      }

      // write Tarball
      TARIO.freeze(cmsOut, input);
      // no need to close. freeze does it

      return dropOut.getUri();

    }

    @Override
    public void updateProgress(final Float update) {
      System.out.print("\rDropFile.to Progress: "
          + (update < 0 ? ((long) (update / 1024) + "kiB") : ((int) (update * 100) + "%"))
          + "                   ");
    }

    @Override
    public void finished() {
      System.out.println();
    }
  };

}
