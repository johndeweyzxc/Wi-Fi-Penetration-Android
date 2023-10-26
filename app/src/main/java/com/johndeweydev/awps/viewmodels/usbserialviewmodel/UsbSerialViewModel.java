package com.johndeweydev.awps.viewmodels.usbserialviewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.repository.UsbSerialOutputModel;
import com.johndeweydev.awps.repository.usbserialrepository.UsbSerialRepository;
import com.johndeweydev.awps.repository.usbserialrepository.models.UsbDeviceModel;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;

import java.util.ArrayList;

public class UsbSerialViewModel extends ViewModel {

  public UsbSerialRepository usbSerialRepository;
  public MutableLiveData<ArrayList<UsbDeviceModel>> devicesList = new MutableLiveData<>();
  public MutableLiveData<UsbSerialOutputModel> currentMessageFormatted = new MutableLiveData<>();
  public MutableLiveData<UsbSerialOutputModel> currentMessageRaw = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  public UsbSerialViewModel(UsbSerialRepository aUsbSerialRepository) {
    UsbSerialRepositoryEvent usbSerialRepositoryEvent = new UsbSerialRepositoryEvent() {
      @Override
      public void onRepositoryOutputRaw(UsbSerialOutputModel launcherSerialOutput) {
        currentMessageRaw.postValue(launcherSerialOutput);
      }
      @Override
      public void onRepositoryOutputFormatted(UsbSerialOutputModel launcherSerialOutput) {
        currentMessageFormatted.postValue(launcherSerialOutput);
      }
      @Override
      public void onRepositoryOutputError(String error) {
        currentSerialOutputError.postValue(error);
      }
      @Override
      public void onRepositoryInputError(String input) {
        currentSerialInputError.postValue(input);
      }
    };

    usbSerialRepository = aUsbSerialRepository;
    usbSerialRepository.setUsbSerialViewModelCallback(usbSerialRepositoryEvent);
  }

  public int checkAvailableUsbDevices() {
    ArrayList<UsbDeviceModel> devices = usbSerialRepository.discoverDevices();
    devicesList.setValue(devices);
    return devices.size();
  }

  public UsbSerialStatus connectToDevice(
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
