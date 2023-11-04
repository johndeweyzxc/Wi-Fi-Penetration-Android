package com.johndeweydev.awps.models.repo.serial.terminalreposerial;

import android.util.Log;

import com.johndeweydev.awps.models.repo.serial.RepoIOControl;
import com.johndeweydev.awps.models.repo.serial.RepoIOEvent;
import com.johndeweydev.awps.models.data.LauncherOutputData;
import com.johndeweydev.awps.models.data.UsbDeviceData;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModelEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TerminalRepoSerial extends RepoIOControl implements RepoIOEvent {

  private TerminalViewModelEvent terminalViewModelEvent;
  private final StringBuilder queueData = new StringBuilder();
  @Override
  public void onUsbSerialOutput(String data) {
    char[] dataChar = data.toCharArray();
    for (char c : dataChar) {
      if (c == '\n') {

        String strData = queueData.toString();
        String strTime = createStringTime();

        LauncherOutputData launcherOutputData = new LauncherOutputData(strTime, strData);

        char firstChar = strData.charAt(0);
        char lastChar = strData.charAt(strData.length() - 2);
        if (firstChar == '{' && lastChar == '}') {
          terminalViewModelEvent.onLauncherOutputFormatted(launcherOutputData);
        } else {
          terminalViewModelEvent.onLauncherOutputRaw(launcherOutputData);
        }

        queueData.setLength(0);
      } else {
        queueData.append(c);
      }
    }
  }

  @Override
  public void onUsbSerialOutputError(String error) {
    terminalViewModelEvent.onLauncherOutputError(error);
  }
  @Override
  public void onUsbSerialInputError(String input) {
    terminalViewModelEvent.onLauncherInputError(input);
  }
  public void setEventHandler(
          TerminalViewModelEvent terminalRepoSerialEvent
  ) {
    this.terminalViewModelEvent = terminalRepoSerialEvent;
    Log.d("dev-log", "TerminalRepository.setEventHandler: Terminal repository event " +
            "callback set");
  }

  public void setLauncherEventHandler() {
    LauncherSingleton.getInstance().getLauncher().setLauncherEventHandler(
            this);
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
}
