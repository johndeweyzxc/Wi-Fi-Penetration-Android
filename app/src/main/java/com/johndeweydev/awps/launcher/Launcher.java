package com.johndeweydev.awps.launcher;

import static com.johndeweydev.awps.launcher.LauncherStages.ALREADY_CONNECTED;
import static com.johndeweydev.awps.launcher.LauncherStages.DEVICE_NOT_FOUND;
import static com.johndeweydev.awps.launcher.LauncherStages.DRIVER_NOT_FOUND;
import static com.johndeweydev.awps.launcher.LauncherStages.DRIVER_SET;
import static com.johndeweydev.awps.launcher.LauncherStages.FAILED_OPENING_DEVICE;
import static com.johndeweydev.awps.launcher.LauncherStages.NO_USB_PERMISSION;
import static com.johndeweydev.awps.launcher.LauncherStages.PORT_NOT_FOUND;
import static com.johndeweydev.awps.launcher.LauncherStages.SUCCESSFULLY_CONNECTED;
import static com.johndeweydev.awps.launcher.LauncherStages.UNSUPPORTED_PORT_PARAMETERS;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.johndeweydev.awps.data.UsbDeviceData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Launcher {

  private static final int WRITE_WAIT_MILLIS = 2000;
  private static boolean isConnectedToTheDevice = false;
  private static boolean eventDrivenReadIsTurnedOn = false;
  private UsbDevice usbDevice;
  private UsbSerialDriver usbSerialDriver;
  private UsbSerialPort usbSerialPort;
  private SerialInputOutputManager serialInputOutputManager;
  private LauncherEvent launcherEvent;

  public Launcher() {
    Log.d("dev-log", "Launcher: Created new instance of Launcher");
  }

  public void setLauncherSerialDataEvent(
          LauncherEvent launcherEvent
  ) {
    this.launcherEvent = launcherEvent;
  }

  public UsbSerialDriver getUsbSerialDriver() {
    return usbSerialDriver;
  }

  public LauncherStages initiateConnectionToDevice(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    if (isConnectedToTheDevice) {
      Log.d("dev-log", "Launcher.initiateConnectionToDevice: " +
              "Already connected to the device");
      return ALREADY_CONNECTED;
    }

    LauncherStages driverAndDeviceStageStatus = initializeDriverAndDevice(deviceId, portNum);
    boolean permissionStatus;
    if (driverAndDeviceStageStatus.equals(DRIVER_SET)) {
      UsbManager usbManager = LauncherSingleton.getUsbManager();
      permissionStatus = usbManager.hasPermission(usbDevice);
    } else {
      return driverAndDeviceStageStatus;
    }
    if(!permissionStatus) {
      Log.d("dev-log", "Launcher.initiateConnectionToDevice: Caller does not have " +
              "permission to access the device");
      return NO_USB_PERMISSION;
    }
    LauncherStages connectionStageStatus = connectToDevice(baudRate, dataBits, stopBits,
            parity, portNum);
    if (connectionStageStatus.equals(SUCCESSFULLY_CONNECTED)) {
      isConnectedToTheDevice = true;
      Log.d("dev-log", "Launcher.initiateConnectionToDevice: " +
              "Connected to the device");
      return SUCCESSFULLY_CONNECTED;
    } else {
      return connectionStageStatus;
    }
  }

  private LauncherStages connectToDevice(
          int baudRate, int dataBits, int stopBits, int parity, int portNum
  ) {
    UsbManager usbManager = LauncherSingleton.getUsbManager();
    usbSerialPort = usbSerialDriver.getPorts().get(portNum);
    UsbDeviceConnection usbDeviceConnection = usbManager.openDevice(
            usbSerialDriver.getDevice());

    try {
      usbSerialPort.open(usbDeviceConnection);
      try {
        usbSerialPort.setParameters(baudRate, dataBits, stopBits, parity);
        return SUCCESSFULLY_CONNECTED;
      } catch (UnsupportedOperationException e) {
        Log.w("dev-log", "Launcher.connectToDevice: Unsupported port parameters");
        return UNSUPPORTED_PORT_PARAMETERS;
      }
    } catch (Exception e) {
      Log.w("dev-log", "Launcher.connectToDevice: Connection failed, "
              + e.getMessage());
      disconnect();
      return FAILED_OPENING_DEVICE;
    }
  }

  private LauncherStages initializeDriverAndDevice(int deviceId, int portNum) {
    UsbManager usbManager = LauncherSingleton.getUsbManager();
    for (UsbDevice device: usbManager.getDeviceList().values()) {
      if (device.getDeviceId() == deviceId) {
        usbDevice = device;
      }
    }

    if(usbDevice == null) {
      Log.w("dev-log", "Launcher.initializeDriverAndDevice: Device not found");
      return DEVICE_NOT_FOUND;
    }

    usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
    if(usbSerialDriver == null) {
      Log.d("dev-log", "Launcher.initializeDriverAndDevice: Driver not found " +
              "for device, using custom prober");
      usbSerialDriver = getCustomProber().probeDevice(usbDevice);
    }

    if(usbSerialDriver == null) {
      Log.w("dev-log", "Launcher.initializeDriverAndDevice: Driver not found for device");
      return DRIVER_NOT_FOUND;
    }

    if(usbSerialDriver.getPorts().size() < portNum) {
      Log.w("dev-log", "Launcher.initializeDriverAndDevice: Port not found for driver");
      return PORT_NOT_FOUND;
    }
    return DRIVER_SET;
  }

  private UsbSerialProber getCustomProber() {
    ProbeTable customTable = new ProbeTable();
    customTable.addProduct(0x1234, 0x0001, FtdiSerialDriver.class);
    // e.g. device with custom VID+PID
    customTable.addProduct(0x1234, 0x0002, FtdiSerialDriver.class);
    // e.g. device with custom VID+PID
    return new UsbSerialProber(customTable);
  }

  public void disconnect() {
    try {
      if (isConnectedToTheDevice) {
        usbSerialPort.close();
      }
    } catch (IOException ignored) {}
    usbSerialPort = null;
    isConnectedToTheDevice = false;
    Log.d("dev-log", "Launcher.disconnect: Disconnected from the device");
  }

  SerialInputOutputManager.Listener newDataListener = new SerialInputOutputManager.Listener() {
    @Override
    public void onNewData(byte[] data) {
      if (data.length > 0) {
        String strData = new String(data, StandardCharsets.US_ASCII);
        launcherEvent.onLauncherOutput(strData);
      }
    }

    @Override
    public void onRunError(Exception e) {
      Log.e("dev-log", "Launcher.onRunError: An error has occurred "
              + e.getMessage());
      launcherEvent.onLauncherOutputError(e.getMessage());
    }
  };

  public void startReading() {
    if (!eventDrivenReadIsTurnedOn) {
      serialInputOutputManager = new SerialInputOutputManager(usbSerialPort, newDataListener);
      serialInputOutputManager.start();
      eventDrivenReadIsTurnedOn = true;
      Log.d("dev-log", "Launcher.startReading: Started event driven read");
    }
  }

  public void stopReading() {
    if (eventDrivenReadIsTurnedOn) {
      serialInputOutputManager.setListener(null);
      serialInputOutputManager.stop();
      serialInputOutputManager = null;
      eventDrivenReadIsTurnedOn = false;
      Log.d("dev-log", "Launcher.stopReading: Stopped event driven read");
    }
  }

  public ArrayList<UsbDeviceData> discoverDevices() {
    UsbManager usbManager = LauncherSingleton.getUsbManager();
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = getCustomProber();

    ArrayList<UsbDeviceData> usbDeviceDataList = new ArrayList<>();

    for(UsbDevice device : usbManager.getDeviceList().values()) {
      UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
      if (driver == null) {
        Log.d("dev-log", "Launcher.discoverDevices: Driver not found " +
                "for device, using custom prober");
        driver = usbCustomProber.probeDevice(device);
      }

      if (driver != null) {
        int totalPorts = driver.getPorts().size();
        for (int port = 0; port < totalPorts; port++) {
          usbDeviceDataList.add(new UsbDeviceData(device, port, driver));
        }
      } else {
        Log.w("dev-log", "Launcher.discoverDevices: Used usb custom prober but driver " +
                "still not found");
      }
    }

    return usbDeviceDataList;
  }

  public void writeData(String str) {
    byte[] data = str.getBytes();
    try {
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
    } catch (Exception e) {
      Log.e("dev-log", "Launcher.writeData: An error has occurred "
              + e.getMessage());
      launcherEvent.onLauncherInputError(str);
    }

  }
}
