package com.johndeweydev.awps.viewmodels.usbserialviewmodel;

import com.johndeweydev.awps.repository.usbserialrepository.UsbSerialOutputModel;

public interface LauncherSerialDataVmEvent {
  void onNewDataRaw(UsbSerialOutputModel usbSerialOutputModel);
  void onNewDataFormatted(UsbSerialOutputModel usbSerialOutputModel);
  void onErrorNewData(String errorMessageOnNewData);
  void onErrorWriting(String dataToWrite);
}
