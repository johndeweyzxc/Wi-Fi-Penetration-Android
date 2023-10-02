package com.johndeweydev.himawhs.viewmodels;

import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.johndeweydev.himawhs.models.DeviceModel;
import com.johndeweydev.himawhs.models.SerialOutputModel;
import com.johndeweydev.himawhs.repository.DefaultMainRepository;

import java.util.ArrayList;
import java.util.List;

public class DefaultMainViewModel extends ViewModel {

  public DefaultMainRepository defaultMainRepository;
  public MutableLiveData<ArrayList<DeviceModel>> liveDeviceList = new MutableLiveData<>();
  public MutableLiveData<SerialOutputModel> currentSerialMessage = new MutableLiveData<>();

  public boolean isConnected = false;

  public DefaultMainViewModel(DefaultMainRepository aDefaultMainRepository) {
    defaultMainRepository = aDefaultMainRepository;
  }

  public int getAvailableDevicesFromSerial(FragmentActivity fragmentActivity) {
    ArrayList<DeviceModel> availableDevices = defaultMainRepository
            .getAvailableDevicesFromSerial(fragmentActivity);
    liveDeviceList.setValue(availableDevices);
    return availableDevices.size();
  }

  public boolean connectToDevice(
          UsbSerialDriver driver, UsbManager usbManager, int portNum, int baudRate
  ) {
    isConnected = defaultMainRepository.connect(driver, usbManager, portNum, baudRate);
    return isConnected;
  }

  public boolean disconnectFromDevice() {
    defaultMainRepository.disconnect();
    isConnected = false;
    return isConnected;
  }

  public void startEventDrivenReadFromDevice() {
    defaultMainRepository.startEventRead();
  }

  public void stopEventDrivenReadFromDevice() {defaultMainRepository.stopEventRead();}

  public void writeDataToDevice(String data) {
    defaultMainRepository.sendData(data);
  }

  public void readDataFromDevice() {
    ArrayList<SerialOutputModel> list = defaultMainRepository.readDataInSingleton();

    for (int i = 0; i < list.size(); i++) {
      SerialOutputModel item = list.get(i);
      Log.d("dev-log", "Time: " + item.getTimeInString() +
              "Message: " + item.getSerialOutputInString());
    }
  }
}
