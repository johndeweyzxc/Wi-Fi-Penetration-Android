package com.johndeweydev.awps.viewmodels.terminalviewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.models.repo.serial.terminalreposerial.TerminalRepoSerial;
import com.johndeweydev.awps.data.UsbDeviceData;
import com.johndeweydev.awps.models.repo.serial.terminalreposerial.TerminalRepoSerialEvent;
import com.johndeweydev.awps.viewmodels.DefaultViewModelUsbSerial;

import java.util.ArrayList;

public class TerminalViewModel extends ViewModel implements DefaultViewModelUsbSerial {

  /**
   * Device id, port number and baud rate is set by terminal fragment as a backup source
   * in case getArgument is empty when terminal fragment goes to onCreateView state
   * */
  public int deviceIdFromTerminalArgs;
  public int portNumFromTerminalArgs;
  public int baudRateFromTerminalArgs;

  public TerminalRepoSerial terminalRepoSerial;
  public MutableLiveData<ArrayList<UsbDeviceData>> devicesList = new MutableLiveData<>();
  public MutableLiveData<LauncherOutputData> currentSerialOutput = new MutableLiveData<>();
  public MutableLiveData<LauncherOutputData> currentSerialOutputRaw = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialInputError = new MutableLiveData<>();
  public MutableLiveData<String> currentSerialOutputError = new MutableLiveData<>();

  TerminalRepoSerialEvent terminalRepoSerialEvent = new TerminalRepoSerialEvent() {
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
  public TerminalViewModel(TerminalRepoSerial terminalRepoSerial) {
    Log.d("dev-log", "TerminalViewModel: Created new instance of TerminalViewModel");
    this.terminalRepoSerial = terminalRepoSerial;
    terminalRepoSerial.setEventHandler(terminalRepoSerialEvent);
  }

  @Override
  public void setLauncherEventHandler() {
    terminalRepoSerial.setLauncherEventHandler();
  }

  @Override
  public String connectToDevice(DeviceConnectionParamData deviceConnectionParamData) {
    return terminalRepoSerial.connectToDevice(deviceConnectionParamData);
  }

  @Override
  public void disconnectFromDevice() {
    terminalRepoSerial.disconnectFromDevice();
  }

  @Override
  public void startEventDrivenReadFromDevice() {
    terminalRepoSerial.startEventDrivenReadFromDevice();
  }

  @Override
  public void stopEventDrivenReadFromDevice() {
    terminalRepoSerial.stopEventDrivenReadFromDevice();
  }

  public void writeDataToDevice(String data) {
    terminalRepoSerial.writeDataToDevice(data);
  }

  public int getAvailableDevices() {
    ArrayList<UsbDeviceData> devices = terminalRepoSerial.getAvailableDevices();
    devicesList.setValue(devices);
    return devices.size();
  }
}
