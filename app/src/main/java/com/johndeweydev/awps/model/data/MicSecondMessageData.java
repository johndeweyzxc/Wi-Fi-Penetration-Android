package com.johndeweydev.awps.model.data;

public class MicSecondMessageData {

  public String version;
  public String type;
  public String length;
  public String keyDescriptionType;
  public String keyInformation;
  public String keyLength;
  public String replayCounter;
  public String snonce;
  public String keyIv;
  public String keyRsc;
  public String keyId;
  public String mic;
  public String keyDataLength;
  public String keyData;

  public MicSecondMessageData(
          String version, String type, String length, String keyDescriptionType,
          String keyInformation, String keyLength, String replayCounter, String snonce,
          String keyIv, String keyRsc, String keyId, String mic, String keyDataLength,
          String keyData
  ) {
    this.version = version;
    this.type = type;
    this.length = length;
    this.keyDescriptionType = keyDescriptionType;
    this.keyInformation = keyInformation;
    this.keyLength = keyLength;

    this.replayCounter = replayCounter;
    this.snonce = snonce;
    this.keyIv = keyIv;
    this.keyRsc = keyRsc;
    this.keyId = keyId;
    this.mic = mic;

    this.keyDataLength = keyDataLength;
    this.keyData = keyData;
  }
  public String getMic() {
    return mic;
  }
  public String getAllData() {
    return version + "-" +
            type + "-" +
            length + "-" +
            keyDescriptionType + "-" +
            keyInformation + "-" +
            keyLength + "-" +
            replayCounter + "-" +
            snonce + "-" +
            keyIv + "-" +
            keyRsc + "-" +
            keyId + "-" +
            mic + "-" +
            keyDataLength + "-" +
            keyData + "-";
  }
}
