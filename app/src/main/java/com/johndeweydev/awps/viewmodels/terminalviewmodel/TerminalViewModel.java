package com.johndeweydev.awps.viewmodels.terminalviewmodel;

import android.util.Log;

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
  public MutableLiveData<LauncherOutputData> currentSerialOutput = new MutableLiveData<>();
  public MutableLiveData<LauncherOutputData> currentSerialOutputRaw = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  TerminalRepositoryEvent terminalRepositoryEvent = new TerminalRepositoryEvent() {
    @Override
    public void onRepositoryOutputRaw(LauncherOutputData launcherSerialOutputData) {
      Log.d("dev-log", "TerminalViewModel.onRepositoryOutputRaw: Serial -> " +
              launcherSerialOutputData.getOutput());

      String time = "[" + launcherSerialOutputData.getTime() + "]";
      launcherSerialOutputData.setTime(time);
      currentSerialOutputRaw.postValue(launcherSerialOutputData);
    }
    @Override
    public void onRepositoryOutputFormatted(LauncherOutputData launcherSerialOutputData) {
      Log.d("dev-log", "TerminalViewModel.onRepositoryOutputFormatted: Serial -> " +
              launcherSerialOutputData.getOutput());

      String time = "[" + launcherSerialOutputData.getTime() + "]";
      launcherSerialOutputData.setTime(time);
      currentSerialOutput.postValue(launcherSerialOutputData);
    }
    @Override
    public void onRepositoryOutputError(String error) {
      Log.d("dev-log", "TerminalViewModel.onRepositoryOutputError: Serial -> " + error);
      currentSerialOutputError.postValue(error);
    }
    @Override
    public void onRepositoryInputError(String input) {
      Log.d("dev-log", "TerminalViewModel.onRepositoryInputError: Serial -> " + input);
      currentSerialInputError.postValue(input);
    }
  };
  public TerminalViewModel(TerminalRepository terminalRepository) {
    Log.d("dev-log", "TerminalViewModel: Setting event handler in terminal repository");
    this.terminalRepository = terminalRepository;
    terminalRepository.setEventHandler(terminalRepositoryEvent);
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
