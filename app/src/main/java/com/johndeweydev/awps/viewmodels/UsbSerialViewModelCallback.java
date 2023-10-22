package com.johndeweydev.awps.viewmodels;

import com.johndeweydev.awps.repository.UsbSerialOutputModel;

public interface UsbSerialViewModelCallback {
  void onNewDataRaw(UsbSerialOutputModel usbSerialOutputModel);
  void onNewDataFormatted(UsbSerialOutputModel usbSerialOutputModel);
  void onErrorNewData(String errorMessageOnNewData);
  void onErrorWriting(String dataToWrite);
}
