package com.johndeweydev.himawhs.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TerminalArgsModel implements Parcelable {

  private final int deviceId;
  private final int portNum;
  private final int baudRate;

  public TerminalArgsModel(int aDeviceId, int aPortNum, int aBaudRate) {
    deviceId = aDeviceId;
    portNum = aPortNum;
    baudRate = aBaudRate;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public int getPortNum() {
    return portNum;
  }

  public int getBaudRate() {
    return baudRate;
  }

  protected TerminalArgsModel(Parcel in) {
    deviceId = in.readInt();
    portNum = in.readInt();
    baudRate = in.readInt();
  }

  public static final Creator<TerminalArgsModel> CREATOR = new Creator<TerminalArgsModel>() {
    @Override
    public TerminalArgsModel createFromParcel(Parcel in) {
      return new TerminalArgsModel(in);
    }

    @Override
    public TerminalArgsModel[] newArray(int size) {
      return new TerminalArgsModel[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeInt(deviceId);
    dest.writeInt(portNum);
    dest.writeInt(baudRate);
  }
}
