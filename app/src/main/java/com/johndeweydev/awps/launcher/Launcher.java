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
import com.johndeweydev.awps.models.UsbDeviceModel;

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

  public void setLauncherSerialDataEvent(
          LauncherEvent launcherEvent
  ) {
    this.launcherEvent = launcherEvent;
  }

  public UsbSerialDriver getUsbSerialDriver() {
    return usbSerialDriver;
  }

  public LauncherStages connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    if (isConnectedToTheDevice) {
      return ALREADY_CONNECTED;
    }

    LauncherStages driverAndDeviceStageStatus = setDriverAndDevice(deviceId, portNum);
    boolean permissionStatus;
    if (driverAndDeviceStageStatus.equals(DRIVER_SET)) {
      permissionStatus = hasUsbDevicePermissionGranted();
    } else {
      return driverAndDeviceStageStatus;
    }
    if(!permissionStatus) {
      return NO_USB_PERMISSION;
    }
    LauncherStages connectionStageStatus = connectToDevice(baudRate, dataBits, stopBits,
            parity, portNum);
    if (connectionStageStatus.equals(SUCCESSFULLY_CONNECTED)) {
      isConnectedToTheDevice = true;
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
        Log.w("dev-log", "UsbSerialMain.connectToDevice: Unsupported port parameters");
        return UNSUPPORTED_PORT_PARAMETERS;
      }
    } catch (Exception e) {
      Log.w("dev-log", "UsbSerialMain.connectToDevice: Connection failed, "
              + e.getMessage());
      disconnect();
      return FAILED_OPENING_DEVICE;
    }
  }

  private boolean hasUsbDevicePermissionGranted() {
    UsbManager usbManager = LauncherSingleton.getUsbManager();
    return usbManager.hasPermission(usbDevice);
  }

  private LauncherStages setDriverAndDevice(int deviceId, int portNum) {
    UsbManager usbManager = LauncherSingleton.getUsbManager();
    for (UsbDevice device: usbManager.getDeviceList().values()) {
      if (device.getDeviceId() == deviceId) {
        usbDevice = device;
      }
    }

    if(usbDevice == null) {
      Log.w("dev-log", "UsbSerialMain.setDriverOfDevice: Device not found");
      return DEVICE_NOT_FOUND;
    }

    usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
    if(usbSerialDriver == null) {
      usbSerialDriver = getCustomProber().probeDevice(usbDevice);
    }

    if(usbSerialDriver == null) {
      Log.w("dev-log", "UsbSerialMain.setDriverOfDevice: Driver not found for device");
      return DRIVER_NOT_FOUND;
    }

    if(usbSerialDriver.getPorts().size() < portNum) {
      Log.w("dev-log", "UsbSerialMain.setDriverOfDevice: Port not found for driver");
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
    Log.d("dev-log", "UsbSerialMain.disconnect: Disconnected from the device");
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
      Log.e("dev-log", "UsbIoManagerListener.onRunError: An error has occurred "
              + e.getMessage());
      launcherEvent.onLauncherOutputError(e.getMessage());
    }
  };

  public void startReading() {
    if (!eventDrivenReadIsTurnedOn) {
      serialInputOutputManager = new SerialInputOutputManager(usbSerialPort, newDataListener);
      serialInputOutputManager.start();
      eventDrivenReadIsTurnedOn = true;
      Log.d("dev-log", "UsbSerialMain.startReading: Started event driven read");
    }
  }

  public void stopReading() {
    if (eventDrivenReadIsTurnedOn) {
      serialInputOutputManager.setListener(null);
      serialInputOutputManager.stop();
      serialInputOutputManager = null;
      eventDrivenReadIsTurnedOn = false;
      Log.d("dev-log", "UsbSerialMain.stopReading: Stopped event driven read");
    }
  }

  public ArrayList<UsbDeviceModel> discoverDevices() {
    UsbManager usbManager = LauncherSingleton.getUsbManager();
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = getCustomProber();

    ArrayList<UsbDeviceModel> usbDeviceModelList = new ArrayList<>();

    for(UsbDevice device : usbManager.getDeviceList().values()) {
      UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
      if (driver == null) {
        Log.w("dev-log", "UsbSerialMain.discoverDevices: Driver not found " +
                "for device, using custom prober"
        );
        driver = usbCustomProber.probeDevice(device);
      }

      if (driver != null) {
        int totalPorts = driver.getPorts().size();
        for (int port = 0; port < totalPorts; port++) {
          usbDeviceModelList.add(new UsbDeviceModel(device, port, driver));
        }
      }
    }

    return usbDeviceModelList;
  }

  public void writeData(String str) {
    byte[] data = str.getBytes();
    try {
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
    } catch (Exception e) {
      Log.e("dev-log", "UsbSerialMain.writeData: An error has occurred "
              + e.getMessage());
      launcherEvent.onLauncherInputError(str);
    }

  }
}
