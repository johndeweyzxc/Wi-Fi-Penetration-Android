package com.johndeweydev.awps.viewmodels.usbserialviewmodel;

import com.johndeweydev.awps.repository.usbserialrepository.UsbSerialOutputModel;

public interface UsbSerialEvent {
  void onSerialOutputRaw(UsbSerialOutputModel usbSerialOutputModel);
  void onSerialOutputFormatted(UsbSerialOutputModel usbSerialOutputModel);
  void onSerialOutputError(String errorMessageOnNewData);
  void onSerialInputError(String dataToWrite);
}
