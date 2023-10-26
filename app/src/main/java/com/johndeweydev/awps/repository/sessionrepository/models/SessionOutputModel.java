package com.johndeweydev.awps.repository.sessionrepository.models;

import com.johndeweydev.awps.repository.LauncherSerialOutputModel;

public class SessionOutputModel implements LauncherSerialOutputModel {
  private final String serialOutputString;

  public SessionOutputModel(String serialOutputString) {
    this.serialOutputString = serialOutputString;
  }
  @Override
  public String getTimeInString() {
    return null;
  }

  @Override
  public String getSerialOutputInString() {
    return serialOutputString;
  }
}
