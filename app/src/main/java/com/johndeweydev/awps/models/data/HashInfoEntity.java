package com.johndeweydev.awps.models.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hash_information")
public class HashInfoEntity {
  @PrimaryKey(autoGenerate = true)
  public int uid;
  @ColumnInfo(name = "ssid")
  public String ssid;
  @ColumnInfo(name = "bssid")
  public String bssid;
  @ColumnInfo(name = "client_mac_address")
  public String clientMacAddress;
  @ColumnInfo(name = "key_type")
  public String keyType;
  @ColumnInfo(name = "hash_data")
  // This is where the PMKID or MIC is stored
  public String hashData;
  @ColumnInfo(name = "key_data")
  // The key data is where the second eapol authentication message is stored in the case of
  // MIC based attack
  public String keyData;
  @ColumnInfo(name = "date_captured")
  public String dateCaptured;

  public HashInfoEntity(
          @Nullable String ssid,
          @Nullable String bssid,
          @Nullable String clientMacAddress,
          @Nullable String keyType,
          @Nullable String hashData,
          @Nullable String keyData,
          @Nullable String dateCaptured
  ) {
    this.ssid = ssid;
    this.bssid = bssid;
    this.clientMacAddress = clientMacAddress;
    this.keyType = keyType;
    this.hashData = hashData;
    this.keyData = keyData;
    this.dateCaptured = dateCaptured;
  }
}
