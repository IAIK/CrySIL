package org.crysil.gridh.ipc;

public abstract class GridhResponse {

  private final GridhResponseType responseType;

  public GridhResponse(GridhResponseType type) {
    this.responseType = type;
  }

  public GridhResponseType getResponseType() {
    return responseType;
  }

}
