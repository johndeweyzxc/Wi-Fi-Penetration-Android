package com.johndeweydev.himawhs.serial;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.johndeweydev.himawhs.CustomProber;
import com.johndeweydev.himawhs.models.DeviceModel;
import com.johndeweydev.himawhs.models.SerialOutputModel;

import java.io.IOException;
import java.util.ArrayList;

public class UsbSerialCommunication {

  private static UsbSerialPort usbSerialPort;
  private static SerialInputOutputManager usbIoManager;

  private static final int WRITE_WAIT_MILLIS = 2000;

  public static UsbSerialPort getUsbSerialPort() {
    return usbSerialPort;
  }

  public static ArrayList<DeviceModel> getConnectedDevices(FragmentActivity fragmentActivity) {
    UsbManager usbManager = (UsbManager) fragmentActivity.getSystemService(Context.USB_SERVICE);
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
    ArrayList<DeviceModel> deviceList = new ArrayList<>();

    for (UsbDevice device : usbManager.getDeviceList().values()) {
      UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
      if (driver == null) {
        driver = usbCustomProber.probeDevice(device);
      }
      if (driver != null) {
        for (int port = 0; port < driver.getPorts().size(); port++) {
          deviceList.add(new DeviceModel(device, port, driver));
          Log.d("dev-log", String.format(
                  "scanAvailableDevices: Found new device: %s", device.getDeviceName()
          ));
        }
      } else {
        deviceList.add(new DeviceModel(device, 0, null));
      }
    }

    return deviceList;
  }

  /*
  *  Returns true if successfully connected otherwise returns false
  * */
  public static boolean connect(
          UsbSerialDriver driver, UsbManager usbManager, int portNum, int baudRate
  ) {
    usbSerialPort = driver.getPorts().get(portNum);
    UsbDeviceConnection usbDeviceConnection = usbManager.openDevice(driver.getDevice());
    try {
      usbSerialPort.open(usbDeviceConnection);
      try {
        usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
      } catch (UnsupportedOperationException e) {
        Log.w("dev-log", "connect: Unsupported port parameters");
        return false;
      }
      Log.d("dev-log", "connect: Successfully connected to serial device");
      return true;
    } catch (Exception e) {
      Log.w("dev-log", "connect: Connection failed, " + e.getMessage());
      disconnect();
      return false;
    }
  }

  public static void disconnect() {
    try {
      usbSerialPort.close();
    } catch (IOException ignored) {}
    usbSerialPort = null;
    Log.d("dev-log", "disconnect: Disconnected from serial device");
  }

  public static void startEventRead() {
    UsbIoManagerListener usbIoManagerListener = new UsbIoManagerListener();
    usbIoManager = new SerialInputOutputManager(usbSerialPort, usbIoManagerListener);
    usbIoManager.start();
    Log.d("dev-log", "startEventRead: Event read on serial port started");
  }

  public static void stopEventRead() {
    usbIoManager.setListener(null);
    usbIoManager.stop();
    usbIoManager = null;
    Log.d("dev-log", "stopEventRead: Event read on serial port stopped");
  }

  public static ArrayList<SerialOutputModel> readDataInSingleton() {
    return SerialDataSingleton.getInstance().getAllData();
  }

  public static void sendData(String str) {
    byte[] data = str.getBytes();
    try {
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
    } catch (IOException e) {
      usbIoManager.getListener().onRunError(e);
    }
  }
}
