package com.johndeweydev.himawhs.usbserial;

public class UsbSerialOutputItem {
  private final String timeInString;
  private final String serialOutputString;


  public UsbSerialOutputItem(String aTimeInString, String aSerialOutputString) {
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
