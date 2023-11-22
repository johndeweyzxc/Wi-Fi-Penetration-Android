package com.johndeweydev.awps.api.hashinfo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.johndeweydev.awps.model.data.HashInfoEntity;

import java.util.List;

@Dao
public interface HashInfoDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  void addNewHashInfo(HashInfoEntity hashInfoEntity);
  @Query("SELECT * FROM hash_information")
  List<HashInfoEntity> getAllHashInfo();
  @Query("DELETE FROM hash_information")
  void deleteAllHashInfo();
  @Delete
  void deleteHashInfo(HashInfoEntity hashInfoEntity);
  @Query("SELECT * FROM hash_information " +
          "WHERE ssid = :ssid AND bssid = :bssid AND hash_data = :hashData LIMIT 1")
  HashInfoEntity getHashInfoBySsidAndBssidAndHashData(String ssid, String bssid, String hashData);
}
