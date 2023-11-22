package com.johndeweydev.awps.model.repo.serial.sessionreposerial.attackphase;

import androidx.annotation.Nullable;

import com.johndeweydev.awps.model.data.MicFirstMessageData;
import com.johndeweydev.awps.model.data.MicSecondMessageData;
import com.johndeweydev.awps.model.data.PmkidFirstMessageData;

public interface ExecutionPhase {
  /**
   * The launcher proceeds to the launching sequence which invokes function in the launcher
   * needed to successfully execute the attack. For example promiscuous mode is turned on
   * */
  void onRepoLaunchingSequence();

  /**
   * The main task is created in the launcher after the launching sequence, for example in MIC
   * based attack a deauther task is created
   * */
  void onRepoMainTaskCreated();

  /**
   * While a PMKID based attack is ongoing, a wrong PMKID key type is encountered. This sometimes
   * happens in the case of attacking a Wi-Fi hotspot
   * @param keyType the wrong key type received
   * */
  void onRepoPmkidWrongKeyType(String keyType);

  /**
   * Got wrong PMKID key data OUI, the launcher is programmed that the correct OUI is 0x000fac
   * @param oui the OUI that is found to be wrong
   * */
  void onRepoPmkidWrongOui(String oui);

  /**
   * Got wrong PMKID key data type KDE, the launcher is programmed that the correct KDE is 4
   * @param kde the KDE that is found to be wrong
   * */
  void onRepoPmkidWrongKde(String kde);

  /**
   * The status of the main task
   * @param attackType the type of attack which could be PMKID, MIC or Deauther
   * @param attackStatus the status of the attack in integer, this can be an elapsed time or the
   *                     number of injected deauthentication frame
   * */
  void onRepoMainTaskCurrentStatus(String attackType, int attackStatus);

  /**
   * The PMKID or MIC is received and other important information
   * @param attackType the type of attack which could be PMKID, MIC, Deauther
   * @param messageNumber 1 or 2, which corresponds to the 4 way handshake
   * @param pmkidFirstMessageData contains the BSSID, mac address of client, and the PMKID
   * @param micFirstMessageData contains the anonce and other information
   * @param micSecondMessageData contains the snonce, MIC and other information
   * */
  void onRepoReceivedEapolMessage(
          String attackType,
          int messageNumber,
          @Nullable PmkidFirstMessageData pmkidFirstMessageData,
          @Nullable MicFirstMessageData micFirstMessageData,
          @Nullable MicSecondMessageData micSecondMessageData);
}
