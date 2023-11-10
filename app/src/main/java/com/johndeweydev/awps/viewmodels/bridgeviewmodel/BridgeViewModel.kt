package com.johndeweydev.awps.viewmodels.bridgeviewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johndeweydev.awps.models.api.bridge.BridgeSingleton
import com.johndeweydev.awps.models.data.BridgeUploadRequestHttp
import com.johndeweydev.awps.models.data.HashInfoEntity
import com.johndeweydev.awps.models.repo.network.BridgeRepoNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This view model is written in kotlin because coroutines works well with kotlin
 * */
class BridgeViewModel : ViewModel(), BridgeViewModelEvent {

  private var bridgeRepoNetwork: BridgeRepoNetwork
  val uploadedHash: MutableLiveData<String> = MutableLiveData()

  // For testing purposes only, this is the root endpoint of the rest api server
  private val messageFromRootEndpoint: MutableLiveData<String> = MutableLiveData()

  init {
    Log.w("dev-log", "BridgeViewModel: Created new instance of BridgeViewModel")
    val bridgeInstance = BridgeSingleton.getInstance()
    val bridgeApi = bridgeInstance.bridge.api
    bridgeRepoNetwork = BridgeRepoNetwork(bridgeApi, this)
  }

  fun getRoot() {
    viewModelScope.launch(Dispatchers.IO) {
      bridgeRepoNetwork.getRoot()
    }
  }

  fun uploadHash(hashInfoEntity: HashInfoEntity) {
    // This hash info entity comes from the recycler view item, so we need to remove
    // unnecessary characters
    val keyType = hashInfoEntity.keyType.replace("Type: ", "")
    val aNonce = hashInfoEntity.aNonce.replace("Anonce: ", "")
    val hashData = hashInfoEntity.hashData.replace("Hash: ", "")
    val keyData = hashInfoEntity.keyData.replace("Data: ", "")
    val dateCaptured = hashInfoEntity.dateCaptured.replace("Date captured: ", "")

    viewModelScope.launch(Dispatchers.IO) {
      val bridgeUploadRequestHttp = BridgeUploadRequestHttp(
              hashInfoEntity.ssid,
              hashInfoEntity.bssid,
              hashInfoEntity.clientMacAddress,
              keyType, aNonce, hashData, keyData, dateCaptured
      )

      bridgeRepoNetwork.uploadHash(bridgeUploadRequestHttp)
    }
  }

  override fun onHttpGetResponseSuccess(message: String) {
    messageFromRootEndpoint.postValue(message)
  }

  override fun onHttpUploadResponseSuccess(hashData: String) {
    uploadedHash.postValue(hashData)
  }

  override fun onHttpResponseUnsuccessful(reason: String) {
    Log.e("dev-log", "BridgeViewModel.onHttpResponseNotSuccess: $reason")
  }

  override fun onHttpFailure(reason: String) {
    Log.e("dev-log", "BridgeViewModel.onHttpFailure: $reason")
  }
}