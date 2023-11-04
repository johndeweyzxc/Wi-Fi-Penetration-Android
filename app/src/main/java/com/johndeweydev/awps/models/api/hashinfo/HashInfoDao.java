package com.johndeweydev.awps.models.api.hashinfo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.johndeweydev.awps.data.HashInfoEntity;

import java.util.List;

@Dao
public interface HashInfoDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  void addNewHashInfo(HashInfoEntity hashInfoEntity);
  @Query("SELECT * FROM hash_information")
  List<HashInfoEntity> getAllHashInfo();
  @Query("DELETE FROM hash_information")
  void deleteAllHashInfo();
}
