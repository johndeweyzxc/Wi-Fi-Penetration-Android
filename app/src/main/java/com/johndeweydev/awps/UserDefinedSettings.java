package com.johndeweydev.awps;

public class UserDefinedSettings {
  private static UserDefinedSettings instance;

  public String REST_API_URL = "http://192.168.1.9:8000";
  public int NUMBER_OF_PREVIOUSLY_ATTACKED_TARGETS = 5;
  public int ALLOCATED_TIME_FOR_EACH_ATTACK = 5;

  private UserDefinedSettings() { }

  public static synchronized UserDefinedSettings getInstance() {
    if (instance == null) {
      instance = new UserDefinedSettings();
    }
    return instance;
  }
}
