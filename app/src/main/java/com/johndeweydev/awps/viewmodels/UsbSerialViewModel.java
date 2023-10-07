package com.johndeweydev.awps.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.repository.UsbSerialRepository;
import com.johndeweydev.awps.usbserial.UsbDeviceItem;
import com.johndeweydev.awps.usbserial.UsbSerialMain;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;

import java.util.ArrayList;

public class UsbSerialViewModel extends ViewModel {

  public interface UsbSerialViewModelCallback {
    void onNewData(UsbSerialOutputItem usbSerialOutputItem);
    void onErrorNewData();
    void onErrorWriting();
  }

  public UsbSerialRepository usbSerialRepository;
  public MutableLiveData<ArrayList<UsbDeviceItem>> devicesList = new MutableLiveData<>();
  public MutableLiveData<UsbSerialOutputItem> currentSerialMessage = new MutableLiveData<>();

  public UsbSerialViewModel(UsbSerialRepository aUsbSerialRepository) {
    UsbSerialViewModelCallback usbSerialViewModelCallback = new UsbSerialViewModelCallback() {
      @Override
      public void onNewData(UsbSerialOutputItem usbSerialOutputItem) {

        // TODO: Fix exception: Cannot invoke setValue on a background thread

        currentSerialMessage.setValue(usbSerialOutputItem);
      }
      @Override
      public void onErrorNewData() {
        // TODO: Implement
      }
      @Override
      public void onErrorWriting() {
        // TODO: Implement
      }
    };

    usbSerialRepository = aUsbSerialRepository;
    usbSerialRepository.setUsbSerialViewModelCallback(usbSerialViewModelCallback);
  }

  public int checkAvailableUsbDevices() {
    ArrayList<UsbDeviceItem> devices = usbSerialRepository.discoverDevices();
    devicesList.setValue(devices);
    return devices.size();
  }

  public UsbSerialMain.ReturnStatus connectToDevice(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    return usbSerialRepository.connect(baudRate, dataBits, stopBits, parity, deviceId, portNum);
  }

  public void disconnectFromDevice() {
    usbSerialRepository.disconnect();
  }

  public void startEventDrivenReadFromDevice() {
    usbSerialRepository.startReading();
  }

  public void stopEventDrivenReadFromDevice() {
    usbSerialRepository.stopReading();
  }

  public void writeDataToDevice(String data) {
    usbSerialRepository.writeData(data);
  }
}
