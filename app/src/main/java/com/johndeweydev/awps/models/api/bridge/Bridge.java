package com.johndeweydev.awps.models.api.bridge;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bridge {

  private final Retrofit retrofit;

  public Bridge() {
    Log.w("dev-log", "Bridge: Created new instance of Bridge");
    retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.9:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
  }

  public BridgeApi getApi() {
    return retrofit.create(BridgeApi.class);
  }
}
