package com.johndeweydev.awps.view.manualarmafragment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ManualArmaArgs implements Parcelable {

  private final int deviceId;
  private final int portNum;
  private final int baudRate;
  private String selectedArmament = "";

  public ManualArmaArgs(int deviceId, int portNum, int baudRate, String selectedArmament) {
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

  public String getSelectedArmament() {return selectedArmament;}

  protected ManualArmaArgs(Parcel in) {
    deviceId = in.readInt();
    portNum = in.readInt();
    baudRate = in.readInt();
    selectedArmament = in.readString();
  }

  public static final Creator<com.johndeweydev.awps.view.manualarmafragment.ManualArmaArgs>
          CREATOR = new Creator<>() {
    @Override
    public com.johndeweydev.awps.view.manualarmafragment.ManualArmaArgs createFromParcel(Parcel in) {
      return new com.johndeweydev.awps.view.manualarmafragment.ManualArmaArgs(in);
    }

    @Override
    public com.johndeweydev.awps.view.manualarmafragment.ManualArmaArgs[] newArray(int size) {
      return new com.johndeweydev.awps.view.manualarmafragment.ManualArmaArgs[size];
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
