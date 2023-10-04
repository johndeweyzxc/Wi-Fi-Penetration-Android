package com.johndeweydev.himawhs.usbserial;

import java.util.ArrayList;

public class UsbSerialDataSingleton {
  private ArrayList<UsbSerialOutputItem> data;

  private UsbSerialDataSingleton() {
    data = new ArrayList<>();
  }

  private static UsbSerialDataSingleton mInstance;
  public static synchronized UsbSerialDataSingleton getInstance() {
    if (mInstance == null) {
      mInstance = new UsbSerialDataSingleton();
    }
    return mInstance;
  }

  public void appendData(UsbSerialOutputItem usbSerialOutputItem) {
    data.add(usbSerialOutputItem);
  }

  public ArrayList<UsbSerialOutputItem> getAllData() {
    return data;
  }
}
