package com.johndeweydev.awps.usbserial;

public class UsbSerialOutputItem {
  private final String timeInString;
  private final String serialOutputString;


  public UsbSerialOutputItem(String timeInString, String serialOutputString) {
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
