package com.johndeweydev.awps.models.api.hashinfo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.johndeweydev.awps.data.HashInfoEntity;

@Database(entities = {HashInfoEntity.class}, version = 1, exportSchema = false)
public abstract class HashInfoDatabase extends RoomDatabase {
  public abstract HashInfoDao hashInfoDao();
}
