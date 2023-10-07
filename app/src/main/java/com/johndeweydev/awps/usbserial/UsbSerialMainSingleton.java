package com.johndeweydev.awps.usbserial;

import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbSerialMainSingleton {

  private final UsbSerialMain usbSerialMain;
  private static UsbSerialMainSingleton instance;
  private static UsbManager usbManager;

  public UsbSerialMainSingleton() {
    usbSerialMain = new UsbSerialMain();
    Log.d("dev-log", "UsbSerialMainSingleton: Created new instance of UsbSerialMain2");
  }

  public static synchronized UsbSerialMainSingleton getInstance() {
    if (instance == null) {
      instance = new UsbSerialMainSingleton();
    }
    return instance;
  }

  public UsbSerialMain getUsbSerialMain() {
    return usbSerialMain;
  }

  public static void setUsbManager(UsbManager aUsbManager) {
    usbManager = aUsbManager;
  }

  public static UsbManager getUsbManager() {
    return usbManager;
  }
}
