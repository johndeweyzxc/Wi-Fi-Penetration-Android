package com.johndeweydev.himawhs.serial;

import com.johndeweydev.himawhs.models.SerialOutputModel;

import java.util.ArrayList;

public class SerialDataSingleton {
  private ArrayList<SerialOutputModel> data;

  private SerialDataSingleton() {
    data = new ArrayList<>();
  }

  private static SerialDataSingleton mInstance;
  public static synchronized SerialDataSingleton getInstance() {
    if (mInstance == null) {
      mInstance = new SerialDataSingleton();
    }
    return mInstance;
  }

  public void appendData(SerialOutputModel serialOutputModel) {
    data.add(serialOutputModel);
  }

  public ArrayList<SerialOutputModel> getAllData() {
    return data;
  }
}
