package com.johndeweydev.awps.models.data;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.util.Locale;

public class UsbDeviceData {

  public UsbDevice usbDevice;
  public int devicePort;
  public UsbSerialDriver usbSerialDriver;

  public UsbDeviceData(UsbDevice usbDevice, int devicePort, UsbSerialDriver usbSerialDriver) {
    this.usbDevice = usbDevice;
    this.devicePort = devicePort;
    this.usbSerialDriver = usbSerialDriver;
  }

  public String getDeviceName() {
    if (usbSerialDriver == null) {
      return "<No driver>";
    } else {
      return usbSerialDriver
              .getClass()
              .getSimpleName()
              .replace("SerialDriver", "");
    }
  }

  public String getDeviceProductId() {
    return String.format(Locale.US, "%04X", usbDevice.getProductId());
  }

  public String getDeviceVendorId() {
    return String.format(Locale.US, "%04X", usbDevice.getVendorId());
  }
}
