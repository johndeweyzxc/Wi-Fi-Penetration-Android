package com.johndeweydev.awps.models.repo.database;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.johndeweydev.awps.models.api.locationaware.LocationAwareSingleton;
import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoDao;

import java.io.IOException;
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

  public double getCurrentLocationLatitude() {
    return LocationAwareSingleton.getInstance().getLocationAware()
            .getLatitude();
  }

  public double getCurrentLocationLongitude() {
    return LocationAwareSingleton.getInstance().getLocationAware()
            .getLongitude();
  }

  public String getCurrentLocationAddress(double latitude, double longitude) {
    Geocoder geocoder = LocationAwareSingleton.getGeocoder();
    List<Address> addresses = null;

    try {
      assert geocoder != null;
      addresses = geocoder.getFromLocation(latitude, longitude, 1);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (addresses == null) {
      Log.d("dev-log", "HashInfoRepoDatabase.getCurrentLocationAddress: Addresses is " +
              "null");
      return "None";
    } else {
      if (addresses.size() == 0) {
        return "None";
      } else {
        return addresses.get(0).getAddressLine(0);
      }
    }
  }
}
