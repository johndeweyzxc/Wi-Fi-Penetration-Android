package com.johndeweydev.himawhs.repository;

import android.hardware.usb.UsbManager;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.johndeweydev.himawhs.models.DeviceModel;
import com.johndeweydev.himawhs.models.SerialOutputModel;
import com.johndeweydev.himawhs.serial.UsbSerialCommunication;

import java.util.ArrayList;

public class DefaultMainRepository {
  public ArrayList<DeviceModel> getAvailableDevicesFromSerial(FragmentActivity fragmentActivity) {
    return UsbSerialCommunication.getConnectedDevices(fragmentActivity);
  }

  public boolean connect(
          UsbSerialDriver driver, UsbManager usbManager, int portNum, int baudRate
  ) {
    return UsbSerialCommunication.connect(driver, usbManager, portNum, baudRate);
  }

  public void disconnect() {
    UsbSerialCommunication.disconnect();
  }

  public void startEventRead() {
    UsbSerialCommunication.startEventRead();
  }

  public void stopEventRead() {
    UsbSerialCommunication.stopEventRead();
  }

  public ArrayList<SerialOutputModel> readDataInSingleton() {
    return UsbSerialCommunication.readDataInSingleton();
  }

  public void sendData(String data) {
    if (UsbSerialCommunication.getUsbSerialPort().isOpen()) {
      UsbSerialCommunication.sendData(data);
    }
  }
}
