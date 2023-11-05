package com.johndeweydev.awps.models.api.hashinfo;

import android.util.Log;

public class HashInfoSingleton {

  private HashInfoDatabase hashInfoDatabase;
  private static HashInfoSingleton instance;

  public static synchronized HashInfoSingleton getInstance() {
    if (instance == null) {
      instance = new HashInfoSingleton();
    }
    return instance;
  }

  public void setHashInfoDatabase(HashInfoDatabase aHashInfoDatabase) {
    this.hashInfoDatabase = aHashInfoDatabase;
  }

  public HashInfoDatabase getHashInfoDatabase() {
    if (hashInfoDatabase != null) {
      return hashInfoDatabase;
    } else {
      // Before using this method it is required to first create a new instance of this singleton
      // using getInstance then set the database using setHashInfoDatabase
      Log.e("dev-log", "HashInfoSingleton.getHashInfoDatabase: " +
              "Instance of HashInfoDatabase is not found in the singleton");
      return null;
    }
  }
}
