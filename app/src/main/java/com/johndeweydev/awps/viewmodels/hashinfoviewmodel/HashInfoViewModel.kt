package com.johndeweydev.awps.viewmodels.hashinfoviewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johndeweydev.awps.models.api.hashinfo.HashInfoSingleton
import com.johndeweydev.awps.models.data.HashInfoEntity
import com.johndeweydev.awps.models.repo.database.HashInfoRepoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * This view model is written in kotlin because coroutines works well with kotlin
 * */
class HashInfoViewModel : ViewModel() {

  private var hashInfoRepoDatabase: HashInfoRepoDatabase
  val hashInfoEntities: MutableLiveData<ArrayList<HashInfoEntity>> = MutableLiveData()

  init {
    Log.w("dev-log", "HashInfoViewModel: Created new instance of HashInfoViewModel")
    val databaseInstance = HashInfoSingleton.getInstance()
    val hashInfoDao = databaseInstance.hashInfoDatabase.hashInfoDao()
    hashInfoRepoDatabase = HashInfoRepoDatabase(hashInfoDao)
  }

  fun addNewHashInfo(hashInfoEntity: HashInfoEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      hashInfoRepoDatabase.addNewHashInfo(hashInfoEntity)
    }
  }

  fun getAllHashInfo() {
    viewModelScope.launch(Dispatchers.IO) {
      val result = async {
        hashInfoRepoDatabase.allHashInfo
      }.await()

      result.forEachIndexed { index, hashInfoEntity ->
        hashInfoEntity.bssid = formatMacAddress(hashInfoEntity.bssid)
        hashInfoEntity.clientMacAddress = formatMacAddress(hashInfoEntity.clientMacAddress)
        hashInfoEntity.keyType = "Type: " + hashInfoEntity.keyType
        hashInfoEntity.hashData = "Hash: " + hashInfoEntity.hashData
        hashInfoEntity.keyData = "Data: " + hashInfoEntity.keyData
        hashInfoEntity.dateCaptured = "Date captured: " + hashInfoEntity.dateCaptured
        result[index] = hashInfoEntity
      }

      hashInfoEntities.postValue(ArrayList(result))
    }
  }

  private fun formatMacAddress(bssid: String): String {
    val bssidFormatted = StringBuilder()

    // This formats the mac address (BSSID), e.g. "1234567890AB" will become "12:34:56:78:90:AB"
    var i = 0
    while (i < bssid.length) {
      // Append two characters at a time
      bssidFormatted.append(bssid[i])
      bssidFormatted.append(bssid[i + 1])

      // Append a colon if not at the end
      if (i + 2 < bssid.length) {
        bssidFormatted.append(":")
      }
      i += 2
    }
    return bssidFormatted.toString()
  }

  fun deleteAllHashInfo() {
    viewModelScope.launch(Dispatchers.IO) {
      hashInfoRepoDatabase.deleteAllHashInfo()
    }
  }
}