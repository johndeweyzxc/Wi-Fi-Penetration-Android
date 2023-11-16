package com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase;

import com.johndeweydev.awps.models.data.AccessPointData;

public interface TargetLockingPhase {
  /**
   * The number of found access point, maximum is 20
   * @param numberOfAps the number of access points in string
   * */
  void onRepoNumberOfFoundAccessPoints(String numberOfAps);

  /**
   * The information about the found access point
   * @param accessPointData the data about the access point which contains the mac address, Service
   *                        Set Identifier, Received Signal Strength Indicator and the channel
   *                        used by the access point
   * */
  void onRepoFoundAccessPoint(AccessPointData accessPointData);

  /**
   * The launcher has finished scanning nearby access points
   * */
  void onRepoFinishScan();

  /**
   * The launcher did not found the target access point after it scanned nearby access points
   * and thus the attack will not proceed and it will not go through the launching sequence.
   * This is the only callback used by PMKID, MIC and DEAUTH based attack. RECONNAISSANCE
   * does not use this callback.
   * */
  void onRepoTargetAccessPointNotFound();
}
