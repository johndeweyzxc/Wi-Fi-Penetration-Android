package com.johndeweydev.awps.models;

public class LauncherOutputModel {

  private final String time;
  private final String output;


  public LauncherOutputModel(String time, String output) {
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
