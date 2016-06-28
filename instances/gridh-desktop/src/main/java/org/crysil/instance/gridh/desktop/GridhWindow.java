package org.crysil.instance.gridh.desktop;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStoreException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.LinkedList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.ListListener;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.io.FileList;
import org.apache.pivot.util.CalendarDate;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Accordion;
import org.apache.pivot.wtk.AccordionSelectionListener;
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.CalendarButton;
import org.apache.pivot.wtk.CalendarButtonSelectionListener;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentKeyListener;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.DropAction;
import org.apache.pivot.wtk.DropTarget;
import org.apache.pivot.wtk.Keyboard;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListButtonSelectionListener;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.Manifest;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Meter;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.Prompt;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.ScrollPane;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.SheetCloseListener;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.StackPane;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.TablePane.Row;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextAreaContentListener;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextInputContentListener;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.content.ListButtonDataRenderer;
import org.apache.pivot.wtk.content.ListViewItemRenderer;
import org.crysil.actor.invertedtrust.InvertedTrustActor;
import org.crysil.decentral.NodeState;
import org.crysil.decentral.NodeStateListener;
import org.crysil.decentral.concurrent.ExecutorService;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gridh.GridhNode;
import org.crysil.gridh.TorGridhNode;
import org.crysil.gridh.exceptions.irrecoverable.NodeSetupException;
import org.crysil.gridh.io.storage.GridhURI;
import org.crysil.gridh.io.storage.StorageOutputStream;
import org.crysil.gridh.io.storage.StorageURI;
import org.crysil.gridh.io.storage.dropfile.DropFileOutputStream;
import org.crysil.gridh.io.storage.dropfile.DropfileURI;
import org.crysil.gridh.io.storage.local.LocalFileOutputStream;
import org.crysil.gridh.io.storage.local.LocalFileURI;
import org.crysil.gridh.io.storage.web.HttpsGetURI;
import org.crysil.gridh.io.util.ProgressListener;
import org.crysil.gridh.ipc.DecryptResponse;
import org.crysil.gridh.ipc.EncryptResponse;
import org.crysil.gridh.ipc.ErrorResponse;
import org.crysil.gridh.ipc.GridhResponseListener;
import org.crysil.gridh.ipc.GridhResponse;
import org.crysil.instance.gridh.desktop.DesktopConstants.ErrorCode;
import org.crysil.instance.gridh.desktop.UIHelpers.ProgressIndicator;
import org.crysil.instance.gridh.desktop.auth.AuthSetup;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthInfo;

import com.msopentech.thali.java.toronionproxy.JavaOnionProxyContext;
import com.msopentech.thali.java.toronionproxy.JavaOnionProxyManager;
import com.msopentech.thali.toronionproxy.OsData;

import io.nucleo.net.HiddenServiceDescriptor;
import io.nucleo.net.HiddenServiceReadyListener;
import io.nucleo.net.JavaTorNode;
import io.nucleo.net.TorNode;

public class GridhWindow extends Window implements Bindable, GridhResponseListener, NodeStateListener {

  private static final int    STEP_MINUTES  = 5;

  private static final String INFO_DROPFILE = "DropFile uploads are limited to 1GiB!";
  @BXML
  private PushButton          paneEncrypt_btnAddFiles;
  @BXML
  private PushButton          paneEncrypt_btnEncrypt;
  @BXML
  private Label               paneEncrypt_lblOutputInfo;
  @BXML
  private Label               paneEncrypt_lblHSInfo;
  @BXML
  private Label               paneEncrypt_lblHSStatus;
  @BXML
  private PushButton          paneEncrypt_btnChooseOutputFile;
  @BXML
  private ListButton          paneEncrypt_lstDestination;

  @BXML
  private Border              paneEncrypt_fileDropBorder;
  @BXML
  private Label               fileDropLabel;
  @BXML
  private TableView           paneEncrypt_fileTable;
  @BXML
  private Border              paneEncrypt_fileTableBorder;
  @BXML
  private StackPane           stackPane;
  @BXML
  private StackPane           paneEncrypt_fileStackPane;
  @BXML
  private BoxPane             busyPane;
  @BXML
  private Label               lblBusyStatus;
  @BXML
  private ActivityIndicator   busyIndicator;

  @BXML
  private Sheet               masterKeySheet;
  @BXML
  private TextInput           txtMasterKey;
  @BXML
  private PushButton          btnMasterKeyOK;

  @BXML
  private Sheet               settingsSheet;
  @BXML
  private Checkbox            cbUseNativeTor;
  @BXML
  private TextInput           txtHSPort;
  @BXML
  private PushButton          btnSaveSettings;

  @BXML
  private Sheet               encryptSheet;
  @BXML
  private ListButton          lstProseType;
  @BXML
  private TextArea            txtChallenge;
  @BXML
  private TextInput           txtResponse;
  @BXML
  private CalendarButton      calendarExpiry;
  @BXML
  private ListButton          lstExpiryHour;
  @BXML
  private ListButton          lstExpiryMinute;
  @BXML
  private PushButton          btnStartEncryption;

  @BXML
  private Sheet               encryptionStatusSheet;
  @BXML
  private Label               lbsStatusProseType;
  @BXML
  private TextArea            txtStatusChallenge;
  @BXML
  private TextInput           txtStatusResponse;
  @BXML
  private TextInput           txtStatusExpiry;
  @BXML
  private TextArea            txtStatusUri;
  @BXML
  private PushButton          btnEnclyptionStatus;

  @BXML
  private Sheet               operationSheet;
  @BXML
  private TablePane           tblProgress;

  @BXML
  private TextArea            paneDecrypt_txtDecryptUri;
  @BXML
  private Accordion           paneDecrypt_accordion;
  @BXML
  private ScrollPane          paneDecrypt_paneUri;
  @BXML
  private TablePane           paneDecrypt_paneLocal;
  @BXML
  private PushButton          paneDecrypt_btnOpenContainer;
  @BXML
  private TextInput           paneDecrypt_txtDecryptDestination;
  @BXML
  private PushButton          paneDecrypt_btnDecrypt;

  @BXML
  private TableView           tblLog;

  private FileList            fileList;
  private static DropTarget   fileDropTarget;

  private boolean             busy;
  private final String        FORMAT_TIME   = "%02d";
  private Properties          skynetProps;
  private GridhNode           skyNet;
  private InvertedTrustActor  actor;
  private UIProgressListener  cryptoProgressListener, storageProgressListener;

  Listeners                   listeners;

  @Override
  public void initialize(final Map<String, Object> namespace, final URL arg1, final Resources arg2) {
    try {
      StorageURI.register(DropfileURI.class);
      StorageURI.register(LocalFileURI.class);
      StorageURI.register(HttpsGetURI.class);
    } catch (final IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (final NoSuchFieldException e) {
      e.printStackTrace();
    }
    listeners = new Listeners();

    busy = false;
    calendarExpiry.setDisabledDateFilter(UIHelpers.setupDateFilter());
    lstExpiryHour.setDisabledItemFilter(UIHelpers.setupHourFilter(calendarExpiry));
    lstExpiryMinute.setDisabledItemFilter(UIHelpers.setupMinuteFilter(calendarExpiry, lstExpiryHour));

    calendarExpiry.getCalendarButtonSelectionListeners().add(listeners.new CalenderSelectionListener());

    cryptoProgressListener = new UIProgressListener("Cryptography");
    storageProgressListener = new UIProgressListener("Storage");

    tblLog.setTableData(new LinkedList<Map<String, String>>());
    fileList = new FileList();

    // Storage Providers
    UIHelpers.setupStorageChoice(paneEncrypt_lstDestination);
    paneEncrypt_lstDestination.getListButtonSelectionListeners()
        .add(listeners.new DestinationSelectionListenet());
    paneEncrypt_btnChooseOutputFile.getButtonPressListeners().add(listeners.new OutputFileChoiceListener());
    paneEncrypt_btnChooseOutputFile.setDataRenderer(UIHelpers.setupLocationRenderer());

    paneEncrypt_fileTable.setTableData(fileList);
    final DNDUtil dndUtil = new DNDUtil();
    dndUtil.addDropTarget(paneEncrypt_fileStackPane);
    UIHelpers.setupSheetBehavior(encryptSheet, true);
    UIHelpers.setupSheetBehavior(encryptionStatusSheet, true);
    UIHelpers.setupSheetBehavior(operationSheet, false);

    dndUtil.setupFileTable();

    btnSaveSettings.getButtonPressListeners().add(listeners.new SaveSettingsListener());

    paneEncrypt_btnAddFiles.getButtonPressListeners().add(listeners.new AddFilesButtonListener());
    paneEncrypt_btnEncrypt.getButtonPressListeners().add(listeners.new EncryptListener());
    btnStartEncryption.getButtonPressListeners().add(listeners.new StartEncryptionListener());

    btnMasterKeyOK.getButtonPressListeners().add(listeners.new MasterKeyOKListener());
    final Listeners.URIContentListener uriContentListener = listeners.new URIContentListener();
    paneDecrypt_txtDecryptUri.getTextAreaContentListeners().add(uriContentListener);

    paneDecrypt_btnOpenContainer.setDataRenderer(UIHelpers.setupLocationRenderer());

    paneDecrypt_txtDecryptDestination.getTextInputContentListeners()
        .add(listeners.new DecryptDestinationListener());

    paneDecrypt_btnOpenContainer.getButtonPressListeners().add(listeners.new OpenLocalContainerListener());

    paneDecrypt_btnDecrypt.getButtonPressListeners().add(listeners.new DecryptListener());
    paneDecrypt_accordion.getAccordionSelectionListeners()
        .add(listeners.new DecryptAccordionListener(uriContentListener));

    tblLog.getComponentMouseButtonListeners().add(listeners.new LogTableDoubleClickedListener());

    lstExpiryHour.setListData(genRange(0, 24, 1));
    lstExpiryMinute.setListData(genRange(0, 60, STEP_MINUTES));
    lstExpiryHour.setItemRenderer(new TimeRenderer());
    lstExpiryHour.setDataRenderer(new TimeButtonRenderer());
    lstExpiryMinute.setItemRenderer(new TimeRenderer());
    lstExpiryMinute.setDataRenderer(new TimeButtonRenderer());
    lstExpiryHour.setSelectedIndex(0);
    lstExpiryMinute.setSelectedIndex(0);

    try {
      this.skynetProps = readProperties();
      cbUseNativeTor.setSelected(Boolean.parseBoolean(skynetProps
          .getProperty(DesktopConstants.CONF_TOR_NATIVE, DesktopConstants.CONF_TOR_NATIVE_DEFAULT)));
      txtHSPort
          .setText(skynetProps.getProperty(DesktopConstants.CONF_PORT, DesktopConstants.CONF_PORT_DEFAULT));
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          try {
            writeProperties(skynetProps);
          } catch (final IOException e) {
          }
        }
      });
    } catch (final Exception e) {
      displayFatalError("Config Error", e, ErrorCode.ERR_CONFIG);
    }
  }

  private List<Integer> genRange(final int start, final int end, final int step) {
    final List<Integer> minutes = new ArrayList<Integer>();
    for (int i = start; i < end; i += step) {
      minutes.add(i);
    }
    return minutes;
  }

  @SuppressWarnings("unchecked")
  protected void encrypt() {
    final Set<File> files = new HashSet<File>(fileList.getList());
    final boolean q = lstProseType.getSelectedItem().equals("Question");
    final String challenge = txtChallenge.getText();
    final String response = txtResponse.getText();
    @SuppressWarnings("rawtypes")
    StorageOutputStream out;
    try {
      if (paneEncrypt_lstDestination.getSelectedItem().equals(DropFileOutputStream.getFriendlyName())) {
        out = new DropFileOutputStream();
      } else {
        if (UIHelpers.isButtonEmpty(paneEncrypt_btnChooseOutputFile)) {
          Prompt.prompt(MessageType.ERROR, "No destination file set!", this);
          return;
        }
        out = new LocalFileOutputStream(
            ((File) paneEncrypt_btnChooseOutputFile.getButtonData()).getAbsolutePath());
      }
      final ChallengeResponseAuthInfo auth = new ChallengeResponseAuthInfo();
      final Calendar cal = GregorianCalendar.getInstance();
      final CalendarDate selectedDate = calendarExpiry.getSelectedDate();
      cal.set(Calendar.DATE, selectedDate.day + 1);
      cal.set(Calendar.MONTH, selectedDate.month);
      cal.set(Calendar.YEAR, selectedDate.year);
      cal.set(Calendar.HOUR_OF_DAY, (Integer) lstExpiryHour.getSelectedItem());
      cal.set(Calendar.MINUTE, (Integer) lstExpiryMinute.getSelectedItem());
      auth.setExpiryDate(cal.getTimeInMillis());

      auth.setChallengeString(challenge);
      auth.setResponseString(response);
      skyNet.submitEncryptRequest(files, out, auth);
      encryptSheet.close();
      setBusy(true, "Encrypting…");
    } catch (final IOException e) {
      e.printStackTrace();
      setBusy(false);
      Prompt.prompt(MessageType.ERROR, e.getMessage(), this);
    }

  }

  private void checkLocalDecryptDestinationValid() {
    if ((!UIHelpers.isButtonEmpty(paneDecrypt_btnOpenContainer))
        && (!paneDecrypt_txtDecryptDestination.getText().trim().isEmpty())) {
      paneDecrypt_btnDecrypt.setEnabled(true);
    } else {
      paneDecrypt_btnDecrypt.setEnabled(false);
    }
  }

  private void setLocalContainerDestination() {
    final FileDialog fd = new FileDialog(new Frame(), "Choose Container File", FileDialog.SAVE);
    fd.setDirectory(
        skynetProps.getProperty(DesktopConstants.CONF_LAST_DIR, DesktopConstants.CONF_LAST_DIR_DEFAULT));
    fd.setVisible(true);
    final String f = fd.getFile();
    if (f == null || fd.getDirectory() == null) {
      return;
    }
    paneEncrypt_btnChooseOutputFile.setButtonData(new File(fd.getDirectory() + f));
  }

  private void openLocalContainer() {
    final FileDialog fd = new FileDialog(new Frame(), "Open Container File", FileDialog.LOAD);
    fd.setDirectory(
        skynetProps.getProperty(DesktopConstants.CONF_LAST_DIR, DesktopConstants.CONF_LAST_DIR_DEFAULT));
    fd.setVisible(true);
    final String f = fd.getFile();

    if (f != null && fd.getDirectory() == null) {
      Prompt.prompt(MessageType.WARNING,
          "Some files could not be added. This is probably caused by selecting files from a search result or recently used files.",
          this);
      return;
    }

    if (f == null || fd.getDirectory() == null || !new File(fd.getDirectory() + f).exists()) {
      return;
    }

    paneDecrypt_btnOpenContainer.setButtonData(new File(fd.getDirectory() + f));

  }

  private void addFiles() {
    final FileDialog fd = new FileDialog(new Frame(), "Choose a file", FileDialog.LOAD);
    fd.setMultipleMode(true);
    fd.setDirectory(
        skynetProps.getProperty(DesktopConstants.CONF_LAST_DIR, DesktopConstants.CONF_LAST_DIR_DEFAULT));
    fd.setVisible(true);

    final File[] files = fd.getFiles();
    boolean err = false;
    for (final File f : files) {
      if (!f.exists()) {
        err = true;
        continue;
      }
      fileList.add(f);
    }
    if (fd.getDirectory() != null) {
      skynetProps.setProperty(DesktopConstants.CONF_LAST_DIR, fd.getDirectory());
    }
    if (err) {
      Prompt.prompt(MessageType.WARNING,
          "Some files could not be added. This is probably caused by selecting files from a search result or recently used files.",
          this);
    }
  }

  private void setBusy(final boolean busy) {
    setBusy(busy, "Working…");
  }

  private void setBusy(final boolean busy, final String message) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        lblBusyStatus.setText(message);
      }
    });

    if (this.busy == busy) {
      return;
    }
    if (busy) {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          GridhWindow.this.busy = true;
          busyIndicator.setActive(true);
          lblBusyStatus.setText(message);
          stackPane.add(busyPane);
        }
      });
    } else {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          GridhWindow.this.busy = false;

          stackPane.remove(busyPane);
          busyIndicator.setActive(false);
        }
      });
    }
  }

  private Properties readProperties() throws FileNotFoundException, IOException {
    final Properties props = new Properties();
    final File f = new File(DesktopConstants.FILE_CONFIG);
    final String parent = f.getAbsoluteFile().getParent();
    final File confDir = new File(parent);
    if (!confDir.exists()) {
      confDir.mkdirs();
    }
    if (!f.exists()) {
      props.store(new FileOutputStream(f), "");
    }
    props.load(new FileInputStream(DesktopConstants.FILE_CONFIG));
    return props;
  }

  private void writeProperties(final Properties props) throws IOException {
    final File f = new File(DesktopConstants.FILE_CONFIG);
    final FileOutputStream out = new FileOutputStream(f);
    props.store(out, "0xD06EF00D");
    out.close();
  }

  private void setupSkynet() {
    ExecutorService.submitLongRunning(new Callable<Void>() {
      @Override
      public Void call() {
        final Set<NodeStateListener> listeners = new HashSet<NodeStateListener>();
        listeners.add(GridhWindow.this);
        final int port = Integer.parseInt(
            skynetProps.getProperty(DesktopConstants.CONF_PORT, DesktopConstants.CONF_PORT_DEFAULT));
        try {
          Logger.debug("Trying to setup Tor");
          // if (Boolean.parseBoolean(
          // skynetProps.getProperty(DesktopConstants.CONF_TOR_NATIVE,
          // Boolean.toString(true)))) {
          try {
            setBusy(true, "Setting-up Tor");
            final TorNode<JavaOnionProxyManager, JavaOnionProxyContext> tor = new JavaTorNode(
                new File(DesktopConstants.DIR_HIDDEN));
            setBusy(true, "Publishing Hidden Service");
            final HiddenServiceDescriptor hiddenService = tor.createHiddenService(port,
                new HiddenServiceReadyListener() {

                  @Override
                  public void onConnect(final HiddenServiceDescriptor descriptor) {
                    Logger.info("Hidden Service {} successfully published! You are now reachable!",
                        descriptor.getFullAddress());
                    EventQueue.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        paneEncrypt_lblHSStatus.getStyles().put("color", "#00c000");
                      }
                    });
                  }
                });
            Logger.info("Tor Hidden Service running on port {}", hiddenService.getLocalPort());
            skyNet = new TorGridhNode(hiddenService, tor, listeners, actor, GridhWindow.this,
                AuthSetup.setupInterceptor());
            EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                paneEncrypt_lblHSInfo.setText(hiddenService.getFullAddress());
                paneEncrypt_lblHSStatus.setVisible(true);
                paneEncrypt_lblHSStatus.repaint();
                ;
                paneEncrypt_lblHSInfo.repaint();
              }
            });
          } catch (final Throwable e) {
            e.printStackTrace();
            throw new NodeSetupException(e);
          }
          // } else {
          // skyNet = new SilverTunnelSkynetNode(port, new
          // File(DesktopConstants.DIR_HIDDEN), listeners, actor,
          // SkyNetWindow.this, AuthSetup.setupInterceptor());
          // EventQueue.invokeLater(new Runnable() {
          // public void run() {
          // paneEncrypt_lblHSInfo.setText(skyNet.getName());
          // paneEncrypt_lblHSStatus.getStyles().put("color", "#00c000");
          // paneEncrypt_lblHSStatus.setVisible(true);
          // paneEncrypt_lblHSStatus.repaint();
          // paneEncrypt_lblHSInfo.repaint();
          // }
          // });
          // }
          skyNet.setCryptoProgressListener(cryptoProgressListener);
          skyNet.setStorageProgressListener(storageProgressListener);
          setBusy(false);
        } catch (final NodeSetupException e) {
          displayFatalError("Grið Setup Error", e, ErrorCode.SETUP_FATAL);
        }
        return null;
      }
    });
  }

  @Override
  public void stateChanged(final NodeState state) {
    setBusy(state != NodeState.TOR_CONNECTED, state.toString());

  }

  @Override
  public void parseResponse(final GridhResponse response) {

    switch (response.getResponseType()) {
    case DECRYPT:
      final DecryptResponse resp = (DecryptResponse) response;
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          Prompt.prompt(MessageType.INFO,
              "Decryption Successful!\nYour files are here:\n" + resp.getOutputDir().getAbsolutePath(),
              GridhWindow.this, new SheetCloseListener() {
                @Override
                public void sheetClosed(final Sheet sheet) {

                  switch (OsData.getOsType()) {
                  case Linux32:
                  case Linux64:
                    final String[] commands = new String[] {
                        "xdg-open",
                        "gnome-open",
                        "exo-open",
                        "kde-open" };
                    for (final String cmd : commands) {
                      try {
                        final Process exec = Runtime.getRuntime().exec(new String[] {
                            cmd,
                            resp.getOutputDir().getAbsolutePath() });
                        if (exec != null) {
                          Logger.info("Opening file {} with {}", resp.getOutputDir().getAbsolutePath(), cmd);
                          break;
                        }
                      } catch (final IOException e1) {
                        e1.printStackTrace();
                      }
                    }
                    break;
                  case Mac:
                    try {
                      final Process exec = Runtime.getRuntime().exec(new String[] {
                          "open",
                          resp.getOutputDir().getAbsolutePath() });
                      if (exec != null) {
                        Logger.info("Opening file {}", resp.getOutputDir().getAbsolutePath());
                        break;
                      }
                    } catch (final IOException e1) {
                      e1.printStackTrace();
                    }
                    break;
                  default:
                    try {
                      Desktop.getDesktop().open(resp.getOutputDir());
                    } catch (final Exception e) {
                    }
                    break;
                  }

                }
              });
        }
      });
      break;
    case ENCRYPT:
      Logger.info("ENCRYPTED");
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          paneEncrypt_btnChooseOutputFile.setButtonData(null);
          final EncryptResponse encResp = (EncryptResponse) response;
          final ChallengeResponseAuthInfo auth = (ChallengeResponseAuthInfo) encResp.getAuthInfo();
          lbsStatusProseType.setText("Task");
          txtStatusChallenge.setText(auth.getChallengeString());
          txtStatusResponse.setText(auth.getResponseString());
          txtStatusUri.setText(encResp.getUri().toString());
          final Map<String, String> res = new HashMap<String, String>();
          final Calendar cal = GregorianCalendar.getInstance();
          res.put("time", cal.getTime().toString());
          res.put("uri", encResp.getUri().getStorageFileURI().toString());
          res.put("challenge", auth.getChallengeString());
          res.put("ctype", "Task");
          res.put("response", auth.getResponseString());
          cal.setTimeInMillis(auth.getExpiryDate());
          res.put("expiry", cal.getTime().toString());
          @SuppressWarnings("unchecked")
          final List<Map<String, String>> tableData = (List<Map<String, String>>) tblLog.getTableData();
          tableData.add(res);
          txtStatusExpiry.setText(cal.getTime().toString());
          encryptionStatusSheet.open(GridhWindow.this.getDisplay(), GridhWindow.this);
          btnEnclyptionStatus.requestFocus();
        }
      });

      break;
    case ERROR:
      final ErrorResponse errResp = (ErrorResponse) response;
      errResp.getCause().printStackTrace();
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          Throwable cause = errResp.getCause();
          while (!(cause instanceof CrySILException)) {
            cause = cause.getCause();
          }
          if (!(cause instanceof CrySILException)) {

            cause = errResp.getCause();

            Prompt.prompt(MessageType.ERROR, "Grið Operation Error:\n" + cause.getLocalizedMessage(),
                GridhWindow.this);
          } else {
            Prompt.prompt(MessageType.ERROR,
                "Grið Operation Error:\n" + ((CrySILException) cause).getErrorCode(), GridhWindow.this);
          }
        }
      });
      break;
    }
    setBusy(false);
    cryptoProgressListener.finished();
    storageProgressListener.finished();
    ExecutorService.submitQuickAsync(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        Thread.sleep(800);
        if (operationSheet.isOpen()) {
          EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
              operationSheet.close();
            }
          });
        }
        return null;
      }
    });
  }

  private void displayFatalError(final String title, final Throwable t, final ErrorCode errCode) {

    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        Logger.error("{}:\n{}\n Error Code: {}", title, t, errCode);
        Prompt.prompt(MessageType.ERROR,
            t.getLocalizedMessage() + "\nThis is a fatal error.\nGrið will now exit.", GridhWindow.this,
            new SheetCloseListener() {
              @Override
              public void sheetClosed(final Sheet sheet) {
                System.exit(errCode.toErrorCode());
              }
            });

      }
    });
  }

  private void saveSettings() {
    skynetProps.put(DesktopConstants.CONF_PORT, txtHSPort.getText());
    skynetProps.put(DesktopConstants.CONF_TOR_NATIVE, Boolean.toString(cbUseNativeTor.isSelected()));
    settingsSheet.close();
    try {
      writeProperties(skynetProps);
    } catch (final IOException e) {
    }
  }

  private InvertedTrustActor setupActor() throws KeyStoreException, IOException, CrySILException {
    return new InvertedTrustActor(new File(DesktopConstants.FILE_KEYSTORE), txtMasterKey.getText().toCharArray());
  }

  private class Listeners {

    private class MasterKeySheetListener implements SheetCloseListener {
      @Override
      public void sheetClosed(final Sheet sheet) {
        setBusy(true, new File(DesktopConstants.FILE_KEYSTORE).exists() ? "Unlocking key store."
            : "Creating new key store. This may take up to five minutes…");
        ExecutorService.submitLongRunning(new Callable<Void>() {
          @Override
          public Void call() throws Exception {
            tryOpenKeystore();
            return null;
          }
        });
      }
    };

    private class SaveSettingsListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        saveSettings();
      }
    }

    private final class DecryptAccordionListener extends AccordionSelectionListener.Adapter {
      private final Listeners.URIContentListener uriContentListener;

      private DecryptAccordionListener(final Listeners.URIContentListener uriContentListener) {
        this.uriContentListener = uriContentListener;
      }

      @Override
      public void selectedIndexChanged(final Accordion accordion, final int previousSelectedIndex) {
        if (accordion.getSelectedPanel().equals(paneDecrypt_paneUri)) {
          uriContentListener.textChanged(paneDecrypt_txtDecryptUri);
        } else {
          checkLocalDecryptDestinationValid();
        }
      }
    }

    private final class OpenLocalContainerListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        openLocalContainer();
        checkLocalDecryptDestinationValid();
      }
    }

    private final class DecryptDestinationListener extends TextInputContentListener.Adapter {
      @Override
      public void textChanged(final TextInput textInput) {
        checkLocalDecryptDestinationValid();
      }
    }

    private final class CalenderSelectionListener implements CalendarButtonSelectionListener {
      @Override
      public void selectedDateChanged(final CalendarButton calendarButton,
          final CalendarDate previousSelectedDate) {
        if (calendarButton.getSelectedDate().compareTo(new CalendarDate()) == 0) {
          if (((Integer) lstExpiryHour.getSelectedItem()) < GregorianCalendar.getInstance()
              .get(Calendar.HOUR_OF_DAY)) {
            lstExpiryHour.setSelectedItem(GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY));
            if (((Integer) lstExpiryMinute.getSelectedItem()) < GregorianCalendar.getInstance()
                .get(Calendar.MINUTE)) {
              lstExpiryMinute.setSelectedItem(
                  GregorianCalendar.getInstance().get(Calendar.MINUTE) / STEP_MINUTES * STEP_MINUTES);
            }
          }
        }
      }
    }

    private final class LogTableDoubleClickedListener extends ComponentMouseButtonListener.Adapter {
      @Override
      @SuppressWarnings("unchecked")
      public boolean mouseClick(final Component component, final Mouse.Button button, final int x,
          final int y, final int count) {
        if (tblLog.getSelectedIndex() > -1 && count == 2) {
          final Map<String, String> row = ((Map<String, String>) tblLog.getSelectedRow());
          lbsStatusProseType.setText(row.get("ctype"));
          txtStatusChallenge.setText(row.get("challenge"));
          txtStatusResponse.setText(row.get("response"));
          txtStatusExpiry.setText(row.get("expiry"));

          GridhURI uri;
          try {
            uri = new GridhURI(StorageURI.createFromUri(row.get("uri")), skyNet.getName());
            txtStatusUri.setText(uri.toString());
            encryptionStatusSheet.open(GridhWindow.this.getDisplay(), GridhWindow.this);
            btnEnclyptionStatus.requestFocus();
          } catch (final Exception e) {
            displayFatalError("Internal Error!", e, ErrorCode.ERR_INTERNAL);
            e.printStackTrace();
          }
        }
        return true;
      }
    }

    private final class EncryptListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        lstExpiryHour.setSelectedItem(GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY));
        lstExpiryMinute.setSelectedItem(
            GregorianCalendar.getInstance().get(Calendar.MINUTE) / STEP_MINUTES * STEP_MINUTES);
        final GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, 1);
        final CalendarDate date = new CalendarDate(cal);
        calendarExpiry.setSelectedDate(date);
      }
    }

    private final class DecryptListener implements ButtonPressListener {
      @Override
      @SuppressWarnings("unchecked")
      public void buttonPressed(final Button button) {
        try {
          GridhURI uri;
          if (paneDecrypt_accordion.getSelectedPanel().equals(paneDecrypt_paneUri)) {
            uri = new GridhURI(paneDecrypt_txtDecryptUri.getText().trim());
          } else {
            final LocalFileURI fileUri = new LocalFileURI(
                (File) (paneDecrypt_btnOpenContainer.getButtonData()));
            uri = new GridhURI(fileUri, paneDecrypt_txtDecryptDestination.getText().trim());
          }
          final String destination = uri.getNodeName();
          final File outputDirectory = new File(System.getProperty("user.home") + File.separator + "GridhDL"
              + File.separator + GregorianCalendar.getInstance().getTimeInMillis());
          outputDirectory.mkdirs();
          final File tempDir = new File(outputDirectory + File.separator + "tempDL");
          tempDir.mkdirs();
          skyNet.submitDecryptRequest(uri.createInputStream(), destination, outputDirectory, tempDir);
          setBusy(true, "Decrypting…");

        } catch (final IOException e) {
          setBusy(false);
          Prompt.prompt(MessageType.ERROR, e.getClass().getSimpleName() + ": " + e.getLocalizedMessage(),
              GridhWindow.this);
        }
      }
    }

    private final class URIContentListener extends TextAreaContentListener.Adapter {
      @Override
      public void textChanged(final TextArea textArea) {
        try {
          new GridhURI(textArea.getText().trim());
          paneDecrypt_btnDecrypt.setEnabled(true);
        } catch (final Exception e) {
          paneDecrypt_btnDecrypt.setEnabled(false);
        }
      }
    }

    private final class MasterKeyOKListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        masterKeySheet.close();
      }
    }

    private final class StartEncryptionListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        if (paneEncrypt_lstDestination.getSelectedItem().equals(LocalFileOutputStream.getFriendlyName())) {
          if (UIHelpers.isButtonEmpty(paneEncrypt_btnChooseOutputFile)) {
            setLocalContainerDestination();
          }
          if (UIHelpers.isButtonEmpty(paneEncrypt_btnChooseOutputFile)) {
            Prompt.prompt(MessageType.ERROR, "No destination file set!", GridhWindow.this);
            return;
          }
        }
        encrypt();
      }
    }

    private final class AddFilesButtonListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        addFiles();
      }
    }

    private final class OutputFileChoiceListener implements ButtonPressListener {
      @Override
      public void buttonPressed(final Button button) {
        setLocalContainerDestination();
      }
    }

    private final class DestinationSelectionListenet implements ListButtonSelectionListener {
      @Override
      public void selectedItemChanged(final ListButton listButton, final Object previousSelectedItem) {
        final boolean isLocal = listButton.getSelectedItem().equals(LocalFileOutputStream.getFriendlyName());
        paneEncrypt_btnChooseOutputFile.setVisible(isLocal);
        if (isLocal) {
          paneEncrypt_lblOutputInfo.setText("");
        } else {
          paneEncrypt_lblOutputInfo.setText(INFO_DROPFILE);
        }
      }

      @Override
      public void selectedIndexChanged(final ListButton listButton, final int previousSelectedIndex) {
        final boolean isLocal = listButton.getSelectedItem().equals(LocalFileOutputStream.getFriendlyName());
        paneEncrypt_btnChooseOutputFile.setVisible(isLocal);
        if (isLocal) {
          paneEncrypt_lblOutputInfo.setText("");
        } else {
          paneEncrypt_lblOutputInfo.setText(INFO_DROPFILE);
        }
      }
    }

    public void tryOpenKeystore() {
      try {
        actor = setupActor();
        setupSkynet();
      } catch (final Throwable e) {
        e.printStackTrace();
        setBusy(false);
        displayFatalError("KeyStore Error!\nWas the correct password entered?", e, ErrorCode.ERR_KEYSTORE);
      }
    }
  }

  private class UIProgressListener implements ProgressListener<Float> {
    private boolean           known = false;
    private ProgressIndicator unknownIndicator;
    private Meter             knownMeter;
    private final String      label;
    private TablePane.Row     row;

    public UIProgressListener(final String title) {
      this.label = title;
    }

    @Override
    public void updateProgress(final Float update) {
      known = update >= 0;
      EventQueue.invokeLater(new Runnable() {

        @Override
        public void run() {
          if (row == null) {
            row = new Row();
            row.add(new Label(label));
            tblProgress.getRows().add(row);
          }
          if (known) {
            if (knownMeter == null) {
              knownMeter = UIHelpers.getKnownProgressMeter(20, 400);
              row.add(knownMeter);
            }
            knownMeter.setPercentage(update);
            knownMeter.setText((int) (update * 100) + "%");
          } else {
            if (unknownIndicator == null) {
              unknownIndicator = UIHelpers.getUnknownProgressIndicator(20, 20, "");
              row.add(unknownIndicator);
            }
            unknownIndicator.setTitle(((int) (-update / 1024)) + "kiB");
          }
          if (!operationSheet.isOpen()) {
            operationSheet.open(getDisplay(), GridhWindow.this);
          }
        }
      });
    }

    @Override
    public void finished() {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (row == null) {
            return;
          }
          tblProgress.getRows().remove(row);
          if (known) {
            knownMeter = null;
          } else {
            unknownIndicator = null;
          }
          if (tblProgress.getRows().getLength() == 0) {
            operationSheet.close();
          }
          row = null;
        }

      });

    }
  }

  public void showMasterKeyInput(final Listeners listeners) {
    masterKeySheet.open(GridhWindow.this.getDisplay(), GridhWindow.this,
        listeners.new MasterKeySheetListener());
  }

  private class TimeRenderer extends ListViewItemRenderer {
    @Override
    public void render(final Object item, final int index, final ListView listView, final boolean selected,
        final boolean checked, final boolean highlighted, final boolean disabled) {
      if (item instanceof Integer) {
        final String text = String.format(FORMAT_TIME, ((Integer) item).intValue());
        label.setText(text);
      } else {
        super.render(item, index, listView, selected, checked, highlighted, disabled);
      }

    }
  }

  private class TimeButtonRenderer extends ListButtonDataRenderer {
    @Override
    public void render(final Object data, final Button button, final boolean highlight) {
      if (data instanceof Integer) {
        super.render(String.format(FORMAT_TIME, data), button, highlight);
      } else {
        super.render(data, button, highlight);
      }
    }

  }

  private class DNDUtil {
    private void setupFileTable() {
      fileList.getListListeners().add(new ListListener.Adapter<File>() {
        @Override
        public void itemInserted(final List<File> list, final int index) {
          updateFileTable(list.isEmpty());
        }

        @Override
        public void itemsRemoved(final List<File> list, final int index, final Sequence<File> files) {
          updateFileTable(list.isEmpty());
          if (paneEncrypt_fileTable.isFocused() && index < list.getLength()) {
            paneEncrypt_fileTable.setSelectedIndex(index);
          }
        }
      });
      paneEncrypt_fileTable.getComponentKeyListeners().add(new ComponentKeyListener.Adapter() {
        @Override
        public boolean keyPressed(final Component component, final int keyCode,
            final Keyboard.KeyLocation keyLocation) {

          if (keyCode == Keyboard.KeyCode.DELETE) {
            final Sequence<Span> selectedRanges = paneEncrypt_fileTable.getSelectedRanges();
            for (int i = selectedRanges.getLength() - 1; i >= 0; i--) {
              final Span range = selectedRanges.get(i);
              final int index = range.start;
              final int count = range.end - index + 1;
              fileList.remove(index, count);
            }
          } else if ((Keyboard.getModifiers() == Keyboard.Modifier.CTRL.getMask())
              && (keyCode == Keyboard.KeyCode.A)) {
            paneEncrypt_fileTable.selectAll();
          }
          return false;
        }
      });
    }

    private void updateFileTable(final boolean visibility) {
      if (visibility) {
        paneEncrypt_fileStackPane.add(paneEncrypt_fileDropBorder);
      } else {
        paneEncrypt_fileStackPane.remove(paneEncrypt_fileDropBorder);
      }
      paneEncrypt_fileTableBorder.setVisible(!visibility);
      paneEncrypt_btnEncrypt.setEnabled(!visibility);
    }

    private void addDropTarget(final Component comp) {
      comp.setDropTarget(setupDropTarget());
    }

    private DropTarget setupDropTarget() {
      if (fileDropTarget == null) {
        fileDropTarget = new DropTarget() {
          @Override
          public DropAction dragEnter(final Component component, final Manifest dragContent,
              final int supportedDropActions, final DropAction userDropAction) {
            DropAction dropAction = null;

            if (dragContent.containsFileList() && DropAction.COPY.isSelected(supportedDropActions)) {
              dropAction = DropAction.COPY;
            }

            return dropAction;
          }

          @Override
          public void dragExit(final Component component) {
            // empty block
          }

          @Override
          public DropAction dragMove(final Component component, final Manifest dragContent,
              final int supportedDropActions, final int x, final int y, final DropAction userDropAction) {
            return (dragContent.containsFileList() ? DropAction.COPY : null);
          }

          @Override
          public DropAction userDropActionChange(final Component component, final Manifest dragContent,
              final int supportedDropActions, final int x, final int y, final DropAction userDropAction) {
            return (dragContent.containsFileList() ? DropAction.COPY : null);
          }

          @Override
          public DropAction drop(final Component component, final Manifest dragContent,
              final int supportedDropActions, final int x, final int y, final DropAction userDropAction) {
            DropAction dropAction = null;

            if (dragContent.containsFileList()) {
              try {
                final FileList tableData = (FileList) paneEncrypt_fileTable.getTableData();
                final FileList fileListLocal = dragContent.getFileList();
                for (final File file : fileListLocal) {
                  if (file.isDirectory()) {
                    // TODO
                  }

                  tableData.add(file);
                }

                dropAction = DropAction.COPY;
              } catch (final IOException exception) {
                System.err.println(exception);
              }
            }

            dragExit(component);

            return dropAction;
          }
        };
      }
      return fileDropTarget;
    }
  }

  // @SuppressWarnings("rawtypes")
  // private static DecentralNode setupNode(final int port, final File
  // hiddenserviceDir,
  // final Set<NodeStateListener> initialListeners) throws NodeSetupException {
  //
  // try {
  // return new SilvertunnelNode(SilverTunnelUtils.genHiddenSercive(port,
  // hiddenserviceDir),
  // initialListeners);
  // } catch (final IrrecoverableDecentralException e) {
  // throw new NodeSetupException(e);
  // }
  // }

  // private class SilverTunnelSkynetNode extends SkyNetNode {
  // @SuppressWarnings("rawtypes")
  // public SilverTunnelSkynetNode(final int port, final File hiddenSerciveDir,
  // final Set<NodeStateListener> initialListeners, final Actor localActor,
  // final SkyNetResponseListener responseListener, final InterceptorAuth
  // interceptor)
  // throws NodeSetupException {
  // super(setupCrySILNode(setupNode(port, hiddenSerciveDir,
  // initialListeners), localActor, interceptor),
  // responseListener);
  // }
  // }
}
