package com.johndeweydev.awps.usbserial;

import android.util.Log;

import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.johndeweydev.awps.controllers.UsbSerialController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UsbIoManagerListener implements SerialInputOutputManager.Listener {

  public UsbIoManagerListener() {
  }

  @Override
  public void onNewData(byte[] data) {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    String stringDate = dateFormat.format(calendar.getTime());

    if (data.length > 0) {
      String strData = new String(data, StandardCharsets.US_ASCII);
      UsbSerialOutputItem usbSerialOutputItem = new UsbSerialOutputItem(stringDate, strData);
      UsbSerialDataSingleton.getInstance().appendData(usbSerialOutputItem);
    }
  }

  @Override
  public void onRunError(Exception e) {
    Log.e("dev-log", "UsbIoManagerListener.onRunError: An error has occurred "
            + e.getMessage());
    UsbSerialController.disconnect();
  }
}
