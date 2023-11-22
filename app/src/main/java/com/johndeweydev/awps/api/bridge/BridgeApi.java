package com.johndeweydev.awps.api.bridge;

import androidx.room.Dao;

import com.johndeweydev.awps.AppConstants;
import com.johndeweydev.awps.models.data.BridgeGetRootResponseHttp;
import com.johndeweydev.awps.models.data.BridgeUploadRequestHttp;
import com.johndeweydev.awps.models.data.BridgeUploadResponseHttp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

@Dao
public interface BridgeApi {

  @GET(AppConstants.REST_API_ROOT_ENDPOINT)
  Call<BridgeGetRootResponseHttp> getRoot();
  @POST(AppConstants.REST_API_UPLOAD_ENDPOINT)
  Call<BridgeUploadResponseHttp> uploadHash(@Body BridgeUploadRequestHttp
                                                        bridgeUploadRequestHttp);
}
