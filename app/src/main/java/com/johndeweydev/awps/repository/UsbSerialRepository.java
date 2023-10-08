package com.johndeweydev.awps.repository;

import android.util.Log;

import com.johndeweydev.awps.usbserial.UsbDeviceItem;
import com.johndeweydev.awps.usbserial.UsbSerialMain;
import com.johndeweydev.awps.usbserial.UsbSerialMainSingleton;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UsbSerialRepository {

  public interface UsbSerialRepositoryCallback {
    void onNewData(String data);
    void onErrorNewData(String errorMessageOnNewData);
    void onErrorWriting(String dataToWrite);
  }

  private final StringBuilder queueData = new StringBuilder();

  public void setUsbSerialViewModelCallback(
          UsbSerialViewModel.UsbSerialViewModelCallback usbSerialViewModelCallback
  ) {
    UsbSerialRepositoryCallback usbSerialRepositoryCallback = new UsbSerialRepositoryCallback() {
      @Override
      public void onNewData(String data) {
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

        Log.d("dev-log", "UsbSerialRepository.notifyViewModelAboutData: " +
                "Data in -> " + strData);

        UsbSerialOutputItem usbSerialOutputItem = new UsbSerialOutputItem(strTime, strData);
        usbSerialViewModelCallback.onNewDataRaw(usbSerialOutputItem);

        char firstChar = strData.charAt(0);
        char lastChar = strData.charAt(strData.length() - 2);
        if (firstChar == '{' && lastChar == '}') {
          usbSerialViewModelCallback.onNewDataFormatted(usbSerialOutputItem);
        }
      }

      @Override
      public void onErrorNewData(String errorMessageOnNewData) {
        usbSerialViewModelCallback.onErrorNewData(errorMessageOnNewData);
      }
      @Override
      public void onErrorWriting(String dataToWrite) {
        usbSerialViewModelCallback.onErrorWriting(dataToWrite);
      }
    };

    UsbSerialMainSingleton.getInstance().getUsbSerialMain().setUsbSerialRepositoryCallback(
            usbSerialRepositoryCallback
    );
  }

  private String createStringTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    return dateFormat.format(calendar.getTime());
  }

  public ArrayList<UsbDeviceItem> discoverDevices() {
    return UsbSerialMainSingleton.getInstance().getUsbSerialMain().discoverDevices();
  }

  public UsbSerialMain.ReturnStatus connect(
          int baudRate, int dataBits, int stopBits, int parity, int deviceId, int portNum) {
    return UsbSerialMainSingleton.getInstance().getUsbSerialMain().connect(
            baudRate, dataBits, stopBits, parity, deviceId, portNum
    );
  }

  public void disconnect() {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().disconnect();
  }

  public void startReading() {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().startReading();
  }

  public void stopReading() {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().stopReading();
  }

  public void writeData(String data) {
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().writeData(data);
  }
}
