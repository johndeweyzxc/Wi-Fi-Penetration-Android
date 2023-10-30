package com.johndeweydev.awps.repository.sessionrepository;

import androidx.annotation.Nullable;

import com.johndeweydev.awps.data.MicFirstMessageData;
import com.johndeweydev.awps.data.MicSecondMessageData;
import com.johndeweydev.awps.data.PmkidFirstMessageData;
import com.johndeweydev.awps.repository.RepositorySerialEvent;

/**
 * Interface for events occurred in the session repository, it handles formatted launcher serial
 * data. This are event callbacks that are executed when a certain type of launcher serial data
 * is received.
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface SessionRepositoryEvent extends RepositorySerialEvent {

  /**
   * A control code of armament status is issued
   * @param armament the armament currently loaded
   * @param targetBssid the mac address or BSSID of the target access point
   * */
  void onRepositoryCommandParserCurrentArma(String armament, String targetBssid);

  /**
   * An instruction code is issued by the user
   * @param armament the selected armament
   * @param targetBssid the mac address or BSSID of the target access point
   * */
  void onRepositoryCommandParserTargetAndArmaSet(String armament, String targetBssid);

  /**
   * The launcher is started after it resets
   * */
  void onRepositoryStarted();

  /**
   * The instruction code supplied by the user is activated
   * */
  void onRepositoryArmamentActivation();

  /**
   * A currently running or pre-running attack is deactivated
   * */
  void onRepositoryArmamentDeactivation();

  /**
   * The number of found access point, maximum is 20
   * @param numberOfAps the number of access points in string
   * */
  void onRepositoryNumberOfFoundAccessPoints(String numberOfAps);

  /**
   * The information about the found access point
   * @param macAddress the mac address of the access point without colon
   * @param ssid Service Set Identifier or the name of the access point
   * @param rssi Received Signal Strength Indicator or the strength of the signal of access point
   * @param channel the channel used by the access point
   * */
  void onRepositoryScannedAccessPoint(String macAddress, String ssid, String rssi, String channel);

  /**
   * The launcher has finished scanning nearby access points
   * */
  void onRepositoryFinishScanning();

  /**
   * The launcher did not found the target access point after it scanned nearby access points
   * and thus the attack will not proceed and it will not go through the launching sequence
   * */
  void onRepositoryAccessPointNotFound();

  /**
   * The launcher proceeds to the launching sequence which invokes function in the launcher
   * needed to successfully execute the attack. For example promiscuous mode is turned on
   * */
  void onRepositoryLaunchingSequence();

  /**
   * The main task is created in the launcher after the launching sequence, for example in MIC
   * based attack a deauther task is created
   * */
  void onRepositoryTaskCreated();

  /**
   * While a PMKID based attack is ongoing, a wrong PMKID key type is encountered. This sometimes
   * happens in the case of attacking a Wi-Fi hotspot
   * @param keyType the wrong key type received
   * */
  void onRepositoryPmkidWrongKeyType(String keyType);

  /**
   * The status of the main task
   * @param attackType the type of attack which could be PMKID, MIC or Deauther
   * @param attackStatus the status of the attack in integer, this can be an elapsed time or the
   * number of injected deauthentication frame
   * */
  void onRepositoryTaskStatus(String attackType, int attackStatus);

  /**
   * The PMKID or MIC is received and other important information
   * @param attackType the type of attack which could be PMKID, MIC, Deauther
   * @param messageNumber 1 or 2, which corresponds to the 4 way handshake
   * @param pmkidFirstMessageData contains the BSSID, mac address of client, and the PMKID
   * @param micFirstMessageData contains the anonce and other information
   * @param micSecondMessageData contains the snonce, MIC and other information
   * */
  void onRepositoryEapolMessage(
          String attackType,
          int messageNumber,
          @Nullable PmkidFirstMessageData pmkidFirstMessageData,
          @Nullable MicFirstMessageData micFirstMessageData,
          @Nullable MicSecondMessageData micSecondMessageData);

  /**
   * The launcher goes to the finishing state where functions invoke in the launching sequence
   * is reverted back to its original state. For example promiscuous mode is turned off
   * */
  void onRepositoryFinishingSequence();

  /**
   * The launcher successfully executed the attack and exited without any error
   * */
  void onRepositorySuccess();

  /**
   * The launcher failed to execute the attack or it successfully executed the attack but an error
   * occurred later on which requires the launcher to be restarted
   * @param targetBssid the target access point that the launcher were unable to penetrate
   * */
  void onRepositoryFailure(String targetBssid);

  /**
   * The main task when using Deauther is stopped, this task continually injects deauthentication
   * frame
   * @param targetBssid the target access point that is receiving the deauthentication frame
   * */
  void onRepositoryDeauthStop(String targetBssid);
}
