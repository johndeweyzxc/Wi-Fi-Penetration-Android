package com.johndeweydev.awps.data;

public class AccessPointData {

  private final String macAddress;
  private final String ssid;
  private final int rssi;
  private final int channel;

  public AccessPointData(String macAddress, String ssid, int rssi, int channel) {
    this.macAddress = macAddress;
    this.ssid = ssid;
    this.rssi = rssi;
    this.channel = channel;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public String getSsid() {
    return ssid;
  }

  public int getRssi() {
    return rssi;
  }

  public int getChannel() {
    return channel;
  }
}
