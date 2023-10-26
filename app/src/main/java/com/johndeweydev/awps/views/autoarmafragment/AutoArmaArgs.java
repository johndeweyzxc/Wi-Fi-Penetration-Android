package com.johndeweydev.awps.views.autoarmafragment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AutoArmaArgs implements Parcelable {

  private final int deviceId;
  private final int portNum;
  private final int baudRate;
  private String selectedArmament = "";

  public AutoArmaArgs(int deviceId, int portNum, int baudRate, String selectedArmament) {
    this.deviceId = deviceId;
    this.portNum = portNum;
    this.baudRate = baudRate;
    this.selectedArmament = selectedArmament;
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

  public String getSelectedArmament() {return selectedArmament;}

  protected AutoArmaArgs(Parcel in) {
    deviceId = in.readInt();
    portNum = in.readInt();
    baudRate = in.readInt();
    selectedArmament = in.readString();
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
    dest.writeString(selectedArmament);
  }
}
