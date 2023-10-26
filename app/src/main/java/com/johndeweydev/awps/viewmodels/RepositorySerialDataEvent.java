package com.johndeweydev.awps.viewmodels;

import com.johndeweydev.awps.repository.UsbSerialOutputModel;

public interface RepositorySerialDataEvent {

  void onRepositoryOutputRaw(UsbSerialOutputModel usbSerialOutputModel);
  void onRepositoryOutputFormatted(UsbSerialOutputModel usbSerialOutputModel);
  void onRepositoryOutputError(String error);
  void onRepositoryInputError(String input);
}
