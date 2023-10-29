package com.johndeweydev.awps.data;

public class LauncherOutputData {

  private String time;
  private final String output;


  public LauncherOutputData(String time, String output) {
    this.time = time;
    this.output = output;
  }

  public String getTime() {
    return time;
  }

  public String getOutput() {
    return output;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
