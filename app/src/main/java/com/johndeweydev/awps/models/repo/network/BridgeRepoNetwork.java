package com.johndeweydev.awps.models.repo.network;

import androidx.annotation.NonNull;

import com.johndeweydev.awps.models.api.bridge.BridgeApi;
import com.johndeweydev.awps.models.data.BridgeGetRootResponseHttp;
import com.johndeweydev.awps.models.data.BridgeUploadRequestHttp;
import com.johndeweydev.awps.models.data.BridgeUploadResponseHttp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BridgeRepoNetwork {

  /**
   * Callbacks when an http response is received, the response can be successful or a failure such
   * as when the response body is null
   *
   * @author John Dewey (johndewey02003@gmail.com)
   * */
  public interface HttpEvent {

    /**
     * The http get request to the root endpoint is successful
     * @param message the response message from the root endpoint
     * */
    void onHttpGetResponseSuccess(String message);

    /**
     * The http post request to the root endpoint is successful
     * @param hashData the uploaded MIC or PMKID
     * */
    default void onHttpUploadResponseSuccess(String hashData) {}

    /**
     * An http request is unsuccessful
     * @param reason the reason that is was unsuccessful
     * */
    void onHttpResponseUnsuccessful(String reason);

    /**
     * An http request has failed, this could be an IO exception
     * @param reason the reason it failed
     * */
    void onHttpFailure(String reason);
  }

  private final BridgeApi bridgeApi;
  private final BridgeRepoNetwork.HttpEvent httpEvent;

  public BridgeRepoNetwork(BridgeApi bridgeApi, BridgeRepoNetwork.HttpEvent httpEvent) {
    this.bridgeApi = bridgeApi;
    this.httpEvent = httpEvent;
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
          httpEvent.onHttpResponseUnsuccessful("Get request unsuccessful, " +
                  "status code is " + response.code());
          return;
        }

        BridgeGetRootResponseHttp responseData = response.body();
        if (responseData == null) {
          httpEvent.onHttpResponseUnsuccessful("Get response body is null");
          return;
        }

        httpEvent.onHttpGetResponseSuccess(responseData.message());
      }

      @Override
      public void onFailure(@NonNull Call<BridgeGetRootResponseHttp> call, @NonNull Throwable t) {
        if (t.getMessage() != null) {
          httpEvent.onHttpFailure(t.getMessage());
        } else {
          httpEvent.onHttpFailure("Http failure, throwable message is null");
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
          httpEvent.onHttpResponseUnsuccessful("Post request unsuccessful, " +
                  "status code is " + response.code());
          return;
        }

        BridgeUploadResponseHttp responseData = response.body();
        if (responseData == null) {
          httpEvent.onHttpResponseUnsuccessful("Post response body is null");
          return;
        }

        httpEvent.onHttpUploadResponseSuccess(responseData.hash_data());
      }

      @Override
      public void onFailure(@NonNull Call<BridgeUploadResponseHttp> call, @NonNull Throwable t) {
        if (t.getMessage() != null) {
          httpEvent.onHttpFailure(t.getMessage());
        } else {
          httpEvent.onHttpFailure("Http failure, throwable message is null");
        }
      }
    });
  }
}
