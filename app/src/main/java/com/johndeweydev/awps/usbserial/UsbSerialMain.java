package com.johndeweydev.awps.usbserial;

import static com.johndeweydev.awps.usbserial.UsbSerialStatus.ALREADY_CONNECTED;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.DEVICE_NOT_FOUND;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.DRIVER_NOT_FOUND;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.DRIVER_SET;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.FAILED_OPENING_DEVICE;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.FAILED_TO_CONNECT;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.HAS_USB_PERMISSION;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.NO_USB_PERMISSION;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.PORT_NOT_FOUND;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.SUCCESSFULLY_CONNECTED;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.UNSUPPORTED_PORT_PARAMETERS;

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
import com.johndeweydev.awps.repository.usbserialrepository.models.UsbDeviceModel;
import com.johndeweydev.awps.repository.UsbSerialDataEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UsbSerialMain {

  private static final int WRITE_WAIT_MILLIS = 2000;
  private UsbDevice usbDevice;
  private UsbSerialDriver usbSerialDriver;
  private UsbSerialPort usbSerialPort;
  private SerialInputOutputManager serialInputOutputManager;
  private UsbSerialDataEvent usbSerialDataEvent;

  private static class UsbSerialControlData {
    private static boolean isConnected = false;
    private static boolean isReading = false;
  }

  public void setLauncherSerialDataEvent(
          UsbSerialDataEvent usbSerialDataEvent
  ) {
    this.usbSerialDataEvent = usbSerialDataEvent;
  }

  public UsbSerialDriver getUsbSerialDriver() {
    return usbSerialDriver;
  }

  public UsbSerialStatus connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    if (UsbSerialControlData.isConnected) {
      return ALREADY_CONNECTED;
    }

    UsbSerialStatus driverStatus = setDriverAndDevice(deviceId, portNum);
    UsbSerialStatus permissionStatus;
    UsbSerialStatus connectionStatus;

    if (driverStatus.equals(DRIVER_SET)) {
      permissionStatus = hasUsbDevicePermissionGranted();
    } else {
      return FAILED_TO_CONNECT;
    }

    if (permissionStatus.equals(HAS_USB_PERMISSION)) {
      connectionStatus = connectToDevice(baudRate, dataBits, stopBits, parity, portNum);
      if (connectionStatus.equals(SUCCESSFULLY_CONNECTED)) {
        UsbSerialControlData.isConnected = true;
        return SUCCESSFULLY_CONNECTED;
      }
    } else if (permissionStatus.equals(NO_USB_PERMISSION)) {
      return NO_USB_PERMISSION;
    }

    return ALREADY_CONNECTED;
  }

  private UsbSerialStatus connectToDevice(
          int baudRate, int dataBits, int stopBits, int parity, int portNum
  ) {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
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

  private UsbSerialStatus hasUsbDevicePermissionGranted() {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
    if (usbManager.hasPermission(usbDevice)) {
      return HAS_USB_PERMISSION;
    }
    return NO_USB_PERMISSION;
  }

  private UsbSerialStatus setDriverAndDevice(int deviceId, int portNum) {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
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
      if (UsbSerialControlData.isConnected) {
        usbSerialPort.close();
      }
    } catch (IOException ignored) {}
    usbSerialPort = null;
    UsbSerialControlData.isConnected = false;
    Log.d("dev-log", "UsbSerialMain.disconnect: Disconnected from the device");
  }

  SerialInputOutputManager.Listener newDataListener = new SerialInputOutputManager.Listener() {
    @Override
    public void onNewData(byte[] data) {
      if (data.length > 0) {
        String strData = new String(data, StandardCharsets.US_ASCII);
        usbSerialDataEvent.onUsbSerialOutput(strData);
      }
    }

    @Override
    public void onRunError(Exception e) {
      Log.e("dev-log", "UsbIoManagerListener.onRunError: An error has occurred "
              + e.getMessage());
      usbSerialDataEvent.onUsbOutputError(e.getMessage());
    }
  };

  public void startReading() {
    if (!UsbSerialControlData.isReading) {
      serialInputOutputManager = new SerialInputOutputManager(usbSerialPort, newDataListener);
      serialInputOutputManager.start();
      UsbSerialControlData.isReading = true;
      Log.d("dev-log", "UsbSerialMain.startReading: Started event driven read");
    }
  }

  public void stopReading() {
    if (UsbSerialControlData.isReading) {
      serialInputOutputManager.setListener(null);
      serialInputOutputManager.stop();
      serialInputOutputManager = null;
      UsbSerialControlData.isReading = false;
      Log.d("dev-log", "UsbSerialMain.stopReading: Stopped event driven read");
    }
  }

  public ArrayList<UsbDeviceModel> discoverDevices() {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
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
      usbSerialDataEvent.onUsbInputError(str);
    }

  }
}
