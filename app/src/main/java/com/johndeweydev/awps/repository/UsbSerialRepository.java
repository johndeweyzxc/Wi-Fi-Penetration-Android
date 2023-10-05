package com.johndeweydev.awps.repository;

import androidx.fragment.app.FragmentActivity;

import com.johndeweydev.awps.usbserial.UsbDeviceItem;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;
import com.johndeweydev.awps.usbserial.UsbSerialMain;

import java.util.ArrayList;

public class UsbSerialRepository {

  public ArrayList<UsbSerialOutputItem> readData() {
    return UsbSerialMain.readData();
  }

  public void sendData(String data) {
    UsbSerialMain.sendData(data);
  }

  public ArrayList<UsbDeviceItem> discoverDevices(
          FragmentActivity fragmentActivity, String serviceName
  ) {
    return UsbSerialMain.discoverDevices(fragmentActivity, serviceName);
  }
}
