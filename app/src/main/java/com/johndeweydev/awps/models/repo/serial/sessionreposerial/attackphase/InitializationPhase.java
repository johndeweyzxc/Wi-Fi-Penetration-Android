package com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase;

/**
 * Callbacks when the launcher goes to initialization phase, this happens when the launcher is
 * started or when the previous attack has finished and is ready to receive instruction code which
 * contains the information such as the target and the selected attack type or armament.
 * It then asks the user to activate the armament
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
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
