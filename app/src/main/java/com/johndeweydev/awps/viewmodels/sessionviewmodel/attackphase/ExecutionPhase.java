package com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase;

import androidx.annotation.Nullable;

import com.johndeweydev.awps.models.data.MicFirstMessageData;
import com.johndeweydev.awps.models.data.MicSecondMessageData;
import com.johndeweydev.awps.models.data.PmkidFirstMessageData;

/**
 * Callbacks when the launcher goes into execution phase, this happens when the launcher initiates
 * the attack on the target access point and the launcher may or may not intercept the eapol message
 * data from the target which contains crackable hash data
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface ExecutionPhase {
  /**
   * The launcher proceeds to the launching sequence which invokes function in the launcher
   * needed to successfully execute the attack. For example promiscuous mode is turned on
   * */
  void onLauncherLaunchingSequence();

  /**
   * The main task is created in the launcher after the launching sequence, for example in MIC
   * based attack a deauther task is created
   * */
  void onLauncherMainTaskCreated();

  /**
   * While a PMKID based attack is ongoing, a wrong PMKID key type is encountered. This sometimes
   * happens in the case of attacking a Wi-Fi hotspot
   * @param keyType the wrong key type received
   * */
  void onLauncherPmkidWrongKeyType(String keyType);

  /**
   * Got wrong PMKID key data OUI, the launcher is programmed that the correct OUI is 0x000fac
   * @param oui the OUI that is found to be wrong
   * */
  void onLauncherPmkidWrongOui(String oui);

  /**
   * Got wrong PMKID key data type KDE, the launcher is programmed that the correct KDE is 4
   * @param kde the KDE that is found to be wrong
   * */
  void onLauncherPmkidWrongKde(String kde);

  /**
   * The value of IV or RSC or ID in the authentication data of the second message of 4 way
   * handshake is not set to all zero, normally it should be set to all zero
   * @param ivRscId outputs which of the three attribute IV, RSC and ID is not set to zero
   * */
  void onLauncherMicIvRscIdNotSetToZero(String ivRscId);

  /**
   * The status of the main task
   * @param attackType the type of attack which could be PMKID, MIC or Deauther
   * @param attackStatus the status of the attack in integer, this can be an elapsed time or the
   *                     number of injected deauthentication frame
   * */
  void onLauncherMainTaskCurrentStatus(String attackType, int attackStatus);

  /**
   * The PMKID or MIC is received and other important information
   * @param attackType the type of attack which could be PMKID, MIC, Deauther
   * @param messageNumber 1 or 2, which corresponds to the 4 way handshake
   * @param pmkidFirstMessageData contains the BSSID, mac address of client, and the PMKID
   * @param micFirstMessageData contains the anonce and other information
   * @param micSecondMessageData contains the snonce, MIC and other information
   * */
  void onLauncherReceivedEapolMessage(
          String attackType,
          int messageNumber,
          @Nullable PmkidFirstMessageData pmkidFirstMessageData,
          @Nullable MicFirstMessageData micFirstMessageData,
          @Nullable MicSecondMessageData micSecondMessageData);
}
