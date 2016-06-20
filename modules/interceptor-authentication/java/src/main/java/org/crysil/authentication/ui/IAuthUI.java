package org.crysil.authentication.ui;

import java.util.Map;

public interface IAuthUI<T, V> {

  public void init(Map<String, V> values);

  public ActionPerformedCallback getCallbackAuthenticate();

  public void setCallbackAuthenticate(ActionPerformedCallback callbackAuthenticate);

  public void dismiss();

  public T getAuthValue();

  public void present();

}