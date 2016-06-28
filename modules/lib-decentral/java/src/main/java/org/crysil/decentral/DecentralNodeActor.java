package org.crysil.decentral;

import java.io.Serializable;

public interface DecentralNodeActor<RESP extends Serializable, REQ extends Serializable> {

  public RESP take(REQ reuqest);

}
