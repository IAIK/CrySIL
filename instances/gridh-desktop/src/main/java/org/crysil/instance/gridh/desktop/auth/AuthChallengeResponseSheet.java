package org.crysil.instance.gridh.desktop.auth;

import java.io.Serializable;
import java.util.Map;

import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.Form.Section;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.ScrollPane;
import org.apache.pivot.wtk.ScrollPane.ScrollBarPolicy;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.SheetCloseListener;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.TablePane.Column;
import org.apache.pivot.wtk.TablePane.Row;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextInput;
import org.crysil.authentication.authplugins.challengeresponse.AuthChallengeResponse;
import org.crysil.authentication.ui.ActionPerformedCallback;
import org.crysil.authentication.ui.IAuthUI;

public class AuthChallengeResponseSheet extends Sheet implements IAuthUI<String, Serializable> {

  private ActionPerformedCallback callAuthenticate;
  private final TextArea          txtChallenge;
  private final TextInput         txtResponse;
  private final PushButton        btnSubmit;
  private final Border            scrollBorder;
  private String                  authValue;

  public AuthChallengeResponseSheet() {
    super();

    final Border border = new Border();
    border.setTitle("Auth Challenge");
    final BoxPane box = new BoxPane();
    final Column col = new Column(1, true);

    final TablePane pane = new TablePane();
    pane.getColumns().add(col);
    btnSubmit = new PushButton("Authenticate");
    try {
      pane.setStyles("{verticalSpacing: '8'}");
      border.setStyles("{padding:'8'}");
      box.setStyles("{horizontalAlignment:'right'}");
      btnSubmit.setStyles("{backgroundColor:17, color:4, font:'sans bold 11', padding:'5'}");
    } catch (final SerializationException e) {
      // https://berndpruenster.org/skynet/svhuk3b6klrlk7zv.onion:55555/dropfile/uviml
      e.printStackTrace();
    }
    Row row = new Row(-1);
    final Form form = new Form();
    txtChallenge = new TextArea();
    txtChallenge.setEditable(false);
    try {
      txtChallenge.setStyles("{wrapText:true}");
    } catch (final SerializationException e) {
    }
    scrollBorder = new Border();
    final ScrollPane scroll = new ScrollPane();
    scroll.setHorizontalScrollBarPolicy(ScrollBarPolicy.FILL_TO_CAPACITY);
    scrollBorder.setMinimumHeight(70);
    scrollBorder.setMinimumWidth(350);
    scrollBorder.setPreferredWidth(350);
    scroll.setView(txtChallenge);
    scrollBorder.setContent(scroll);
    final Section section = new Section();

    section.add(scrollBorder);
    txtResponse = new TextInput();
    txtResponse.setMinimumWidth(350);
    section.add(txtResponse);

    form.getSections().insert(section, 0);
    border.setContent(form);
    row.add(border);
    pane.getRows().add(row);

    box.add(btnSubmit);
    btnSubmit.getButtonPressListeners().add(new ButtonPressListener() {
      @Override
      public void buttonPressed(final Button button) {
        close(true);
      }
    });
    row = new Row(-1);
    row.add(box);
    pane.getRows().add(row);
    setContent(pane);
  }

  @Override
  public void init(final Map<String, Serializable> values) {
    final String challenge = (String) values.get(AuthChallengeResponse.K_CHALLENGE);
    final boolean isQuestion = (Boolean) (values.get(AuthChallengeResponse.K_ISQUESTION));
    Form.setLabel(scrollBorder, isQuestion ? "Question" : "Task");
    Form.setLabel(txtResponse, isQuestion ? "Answer" : "Result");
    txtChallenge.setText(challenge);
  }

  @Override
  public ActionPerformedCallback getCallbackAuthenticate() {
    return callAuthenticate;
  }

  @Override
  public void setCallbackAuthenticate(final ActionPerformedCallback callbackAuthenticate) {
    this.callAuthenticate = callbackAuthenticate;
  }

  @Override
  public void dismiss() {
  }

  @Override
  public String getAuthValue() {
    return authValue;
  }

  @Override
  public void present() {
    this.open(AutomaticAuthSelector.getMainWindow(), new SheetCloseListener() {
      @Override
      public void sheetClosed(final Sheet sheet) {
        authValue = sheet.getResult() ? txtResponse.getText() : null;
        callAuthenticate.actionPerformed();
      }
    });
    txtResponse.requestFocus();
  }
}
