package com.johndeweydev.awps.models;

public class MicSecondMessageModel {
  public String client;
  public String bssid;

  /*
  /* Version, Type, Length and Key Description Type
   */
  public String messageInfo;

  public String replayCounter;
  public String snonce;
  public String mic;
  public String wpaKeyData;

  public MicSecondMessageModel(
          String client,
          String bssid,
          String messageInfo,
          String replayCounter,
          String snonce,
          String mic,
          String wpaKeyData
  ) {
    this.client = client;
    this.bssid = bssid;
    this.messageInfo = messageInfo;
    this.replayCounter = replayCounter;
    this.snonce = snonce;
    this.mic = mic;
    this.wpaKeyData = wpaKeyData;
  }

  public String getClient() {
    return client;
  }

  public String getBssid() {
    return bssid;
  }

  public String getMessageInfo() {
    return messageInfo;
  }

  public String getReplayCounter() {
    return replayCounter;
  }

  public String getSnonce() {
    return snonce;
  }

  public String getMic() {
    return mic;
  }

  public String getWpaKeyData() {
    return wpaKeyData;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  public void setMessageInfo(String messageInfo) {
    this.messageInfo = messageInfo;
  }

  public void setReplayCounter(String replayCounter) {
    this.replayCounter = replayCounter;
  }

  public void setSnonce(String snonce) {
    this.snonce = snonce;
  }

  public void setMic(String mic) {
    this.mic = mic;
  }

  public void setWpaKeyData(String wpaKeyData) {
    this.wpaKeyData = wpaKeyData;
  }
}
