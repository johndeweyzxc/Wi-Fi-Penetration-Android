package com.johndeweydev.awps.repository;

public interface UsbSerialDataEvent {
  void onUsbSerialOutput(String data);
  void onUsbOutputError(String error);
  void onUsbInputError(String input);
}
