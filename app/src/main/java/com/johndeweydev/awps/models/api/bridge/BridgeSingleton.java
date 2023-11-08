package com.johndeweydev.awps.models.api.bridge;

import android.util.Log;

public class BridgeSingleton {

  private Bridge bridge;
  private static BridgeSingleton instance;

  public static synchronized BridgeSingleton getInstance() {
    if (instance == null) {
      instance = new BridgeSingleton();
    }
    return instance;
  }

  public void setBridge(Bridge bridge) {
    this.bridge = bridge;
  }

  public Bridge getBridge() {
    if (bridge != null) {
      return bridge;
    } else {
      // Before using this method it is required to first create a new instance of this singleton
      // using getInstance then set the bridge using setBridge
      Log.e("dev-log", "BridgeSingleton.getBridgeApi: Instance of Bridge is not found in " +
              "the singleton");
      return null;
    }
  }
}
