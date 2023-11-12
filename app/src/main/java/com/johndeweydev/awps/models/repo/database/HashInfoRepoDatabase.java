package com.johndeweydev.awps.models.repo.database;

import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoDao;

import java.util.List;

public class HashInfoRepoDatabase {

  private final HashInfoDao hashInfoDao;

  public HashInfoRepoDatabase(HashInfoDao hashInfoDao) {
    this.hashInfoDao = hashInfoDao;
  }

  public void addNewHashInfo(HashInfoEntity hashInfoEntity) {
    hashInfoDao.addNewHashInfo(hashInfoEntity);
  }

  public List<HashInfoEntity> getAllHashInfo() {
    return hashInfoDao.getAllHashInfo();
  }

  public void deleteAllHashInfo() {
    hashInfoDao.deleteAllHashInfo();
  }

  public void deleteHashInfo(HashInfoEntity hashInfoEntity) {
    hashInfoDao.deleteHashInfo(hashInfoEntity);
  }

  public HashInfoEntity getHashInfoBySsidAndBssidAndHashData(
          String ssid, String bssid, String hashData) {
    return hashInfoDao.getHashInfoBySsidAndBssidAndHashData(ssid, bssid, hashData);
  }
}
