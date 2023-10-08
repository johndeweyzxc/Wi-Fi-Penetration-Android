package com.johndeweydev.awps.usbserial;

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
import com.johndeweydev.awps.repository.UsbSerialRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UsbSerialMain {

  private static final int WRITE_WAIT_MILLIS = 2000;
  private UsbDevice usbDevice;
  private UsbSerialDriver usbSerialDriver;
  private UsbSerialPort usbSerialPort;
  private SerialInputOutputManager serialInputOutputManager;
  private UsbSerialRepository.UsbSerialRepositoryCallback usbSerialRepositoryCallback;

  public enum ReturnStatus {
    ALREADY_CONNECTED,
    DEVICE_NOT_FOUND,
    DRIVER_NOT_FOUND,
    PORT_NOT_FOUND,
    DRIVER_SET,
    HAS_USB_PERMISSION,
    NO_USB_PERMISSION,
    SUCCESSFULLY_CONNECTED,
    FAILED_OPENING_DEVICE,
    UNSUPPORTED_PORT_PARAMETERS
  }

  private static class UsbSerialControlData {
    private static boolean isConnected = false;
    private static boolean isReading = false;
  }

  public void setUsbSerialRepositoryCallback(
          UsbSerialRepository.UsbSerialRepositoryCallback usbSerialRepositoryCallback
  ) {
    this.usbSerialRepositoryCallback = usbSerialRepositoryCallback;
  }

  public UsbSerialDriver getUsbSerialDriver() {
    return usbSerialDriver;
  }

  public ReturnStatus connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    if (!UsbSerialControlData.isConnected) {
      ReturnStatus driverStatus = setDriverAndDevice(deviceId, portNum);

      if (driverStatus.equals(ReturnStatus.DRIVER_SET)) {
        ReturnStatus permissionStatus = hasUsbDevicePermissionGranted();

        if (permissionStatus.equals(ReturnStatus.HAS_USB_PERMISSION)) {
          ReturnStatus connectionStatus;
          connectionStatus = connectToDevice(baudRate, dataBits, stopBits, parity, portNum);

          if (connectionStatus.equals(ReturnStatus.SUCCESSFULLY_CONNECTED)) {
            UsbSerialControlData.isConnected = true;
            Log.d("dev-log", "UsbSerialMain.connect: Connected to the device");
            return ReturnStatus.SUCCESSFULLY_CONNECTED;
          } else {
            return connectionStatus;
          }
        } else if (permissionStatus.equals(ReturnStatus.NO_USB_PERMISSION)) {
          return ReturnStatus.NO_USB_PERMISSION;
        }
      } else {
        return driverStatus;
      }
    }

    Log.d("dev-log", "UsbSerialMain.connect: Connected to the device");
    return ReturnStatus.ALREADY_CONNECTED;
  }

  private ReturnStatus connectToDevice(
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
        return ReturnStatus.SUCCESSFULLY_CONNECTED;
      } catch (UnsupportedOperationException e) {
        Log.w("dev-log", "UsbSerialMain.connectToDevice: Unsupported port parameters");
        return ReturnStatus.UNSUPPORTED_PORT_PARAMETERS;
      }
    } catch (Exception e) {
      Log.w("dev-log", "UsbSerialMain.connectToDevice: Connection failed, "
              + e.getMessage());
      disconnect();
      return ReturnStatus.FAILED_OPENING_DEVICE;
    }
  }

  private ReturnStatus hasUsbDevicePermissionGranted() {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
    if (usbManager.hasPermission(usbDevice)) {
      return ReturnStatus.HAS_USB_PERMISSION;
    }
    return ReturnStatus.NO_USB_PERMISSION;
  }

  private ReturnStatus setDriverAndDevice(int deviceId, int portNum) {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
    for (UsbDevice device: usbManager.getDeviceList().values()) {
      if (device.getDeviceId() == deviceId) {
        usbDevice = device;
      }
    }

    if(usbDevice == null) {
      Log.w("dev-log", "UsbSerialMain.setDriverOfDevice: Device not found");
      return ReturnStatus.DEVICE_NOT_FOUND;
    }

    usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
    if(usbSerialDriver == null) {
      usbSerialDriver = getCustomProber().probeDevice(usbDevice);
    }

    if(usbSerialDriver == null) {
      Log.w("dev-log", "UsbSerialMain.setDriverOfDevice: Driver not found for device");
      return ReturnStatus.DRIVER_NOT_FOUND;
    }

    if(usbSerialDriver.getPorts().size() < portNum) {
      Log.w("dev-log", "UsbSerialMain.setDriverOfDevice: Port not found for driver");
      return ReturnStatus.PORT_NOT_FOUND;
    }
    return ReturnStatus.DRIVER_SET;
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
        usbSerialRepositoryCallback.onNewData(strData);
      }
    }

    @Override
    public void onRunError(Exception e) {
      Log.e("dev-log", "UsbIoManagerListener.onRunError: An error has occurred "
              + e.getMessage());
      usbSerialRepositoryCallback.onErrorNewData(e.getMessage());
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

  public ArrayList<UsbDeviceItem> discoverDevices() {
    UsbManager usbManager = UsbSerialMainSingleton.getUsbManager();
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = getCustomProber();

    ArrayList<UsbDeviceItem> usbDeviceItemList = new ArrayList<>();

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
          usbDeviceItemList.add(new UsbDeviceItem(device, port, driver));
        }
      }
    }

    return usbDeviceItemList;
  }

  public void writeData(String str) {
    byte[] data = str.getBytes();
    try {
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
    } catch (Exception e) {
      Log.e("dev-log", "UsbSerialMain.writeData: An error has occurred "
              + e.getMessage());
      usbSerialRepositoryCallback.onErrorWriting(str);
    }

  }
}
