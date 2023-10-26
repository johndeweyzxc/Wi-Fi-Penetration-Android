package com.johndeweydev.awps.repository;

public class UsbSerialOutputModel {

  private final String time;
  private final String output;


  public UsbSerialOutputModel(String time, String output) {
    this.time = time;
    this.output = output;
  }

  public String getTime() {
    return time;
  }

  public String getOutput() {
    return output;
  }
}
