package com.johndeweydev.awps.models.repo.network;

import androidx.annotation.NonNull;

import com.johndeweydev.awps.models.api.bridge.BridgeApi;
import com.johndeweydev.awps.models.data.BridgeGetRootResponseHttp;
import com.johndeweydev.awps.models.data.BridgeUploadRequestHttp;
import com.johndeweydev.awps.models.data.BridgeUploadResponseHttp;
import com.johndeweydev.awps.viewmodels.bridgeviewmodel.BridgeViewModelEvent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BridgeRepoNetwork {

  private final BridgeApi bridgeApi;
  private final BridgeViewModelEvent bridgeViewModelEvent;

  public BridgeRepoNetwork(BridgeApi bridgeApi, BridgeViewModelEvent bridgeViewModelEvent) {
    this.bridgeApi = bridgeApi;
    this.bridgeViewModelEvent = bridgeViewModelEvent;
  }

  public void getRoot() {
    Call<BridgeGetRootResponseHttp> response = bridgeApi.getRoot();

    response.enqueue(new Callback<>() {
      @Override
      public void onResponse(
              @NonNull Call<BridgeGetRootResponseHttp> call,
              @NonNull Response<BridgeGetRootResponseHttp> response
      ) {

        if (!response.isSuccessful()) {
          bridgeViewModelEvent.onHttpResponseUnsuccessful("Get request unsuccessful, " +
                  "status code is " + response.code());
          return;
        }

        BridgeGetRootResponseHttp responseData = response.body();
        if (responseData == null) {
          bridgeViewModelEvent.onHttpResponseUnsuccessful("Get response body is null");
          return;
        }

        bridgeViewModelEvent.onHttpGetResponseSuccess(responseData.message());
      }

      @Override
      public void onFailure(@NonNull Call<BridgeGetRootResponseHttp> call, @NonNull Throwable t) {
        if (t.getMessage() != null) {
          bridgeViewModelEvent.onHttpFailure(t.getMessage());
        } else {
          bridgeViewModelEvent.onHttpFailure("Http failure, throwable message is null");
        }
      }
    });

  }

  public void uploadHash(BridgeUploadRequestHttp bridgeUploadRequestHttp) {
    Call<BridgeUploadResponseHttp> response = bridgeApi.uploadHash(bridgeUploadRequestHttp);

    response.enqueue(new Callback<>() {
      @Override
      public void onResponse(
              @NonNull Call<BridgeUploadResponseHttp> call,
              @NonNull Response<BridgeUploadResponseHttp> response
      ) {

        if (!response.isSuccessful()) {
          bridgeViewModelEvent.onHttpResponseUnsuccessful("Post request unsuccessful, " +
                  "status code is " + response.code());
          return;
        }

        BridgeUploadResponseHttp responseData = response.body();
        if (responseData == null) {
          bridgeViewModelEvent.onHttpResponseUnsuccessful("Post response body is null");
          return;
        }

        bridgeViewModelEvent.onHttpUploadResponseSuccess(responseData.hash_data());
      }

      @Override
      public void onFailure(@NonNull Call<BridgeUploadResponseHttp> call, @NonNull Throwable t) {
        if (t.getMessage() != null) {
          bridgeViewModelEvent.onHttpFailure(t.getMessage());
        } else {
          bridgeViewModelEvent.onHttpFailure("Http failure, throwable message is null");
        }
      }
    });
  }
}
