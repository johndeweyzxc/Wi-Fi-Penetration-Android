package com.johndeweydev.himawhs.models;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.util.Locale;

public class DeviceModel {
  private final UsbDevice device;
  private final int port;
  private final UsbSerialDriver driver;

  public DeviceModel(UsbDevice aDevice, int aPort, UsbSerialDriver aDriver) {
    device = aDevice;
    port = aPort;
    driver = aDriver;
  }

  public String getDeviceName() {
    if (driver == null) {
      return "<No driver>";
    } else {
      return String.format(Locale.US, "Serial device: %s", driver.getClass()
              .getSimpleName().replace("SerialDriver", "").toUpperCase()
              + " - " + port);
    }
  }

  public String getDeviceProductId() {
    return String.format(Locale.US, "Product Id: %04X", device.getProductId());
  }

  public String getDeviceVendorId() {
    return String.format(Locale.US, "Vendor Id: %04X", device.getVendorId());
  }

  public int getDeviceId() {
    return device.getDeviceId();
  }

  public int getDevicePort() {
    return port;
  }
}
