package com.johndeweydev.awps.views.hashinfomodalbottomsheetdialog;

import android.os.Parcel;
import android.os.Parcelable;

public class HashInfoModalBottomArgs implements Parcelable {

  private final String ssid;
  private final String bssid;
  private final String clientMacAddress;
  private final String keyType;
  private final String keyData;
  private final String aNonce;
  private final String hashData;

  private final String latitude;
  private final String longitude;
  private final String address;

  private final String dateCaptured;

  public HashInfoModalBottomArgs(
          String ssid,
          String bssid,
          String clientMacAddress,
          String keyType,
          String keyData,
          String aNonce,
          String hashData,
          String latitude,
          String longitude,
          String address,
          String dateCaptured
  ) {
    this.ssid = ssid;
    this.bssid = bssid;
    this.clientMacAddress = clientMacAddress;
    this.keyType = keyType;
    this.keyData = keyData;
    this.aNonce = aNonce;
    this.hashData = hashData;
    this.latitude = latitude;
    this.longitude = longitude;
    this.address = address;
    this.dateCaptured = dateCaptured;
  }

  public String getSsid() {
    return ssid;
  }

  public String getBssid() {
    return bssid;
  }

  public String getClientMacAddress() {
    return clientMacAddress;
  }

  public String getKeyType() {
    return keyType;
  }

  public String getKeyData() {
    return keyData;
  }

  public String getaNonce() {
    return aNonce;
  }

  public String getHashData() {
    return hashData;
  }

  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public String getAddress() {
    return address;
  }

  public String getDateCaptured() {
    return dateCaptured;
  }

  protected HashInfoModalBottomArgs(Parcel in) {
    ssid = in.readString();
    bssid = in.readString();
    clientMacAddress = in.readString();
    keyType = in.readString();
    keyData = in.readString();
    aNonce = in.readString();
    hashData = in.readString();
    latitude = in.readString();
    longitude = in.readString();
    address = in.readString();
    dateCaptured = in.readString();
  }

  public static final Creator<HashInfoModalBottomArgs> CREATOR = new Creator<>() {
    @Override
    public HashInfoModalBottomArgs createFromParcel(Parcel in) {
      return new HashInfoModalBottomArgs(in);
    }

    @Override
    public HashInfoModalBottomArgs[] newArray(int size) {
      return new HashInfoModalBottomArgs[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(ssid);
    dest.writeString(bssid);
    dest.writeString(clientMacAddress);
    dest.writeString(keyType);
    dest.writeString(keyData);
    dest.writeString(aNonce);
    dest.writeString(hashData);
    dest.writeString(latitude);
    dest.writeString(longitude);
    dest.writeString(address);
    dest.writeString(dateCaptured);
  }
}
