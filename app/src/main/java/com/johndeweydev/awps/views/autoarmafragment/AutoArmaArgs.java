package com.johndeweydev.awps.views.autoarmafragment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AutoArmaArgs implements Parcelable {

  private final int deviceId;
  private final int portNum;
  private final int baudRate;

  public AutoArmaArgs(int aDeviceId, int aPortNum, int aBaudRate) {
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

  protected AutoArmaArgs(Parcel in) {
    deviceId = in.readInt();
    portNum = in.readInt();
    baudRate = in.readInt();
  }

  public static final Creator<com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs>
          CREATOR = new Creator<com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs>() {
    @Override
    public com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs createFromParcel(Parcel in) {
      return new com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs(in);
    }

    @Override
    public com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs[] newArray(int size) {
      return new com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs[size];
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
