package com.johndeweydev.awps.repository.sessionrepository.models;

public class PmkidFirstMessageModel {
  private String bssid;
  private String client;
  private String pmkid;

  public PmkidFirstMessageModel(String bssid, String client, String pmkid) {
    this.bssid = bssid;
    this.client = client;
    this.pmkid = pmkid;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public void setPmkid(String pmkid) {
    this.pmkid = pmkid;
  }

  public String getBssid() {
    return bssid;
  }

  public String getClient() {
    return client;
  }

  public String getPmkid() {
    return pmkid;
  }
}
