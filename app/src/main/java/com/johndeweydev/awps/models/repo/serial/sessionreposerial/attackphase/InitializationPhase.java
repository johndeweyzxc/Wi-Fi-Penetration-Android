package com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase;


public interface InitializationPhase {
  /**
   * The launcher is started after it resets
   * */
  void onRepoStarted();

  /**
   * A control code of armament status is issued
   * @param armament the armament currently loaded
   * @param targetBssid the mac address or BSSID of the target access point
   * */
  void onRepoArmamentStatus(String armament, String targetBssid);

  /**
   * An instruction code is issued by the user
   * @param armament the selected armament
   * @param targetBssid the mac address or BSSID of the target access point
   * */
  void onRepoInstructionIssued(String armament, String targetBssid);

  /**
   * The instruction code supplied by the user is activated
   * */
  void onRepoArmamentActivation();

  /**
   * A currently running or pre-running attack is deactivated
   * */
  void onRepoArmamentDeactivation();
}
