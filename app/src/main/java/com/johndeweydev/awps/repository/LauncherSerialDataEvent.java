package com.johndeweydev.awps.repository;

public interface LauncherSerialDataEvent {
  void onSerialOutput(String data);
  void onSerialOutputError(String errorMessageOnNewData);
  void onSerialInputError(String dataToWrite);
}
