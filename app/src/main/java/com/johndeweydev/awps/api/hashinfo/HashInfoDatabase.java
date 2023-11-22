package com.johndeweydev.awps.api.hashinfo;

import android.util.Log;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.johndeweydev.awps.model.data.HashInfoEntity;

@Database(entities = {HashInfoEntity.class}, version = 1, exportSchema = false)
public abstract class HashInfoDatabase extends RoomDatabase {
  public abstract HashInfoDao hashInfoDao();

  public HashInfoDatabase() {
    Log.w("dev-log", "HashInfoDatabase: Created new instance of HashInfoDatabase");
  }
}
