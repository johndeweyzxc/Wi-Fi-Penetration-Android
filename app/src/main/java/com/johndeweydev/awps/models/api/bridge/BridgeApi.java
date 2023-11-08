package com.johndeweydev.awps.models.api.bridge;

import androidx.room.Dao;

import com.johndeweydev.awps.models.data.BridgeGetRootResponseHttp;
import com.johndeweydev.awps.models.data.BridgeUploadRequestHttp;
import com.johndeweydev.awps.models.data.BridgeUploadResponseHttp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

@Dao
public interface BridgeApi {

  @GET("/")
  Call<BridgeGetRootResponseHttp> getRoot();
  @POST("/api/v1/upload-hash")
  Call<BridgeUploadResponseHttp> uploadHash(@Body BridgeUploadRequestHttp
                                                        bridgeUploadRequestHttp);
}
