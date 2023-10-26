package com.johndeweydev.awps.repository.usbserialrepository;

import com.johndeweydev.awps.repository.LauncherSerialOutputModel;

public class UsbSerialOutputModel implements LauncherSerialOutputModel {
  private final String timeInString;
  private final String serialOutputString;


  public UsbSerialOutputModel(String timeInString, String serialOutputString) {
    this.timeInString = timeInString;
    this.serialOutputString = serialOutputString;
  }

  @Override
  public String getTimeInString() {
    return timeInString;
  }

  @Override
  public String getSerialOutputInString() {
    return serialOutputString;
  }

}
