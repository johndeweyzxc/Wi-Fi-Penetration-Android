package com.johndeweydev.awps.api.launcher;

import android.hardware.usb.UsbManager;

public class LauncherSingleton {

  private final Launcher launcher;
  private static LauncherSingleton instance;
  private static UsbManager usbManager;

  public LauncherSingleton() {
    launcher = new Launcher();
  }

  public static synchronized LauncherSingleton getInstance() {
    if (instance == null) {
      instance = new LauncherSingleton();
    }
    return instance;
  }

  public Launcher getLauncher() {
    return launcher;
  }

  public static void setUsbManager(UsbManager aUsbManager) {
    usbManager = aUsbManager;
  }

  public static UsbManager getUsbManager() {
    return usbManager;
  }
}
