package com.johndeweydev.awps.viewmodels;

import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;

public interface UsbSerialViewModelCallback {
  void onNewDataRaw(UsbSerialOutputItem usbSerialOutputItem);
  void onNewDataFormatted(UsbSerialOutputItem usbSerialOutputItem);
  void onErrorNewData(String errorMessageOnNewData);
  void onErrorWriting(String dataToWrite);
}
