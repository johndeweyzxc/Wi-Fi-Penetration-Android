package com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase;

import com.johndeweydev.awps.models.data.AccessPointData;

/**
 * Callbacks when the launcher goes into the target locking phase, this happens when the user
 * activates the armament. The launcher then tries to find the target access point by initiating a
 * scan, if it finds the target among those scanned access point, it goes into execution phase
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface TargetLockingPhase {
  /**
   * The number of found access point, maximum is 20
   * @param numberOfAps the number of access points in string
   * */
  void onLauncherNumberOfFoundAccessPoints(String numberOfAps);

  /**
   * The information about the found access point
   * @param accessPointData the data about the access point which contains the mac address, Service
   *                        Set Identifier, Received Signal Strength Indicator and the channel
   *                        used by the access point
   * */
  void onLauncherFoundAccessPoint(AccessPointData accessPointData);

  /**
   * The launcher has finished scanning nearby access points
   * */
  void onLauncherFinishScan();

  /**
   * The launcher did not found the target access point after it scanned nearby access points
   * and thus the attack will not proceed and it will not go through the launching sequence
   * */
  void onLauncherTargetAccessPointNotFound();
}
