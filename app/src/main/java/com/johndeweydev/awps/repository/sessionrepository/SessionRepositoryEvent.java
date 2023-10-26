package com.johndeweydev.awps.repository.sessionrepository;

import androidx.annotation.Nullable;

import com.johndeweydev.awps.models.MicFirstMessageModel;
import com.johndeweydev.awps.models.MicSecondMessageModel;
import com.johndeweydev.awps.models.PmkidFirstMessageModel;
import com.johndeweydev.awps.repository.RepositorySerialDataEvent;

public interface SessionRepositoryEvent extends RepositorySerialDataEvent {

  // Is invoke only when the user ask what the current selected armament and the target bssid
  void onRepositoryCommandParserCurrentArma(String armament, String targetBssid);

  // Is invoke everytime the user sends instruction code
  void onRepositoryCommandParserTargetAndArmaSet(String armament, String targetBssid);
  void onRepositoryStarted();
  void onRepositoryArmamentActivation();
  void onRepositoryArmamentDeactivation();
  void onRepositoryNumberOfFoundAccessPoints(String numberOfAps);
  void onRepositoryScannedAccessPoint(String macAddress, String ssid, String rssi, String channel);
  void onRepositoryFinishScanning();
  void onRepositoryAccessPointNotFound();
  void onRepositoryLaunchingSequence();
  void onRepositoryTaskCreated();
  void onRepositoryPmkidWrongKeyType(String keyType);
  void onRepositoryTaskStatus(String attackType, int attackStatus);
  void onRepositoryEapolMessage(
          String attackType,
          int messageNumber,
          @Nullable PmkidFirstMessageModel pmkidFirstMessageModel,
          @Nullable MicFirstMessageModel micFirstMessageModel,
          @Nullable MicSecondMessageModel micSecondMessageModel);
  void onRepositoryFinishingSequence();
  void onRepositorySuccess();
  void onRepositoryFailure(String targetBssid);
  void onRepositoryDeauthStop(String targetBssid);
}
