package com.johndeweydev.himawhs.serial;

import android.util.Log;

import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.johndeweydev.himawhs.models.SerialOutputModel;

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
      SerialOutputModel serialOutputModel = new SerialOutputModel(stringDate, strData);
      SerialDataSingleton.getInstance().appendData(serialOutputModel);
    }
  }

  @Override
  public void onRunError(Exception e) {
    Log.e("dev-log", "An error has occurred " + e.getMessage());
    UsbSerialCommunication.disconnect();
  }


}
