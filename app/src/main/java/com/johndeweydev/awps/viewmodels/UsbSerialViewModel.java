package com.johndeweydev.awps.viewmodels;

import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.controllers.UsbSerialController;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;
import com.johndeweydev.awps.repository.UsbSerialRepository;
import com.johndeweydev.awps.usbserial.UsbDeviceItem;

import java.util.ArrayList;

public class UsbSerialViewModel extends ViewModel {

  public UsbSerialRepository usbSerialRepository;
  public MutableLiveData<ArrayList<UsbDeviceItem>> devicesList = new MutableLiveData<>();
  public MutableLiveData<UsbSerialOutputItem> currentSerialMessage = new MutableLiveData<>();

  public UsbSerialViewModel(UsbSerialRepository aUsbSerialRepository) {
    usbSerialRepository = aUsbSerialRepository;
  }

  public int checkAvailableUsbDevices(FragmentActivity fragmentActivity, String serviceName) {
    ArrayList<UsbDeviceItem> devices = usbSerialRepository.discoverDevices(
            fragmentActivity, serviceName
    );
    devicesList.setValue(devices);
    return devices.size();
  }

  public static void requestUsbDeviceAccessPermission(
          FragmentActivity fragmentActivity, String intentAction
  ) {
    UsbSerialController.requestUsbDevicePermission(fragmentActivity, intentAction);
  }

  public static boolean hasUsbDevicePermission() {
    return UsbSerialController.usbDevicePermissionGranted();
  }

  public static void connectToDevice(int portNum, int baudRate) {
    UsbSerialController.connect(portNum, baudRate);
  }

  public static void disconnectFromDevice() {
    UsbSerialController.disconnect();
  }

  public static void setTheDriverOfDevice(int deviceId, int portNum) {
    UsbSerialController.setDriverOfDevice(deviceId, portNum);
  }

  public static void startEventDrivenReadFromDevice() {
    UsbSerialController.startEventRead();
  }

  public static void stopEventDrivenReadFromDevice() { UsbSerialController.stopEventRead(); }

  public void writeDataToDevice(String data) {
    usbSerialRepository.sendData(data);
  }

  public void readDataFromDevice() {
    ArrayList<UsbSerialOutputItem> list = usbSerialRepository.readData();

    for (int i = 0; i < list.size(); i++) {
      UsbSerialOutputItem item = list.get(i);
      Log.d("dev-log", "Time: " + item.getTimeInString() +
              "Message: " + item.getSerialOutputInString());
    }
  }
}
