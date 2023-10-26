package com.johndeweydev.awps.viewmodels.sessionviewmodel;

import androidx.annotation.Nullable;

import com.johndeweydev.awps.repository.sessionrepository.models.SessionOutputModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicFirstMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.MicSecondMessageModel;
import com.johndeweydev.awps.repository.sessionrepository.models.PmkidFirstMessageModel;

public interface SessionEvent {
  void onSerialOutputRaw(SessionOutputModel sessionOutputModel);
  void onSerialOutputFormatted(SessionOutputModel sessionOutputModel);
  void onSerialOutputError(String errorMessageOnNewData);
  void onSerialInputError(String dataToWrite);

  // Is invoke only when the user ask what the current selected armament and the target bssid
  void onLauncherCommandParserCurrentArma(String armament, String targetBssid);

  // Is invoke everytime the user sends instruction code
  void onLauncherCommandParserTargetAndArmaSet(String armament, String targetBssid);
  void onLauncherStarted();
  void onLauncherArmamentActivation();
  void onLauncherArmamentDeactivation();
  void onLauncherNumberOfFoundAccessPoints(String numberOfAps);
  void onLauncherScannedAccessPoint(String macAddress, String ssid, String rssi, String channel);
  void onLauncherFinishScanning();
  void onLauncherAccessPointNotFound();
  void onLauncherLaunchingSequence();
  void onLauncherTaskCreated();
  void onLauncherPmkidWrongKeyType(String keyType);
  void onLauncherTaskStatus(String attackType, int attackStatus);
  void onLauncherEapolMessage(
          String attackType,
          int messageNumber,
          @Nullable PmkidFirstMessageModel pmkidFirstMessageModel,
          @Nullable MicFirstMessageModel micFirstMessageModel,
          @Nullable MicSecondMessageModel micSecondMessageModel);
  void onLauncherFinishingSequence();
  void onLauncherSuccess();
  void onLauncherFailure(String targetBssid);
  void onLauncherDeauthStop(String targetBssid);
}
