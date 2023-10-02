package com.johndeweydev.himawhs.models;

public class SerialOutputModel {
  private final String timeInString;
  private final String serialOutputString;


  public SerialOutputModel(String aTimeInString, String aSerialOutputString) {
    timeInString = aTimeInString;
    serialOutputString = aSerialOutputString;
  }

  public String getTimeInString() {
    return timeInString;
  }

  public String getSerialOutputInString() {
    return serialOutputString;
  }
}
