package com.johndeweydev.awps.repository;

public interface UsbSerialRepositoryCallback {
  void onNewData(String data);
  void onErrorNewData(String errorMessageOnNewData);
  void onErrorWriting(String dataToWrite);
}
