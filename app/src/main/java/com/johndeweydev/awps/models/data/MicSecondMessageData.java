package com.johndeweydev.awps.models.data;

public class MicSecondMessageData {
  // Version, Type, Length and Key Description Type
  public String messageInfo;
  public String replayCounter;
  public String snonce;
  public String mic;
  public String wpaKeyData;

  public MicSecondMessageData(String messageInfo, String replayCounter, String snonce, String mic,
          String wpaKeyData
  ) {
    this.messageInfo = messageInfo;
    this.replayCounter = replayCounter;
    this.snonce = snonce;
    this.mic = mic;
    this.wpaKeyData = wpaKeyData;
  }
  public String getMic() {
    return mic;
  }
  public String getAllData() {
    return "-" + messageInfo + "-" + replayCounter + "-" + snonce + "-" + wpaKeyData;
  }
}
