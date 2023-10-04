package com.johndeweydev.himawhs.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;

public class UsbSerialMain {

  private static UsbManager usbManager;
  private static UsbDevice usbDevice = null;
  private static UsbSerialDriver usbSerialDriver;
  private static UsbSerialPort usbSerialPort;
  private static SerialInputOutputManager serialInputOutputManager;

  public static UsbManager getUsbManager() {
    return usbManager;
  }

  public static UsbSerialDriver getUsbSerialDriver() {
    return usbSerialDriver;
  }

  public static ArrayList<UsbDeviceItem> discoverDevices(
          FragmentActivity fragmentActivity, String serviceName
  ) {
    usbManager = (UsbManager) fragmentActivity.getSystemService(serviceName);
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = UsbSerialCustomProber.getCustomProber();

    ArrayList<UsbDeviceItem> usbDeviceItemList = new ArrayList<>();

    for(UsbDevice device : usbManager.getDeviceList().values()) {
      UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
      if (driver == null) {
        Log.d("dev-log", "UsbSerialMain.discoverDevices: Driver not found " +
                "for device, using custom prober"
        );
        driver = usbCustomProber.probeDevice(device);
      }
      
      if (driver != null) {
        int totalPorts = driver.getPorts().size();
        for (int port = 0; port < totalPorts; port++) {
          usbDeviceItemList.add(new UsbDeviceItem(device, port, driver));
        }
      }
    }

    return usbDeviceItemList;
  }

  /*
  * Sets the usb serial driver, returns true if it successfully sets otherwise false
  * */
  public static boolean setDriverOfDevice(int deviceId, int portNum) {
    for (UsbDevice device: usbManager.getDeviceList().values()) {
      if (device.getDeviceId() == deviceId) {
        usbDevice = device;
      }
    }

    if(usbDevice == null) {
      Log.d("dev-log", "UsbSerialMain.setDriverOfDevice: Device not found");
      return false;
    }

    usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
    if(usbSerialDriver == null) {
      usbSerialDriver = UsbSerialCustomProber.getCustomProber().probeDevice(usbDevice);
    }

    if(usbSerialDriver == null) {
      Log.d("dev-log", "UsbSerialMain.setDriverOfDevice: Driver not found for device");
      return false;
    }

    if(usbSerialDriver.getPorts().size() < portNum) {
      Log.d("dev-log", "UsbSerialMain.setDriverOfDevice: Port not found for driver");
      return false;
    }
    return true;
  }

  public static boolean connect(int portNum, int baudRate) {
    usbSerialPort = usbSerialDriver.getPorts().get(portNum);
    UsbDeviceConnection usbDeviceConnection = usbManager.openDevice(usbSerialDriver.getDevice());

    try {
      usbSerialPort.open(usbDeviceConnection);
      try {
        usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
      } catch (UnsupportedOperationException e) {
        Log.w("dev-log", "UsbSerialMain.connect: Unsupported port parameters");
        return false;
      }
    } catch (Exception e) {
      Log.w("dev-log", "UsbSerialMain.connect: Connection failed, " + e.getMessage());
      disconnect();
      return false;
    }

    return true;
  }

  public static void disconnect() {
    try {
      usbSerialPort.close();
    } catch (IOException ignored) {}
    usbSerialPort = null;
  }

  public static void startEventRead() {
    UsbIoManagerListener usbIoManagerListener = new UsbIoManagerListener();
    serialInputOutputManager = new SerialInputOutputManager(usbSerialPort, usbIoManagerListener);
    serialInputOutputManager.start();
  }

  public static void stopEventRead() {
    serialInputOutputManager.setListener(null);
    serialInputOutputManager.stop();
    serialInputOutputManager = null;
  }

  public static void sendData(String str) {
    byte[] data = str.getBytes();
    try {
      int WRITE_WAIT_MILLIS = 2000;
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
    } catch (IOException e) {
      serialInputOutputManager.getListener().onRunError(e);
    }
  }

  public static ArrayList<UsbSerialOutputItem> readData() {
    return UsbSerialDataSingleton.getInstance().getAllData();
  }
}
