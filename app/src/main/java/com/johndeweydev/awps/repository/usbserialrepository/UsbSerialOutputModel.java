package com.johndeweydev.awps.repository.usbserialrepository;

public class UsbSerialOutputModel {
  private final String timeInString;
  private final String serialOutputString;


  public UsbSerialOutputModel(String timeInString, String serialOutputString) {
    this.timeInString = timeInString;
    this.serialOutputString = serialOutputString;
  }

  public String getTimeInString() {
    return timeInString;
  }

  public String getSerialOutputInString() {
    return serialOutputString;
  }

}
