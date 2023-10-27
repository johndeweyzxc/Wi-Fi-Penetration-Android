package com.johndeweydev.awps.viewmodels.terminalviewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.repository.terminalrepository.TerminalRepository;
import com.johndeweydev.awps.data.UsbDeviceData;
import com.johndeweydev.awps.repository.terminalrepository.TerminalRepositoryEvent;

import java.util.ArrayList;

public class TerminalViewModel extends ViewModel {

  public TerminalRepository terminalRepository;
  public MutableLiveData<ArrayList<UsbDeviceData>> devicesList = new MutableLiveData<>();
  public MutableLiveData<LauncherOutputData> currentMessageFormatted = new MutableLiveData<>();
  public MutableLiveData<LauncherOutputData> currentMessageRaw = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  public TerminalViewModel(TerminalRepository aTerminalRepository) {
    TerminalRepositoryEvent terminalRepositoryEvent = new TerminalRepositoryEvent() {
      @Override
      public void onRepositoryOutputRaw(LauncherOutputData launcherSerialOutput) {
        currentMessageRaw.postValue(launcherSerialOutput);
      }
      @Override
      public void onRepositoryOutputFormatted(LauncherOutputData launcherSerialOutput) {
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

    terminalRepository = aTerminalRepository;
    terminalRepository.setUsbSerialViewModelCallback(terminalRepositoryEvent);
  }

  public int checkAvailableUsbDevices() {
    ArrayList<UsbDeviceData> devices = terminalRepository.discoverDevices();
    devicesList.setValue(devices);
    return devices.size();
  }

  public String connectToDevice(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    return terminalRepository.connect(baudRate, dataBits, stopBits, parity, deviceId, portNum);
  }

  public void disconnectFromDevice() {
    terminalRepository.disconnect();
  }

  public void startEventDrivenReadFromDevice() {
    terminalRepository.startReading();
  }

  public void stopEventDrivenReadFromDevice() {
    terminalRepository.stopReading();
  }

  public void writeDataToDevice(String data) {
    terminalRepository.writeData(data);
  }
}
