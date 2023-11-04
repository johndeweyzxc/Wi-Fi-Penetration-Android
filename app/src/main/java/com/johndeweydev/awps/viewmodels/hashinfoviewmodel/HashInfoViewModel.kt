package com.johndeweydev.awps.viewmodels.hashinfoviewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johndeweydev.awps.models.data.HashInfoEntity
import com.johndeweydev.awps.models.api.hashinfo.HashInfoSingleton
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
      }
      hashInfoEntities.postValue(ArrayList(result.await()))
    }
  }

  fun deleteAllHashInfo() {
    viewModelScope.launch(Dispatchers.IO) {
      hashInfoRepoDatabase.deleteAllHashInfo()
    }
  }
}