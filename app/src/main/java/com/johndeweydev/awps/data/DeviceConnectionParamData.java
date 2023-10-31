package com.johndeweydev.awps.data;

public class DeviceConnectionParamData {
  private final int baudRate;
  private final int dataBits;
  private final int stopBits;
  private final String parity;
  private final int deviceId;
  private final int portNum;

  public DeviceConnectionParamData(
          int baudRate, int dataBits, int stopBits, String parity, int deviceId, int portNum
  ) {
    this.baudRate = baudRate;
    this.dataBits = dataBits;
    this.stopBits = stopBits;
    this.parity = parity;
    this.deviceId = deviceId;
    this.portNum = portNum;
  }

  public int getBaudRate() {
    return baudRate;
  }

  public int getDataBits() {
    return dataBits;
  }

  public int getStopBits() {
    return stopBits;
  }

  public String getParity() {
    return parity;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public int getPortNum() {
    return portNum;
  }
}
