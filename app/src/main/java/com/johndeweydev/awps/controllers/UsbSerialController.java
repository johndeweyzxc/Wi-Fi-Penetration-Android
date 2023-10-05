package com.johndeweydev.awps.controllers;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.johndeweydev.awps.usbserial.UsbSerialMain;

public class UsbSerialController {

  private static boolean isConnected = false;
  private static boolean eventReadStarted = false;
  private static boolean driverAlreadySet = false;

  public static void requestUsbDevicePermission(
          FragmentActivity fragmentActivity, String intentAction
  ) {
    UsbManager usbManager = UsbSerialMain.getUsbManager();

    int flags = PendingIntent.FLAG_MUTABLE;
    PendingIntent usbPermissionIntent;
    usbPermissionIntent = PendingIntent.getBroadcast(
            fragmentActivity, 0, new Intent(intentAction), flags
    );
    usbManager.requestPermission(
            UsbSerialMain.getUsbSerialDriver().getDevice(), usbPermissionIntent);
  }

  public static boolean usbDevicePermissionGranted() {
    UsbManager usbManager = UsbSerialMain.getUsbManager();
    UsbDevice usbDevice = UsbSerialMain.getUsbSerialDriver().getDevice();

    return usbManager.hasPermission(usbDevice);
  }

  public static void connect(int portNum, int baudRate) {
    if (!isConnected) {
      isConnected = UsbSerialMain.connect(portNum, baudRate);
      Log.d("dev-log", "UsbSerialController.connect: Connected to device");
    }
  }

  public static void disconnect() {
    if (isConnected) {
      UsbSerialMain.disconnect();
      isConnected = false;
      Log.d("dev-log", "UsbSerialController.disconnect: Disconnected from device");
    }
  }

  public static void setDriverOfDevice(int deviceId, int portNum) {
    if (!driverAlreadySet) {
      boolean success = UsbSerialMain.setDriverOfDevice(deviceId, portNum);
      if (success) {
        driverAlreadySet = true;
        Log.d("dev-log", "UsbSerialController.setDriverOfDevice: Driver set");
      }
    }
  }

  public static void startEventRead() {
    if (!eventReadStarted) {
      UsbSerialMain.startEventRead();
      eventReadStarted = true;
      Log.d("dev-log", "UsbSerialController.startEventRead: Event driven read started");
    }
  }

  public static void stopEventRead() {
    if (eventReadStarted) {
      UsbSerialMain.stopEventRead();
      eventReadStarted = false;
      Log.d("dev-log", "UsbSerialController.stopEventRead: Event driven read stopped");
    }
  }
}
