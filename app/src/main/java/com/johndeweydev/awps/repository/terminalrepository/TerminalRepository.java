package com.johndeweydev.awps.repository.terminalrepository;

import android.util.Log;

import com.johndeweydev.awps.launcher.LauncherStages;
import com.johndeweydev.awps.launcher.LauncherEvent;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.data.UsbDeviceData;
import com.johndeweydev.awps.launcher.LauncherSingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TerminalRepository {

  private final StringBuilder queueData = new StringBuilder();
  private TerminalRepositoryEvent terminalRepositoryEvent;

  LauncherEvent launcherEvent = new LauncherEvent() {
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
        terminalRepositoryEvent.onRepositoryOutputFormatted(launcherOutputData);
      } else {
        terminalRepositoryEvent.onRepositoryOutputRaw(launcherOutputData);
      }
    }

    @Override
    public void onLauncherOutputError(String error) {
      terminalRepositoryEvent.onRepositoryOutputError(error);
    }
    @Override
    public void onLauncherInputError(String input) {
      terminalRepositoryEvent.onRepositoryInputError(input);
    }
  };

  public void setEventHandler(
          TerminalRepositoryEvent terminalRepositoryEvent
  ) {
    this.terminalRepositoryEvent = terminalRepositoryEvent;
    Log.d("dev-log", "TerminalRepository.setEventHandler: Setting event " +
            "handlers in launcher");
    LauncherSingleton.getInstance().getLauncher().setLauncherSerialDataEvent(
            launcherEvent
    );
  }

  private String createStringTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    return dateFormat.format(calendar.getTime());
  }

  public ArrayList<UsbDeviceData> discoverDevices() {
    return LauncherSingleton.getInstance().getLauncher().discoverDevices();
  }

  public String connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    LauncherStages status = LauncherSingleton.getInstance().getLauncher().initiateConnectionToDevice(
            baudRate, dataBits, stopBits, parity, deviceId, portNum
    );

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

  public void disconnect() {
    LauncherSingleton.getInstance().getLauncher().disconnect();
  }

  public void startReading() {
    LauncherSingleton.getInstance().getLauncher().startReading();
  }

  public void stopReading() {
    LauncherSingleton.getInstance().getLauncher().stopReading();
  }

  public void writeData(String data) {
    LauncherSingleton.getInstance().getLauncher().writeData(data);
  }
}
