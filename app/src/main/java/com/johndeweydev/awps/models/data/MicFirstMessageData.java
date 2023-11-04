package com.johndeweydev.awps.models.data;

public class MicFirstMessageData {
  private String bssid;
  private String client;
  private String anonce;

  public MicFirstMessageData(String bssid, String client, String anonce) {
    this.bssid = bssid;
    this.client = client;
    this.anonce = anonce;
  }

  public String getBssid() {
    return bssid;
  }

  public String getAnonce() {
    return anonce;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public void setAnonce(String anonce) {
    this.anonce = anonce;
  }
}
