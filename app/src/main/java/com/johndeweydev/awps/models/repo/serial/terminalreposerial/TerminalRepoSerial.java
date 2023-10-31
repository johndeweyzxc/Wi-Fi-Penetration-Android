package com.johndeweydev.awps.models.repo.serial.terminalreposerial;

import android.util.Log;

import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.models.api.launcher.LauncherStages;
import com.johndeweydev.awps.models.api.launcher.LauncherEvent;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.data.UsbDeviceData;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TerminalRepoSerial {

  private TerminalRepoSerialEvent terminalRepoSerialEvent;
  private final StringBuilder queueData = new StringBuilder();
  private final LauncherEvent launcherEvent = new LauncherEvent() {
    @Override
    public void onLauncherOutput(String data) {
      char[] dataChar = data.toCharArray();
      for (char c : dataChar) {
        if (c == '\n') {
          notifyViewModelAboutData();
          queueData.setLength(0);
        } else {
          queueData.append(c);
        }
      }
    }

    private void notifyViewModelAboutData() {
      String strData = queueData.toString();
      String strTime = createStringTime();

      LauncherOutputData launcherOutputData = new LauncherOutputData(strTime, strData);

      char firstChar = strData.charAt(0);
      char lastChar = strData.charAt(strData.length() - 2);
      if (firstChar == '{' && lastChar == '}') {
        terminalRepoSerialEvent.onRepositoryOutputFormatted(launcherOutputData);
      } else {
        terminalRepoSerialEvent.onRepositoryOutputRaw(launcherOutputData);
      }
    }

    @Override
    public void onLauncherOutputError(String error) {
      terminalRepoSerialEvent.onRepositoryOutputError(error);
    }
    @Override
    public void onLauncherInputError(String input) {
      terminalRepoSerialEvent.onRepositoryInputError(input);
    }
  };

  public void setEventHandler(
          TerminalRepoSerialEvent terminalRepoSerialEvent
  ) {
    this.terminalRepoSerialEvent = terminalRepoSerialEvent;
    Log.d("dev-log", "TerminalRepository.setEventHandler: Terminal repository event " +
            "callback set");
  }

  public void setLauncherEventHandler() {
    LauncherSingleton.getInstance().getLauncher().setLauncherEventHandler(
            launcherEvent);
    Log.d("dev-log", "TerminalRepository.setLauncherEventHandler: Launcher event callback " +
            "set in the context of terminal repository");
  }

  private String createStringTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    return dateFormat.format(calendar.getTime());
  }

  public ArrayList<UsbDeviceData> getAvailableDevices() {
    return LauncherSingleton.getInstance().getLauncher().getAvailableDevices();
  }

  public String connectToDevice(DeviceConnectionParamData deviceConnectionParamData) {
    LauncherStages status = LauncherSingleton.getInstance().getLauncher()
            .connectToDevice(deviceConnectionParamData);

    switch (status) {
      case ALREADY_CONNECTED: return "Already connected";
      case DEVICE_NOT_FOUND: return "Device not found";
      case DRIVER_NOT_FOUND: return "Driver not found";
      case PORT_NOT_FOUND: return "Port not found";
      case NO_USB_PERMISSION: return "No usb permission";
      case SUCCESSFULLY_CONNECTED: return "Successfully connected";
      case UNSUPPORTED_PORT_PARAMETERS: return "Unsupported port parameters";
      case FAILED_OPENING_DEVICE: return "Failed to open the device";
      default: return "None";
    }
  }

  public void disconnectFromDevice() {
    LauncherSingleton.getInstance().getLauncher().disconnectFromDevice();
  }

  public void startEventDrivenReadFromDevice() {
    LauncherSingleton.getInstance().getLauncher().startEventDrivenReadFromDevice();
  }

  public void stopEventDrivenReadFromDevice() {
    LauncherSingleton.getInstance().getLauncher().stopEventDrivenReadFromDevice();
  }

  public void writeDataToDevice(String data) {
    LauncherSingleton.getInstance().getLauncher().writeDataToDevice(data);
  }
}
