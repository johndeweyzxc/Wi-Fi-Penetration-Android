package com.johndeweydev.awps.views.manualarmafragment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ManualArmaArgs implements Parcelable {

  private final int deviceId;
  private final int portNum;
  private final int baudRate;

  public ManualArmaArgs(int aDeviceId, int aPortNum, int aBaudRate) {
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

  protected ManualArmaArgs(Parcel in) {
    deviceId = in.readInt();
    portNum = in.readInt();
    baudRate = in.readInt();
  }

  public static final Creator<com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs>
          CREATOR = new Creator<com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs>() {
    @Override
    public com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs createFromParcel(Parcel in) {
      return new com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs(in);
    }

    @Override
    public com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs[] newArray(int size) {
      return new com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs[size];
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
