package com.johndeweydev.awps.repository.usbserialrepository;

public interface LauncherSerialDataEvent {
  void onNewData(String data);
  void onErrorNewData(String errorMessageOnNewData);
  void onErrorWriting(String dataToWrite);
}
