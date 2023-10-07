package com.johndeweydev.awps.repository;

import com.johndeweydev.awps.usbserial.UsbDeviceItem;
import com.johndeweydev.awps.usbserial.UsbSerialMain;
import com.johndeweydev.awps.usbserial.UsbSerialMainSingleton;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;

import java.util.ArrayList;

public class UsbSerialRepository {

  public interface UsbSerialRepositoryCallback {
    void onNewData(UsbSerialOutputItem usbSerialOutputItem);
    void onErrorNewData();
    void onErrorWriting();
  }

  public void setUsbSerialViewModelCallback(
          UsbSerialViewModel.UsbSerialViewModelCallback usbSerialViewModelCallback
  ) {
    UsbSerialRepositoryCallback usbSerialRepositoryCallback = new UsbSerialRepositoryCallback() {
      @Override
      public void onNewData(UsbSerialOutputItem usbSerialOutputItem) {
        usbSerialViewModelCallback.onNewData(usbSerialOutputItem);
      }
      @Override
      public void onErrorNewData() {
        usbSerialViewModelCallback.onErrorNewData();
      }
      @Override
      public void onErrorWriting() {
        usbSerialViewModelCallback.onErrorWriting();
      }
    };
    UsbSerialMainSingleton.getInstance().getUsbSerialMain().setUsbSerialRepositoryCallback(
            usbSerialRepositoryCallback
    );
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
