package com.johndeweydev.awps.repository.usbserialrepository;

import com.johndeweydev.awps.repository.UsbSerialDataEvent;
import com.johndeweydev.awps.repository.UsbSerialOutputModel;
import com.johndeweydev.awps.repository.usbserialrepository.models.UsbDeviceModel;
import com.johndeweydev.awps.usbserial.UsbSerialMainSingleton;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.usbserialviewmodel.UsbSerialRepositoryEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class UsbSerialRepository {

  private final StringBuilder queueData = new StringBuilder();

  public void setUsbSerialViewModelCallback(
          UsbSerialRepositoryEvent usbSerialRepositoryEvent
  ) {
    UsbSerialDataEvent usbSerialDataEvent = new UsbSerialDataEvent() {
      @Override
      public void onUsbSerialOutput(String data) {
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

        UsbSerialOutputModel usbSerialOutputModel = new UsbSerialOutputModel(strTime, strData);
        usbSerialRepositoryEvent.onRepositoryOutputRaw(usbSerialOutputModel);

        char firstChar = strData.charAt(0);
        char lastChar = strData.charAt(strData.length() - 2);
        if (firstChar == '{' && lastChar == '}') {
          usbSerialRepositoryEvent.onRepositoryOutputFormatted(usbSerialOutputModel);
        }
      }

      @Override
      public void onUsbOutputError(String errorMessageOnNewData) {
        usbSerialRepositoryEvent.onRepositoryOutputError(errorMessageOnNewData);
      }
      @Override
      public void onUsbInputError(String dataToWrite) {
        usbSerialRepositoryEvent.onRepositoryInputError(dataToWrite);
      }
    };

    UsbSerialMainSingleton.getInstance().getUsbSerialMain().setLauncherSerialDataEvent(
            usbSerialDataEvent
    );
  }

  private String createStringTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    return dateFormat.format(calendar.getTime());
  }

  public ArrayList<UsbDeviceModel> discoverDevices() {
    return UsbSerialMainSingleton.getInstance().getUsbSerialMain().discoverDevices();
  }

  public UsbSerialStatus connect(
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
