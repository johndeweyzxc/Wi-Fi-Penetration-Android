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

  // The key type can be either "PMKID" in the case of PMKID based attack or "MIC" in the case
  // of MIC based attack
  @ColumnInfo(name = "key_type")
  public String keyType;

  // The hash data is where the actual PMKID or MIC is stored
  @ColumnInfo(name = "hash_data")
  public String hashData;

  // The anonce or access point nonce from the first eapol message, this is initialized in the
  // case of MIC based attack otherwise its value is "None" in the case of PMKID based attack
  @ColumnInfo(name = "a_nonce")
  public String aNonce;

  // The key data is where the second eapol authentication message is stored in the case of
  // MIC based attack otherwise its value is "None"
  @ColumnInfo(name = "key_data")
  public String keyData;
  @ColumnInfo(name = "date_captured")
  public String dateCaptured;

  public HashInfoEntity(
          @Nullable String ssid,
          @Nullable String bssid,
          @Nullable String clientMacAddress,
          @Nullable String keyType,
          @Nullable String hashData,
          @Nullable String aNonce,
          @Nullable String keyData,
          @Nullable String dateCaptured
  ) {
    this.ssid = ssid;
    this.bssid = bssid;
    this.clientMacAddress = clientMacAddress;
    this.keyType = keyType;
    this.hashData = hashData;
    this.aNonce = aNonce;
    this.keyData = keyData;
    this.dateCaptured = dateCaptured;
  }
}
