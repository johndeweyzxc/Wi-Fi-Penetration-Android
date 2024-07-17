package com.johndeweydev.awps.api.bridge;

import android.util.Log;

import com.johndeweydev.awps.UserDefinedSettings;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bridge {

  private final Retrofit retrofit;

  public Bridge() {
    UserDefinedSettings userDefinedSettings = UserDefinedSettings.getInstance();
    Log.w("dev-log", "Bridge: Created new instance of Bridge");
    Log.d("dev-log", "Bridge: " + userDefinedSettings.REST_API_URL);
    retrofit = new Retrofit.Builder()
            .baseUrl(userDefinedSettings.REST_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
  }

  public BridgeApi getApi() {
    return retrofit.create(BridgeApi.class);
  }
}
