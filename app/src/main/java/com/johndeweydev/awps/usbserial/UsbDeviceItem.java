package com.johndeweydev.awps.usbserial;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.util.Locale;

public class UsbDeviceItem {

  public UsbDevice device;
  public int devicePort;
  public UsbSerialDriver usbSerialDriver;

  public UsbDeviceItem(UsbDevice usbDevice, int devicePort, UsbSerialDriver usbSerialDriver) {
    this.device = usbDevice;
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
    return String.format(Locale.US, "%04X", device.getProductId());
  }

  public String getDeviceVendorId() {
    return String.format(Locale.US, "%04X", device.getVendorId());
  }
}
