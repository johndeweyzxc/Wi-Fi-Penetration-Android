package com.johndeweydev.awps.models.api.bridge;

import android.util.Log;

import com.johndeweydev.awps.UserDefinedSettings;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bridge {

  private final Retrofit retrofit;

  public Bridge() {
    Log.w("dev-log", "Bridge: Created new instance of Bridge");
    retrofit = new Retrofit.Builder()
            .baseUrl(UserDefinedSettings.REST_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
  }

  public BridgeApi getApi() {
    return retrofit.create(BridgeApi.class);
  }
}
