package com.johndeweydev.awps.models.repo.serial.sessionreposerial.attackphase;

public interface PostExecutionPhase {
  /**
   * The launcher goes to the finishing state where functions invoke in the launching sequence
   * is reverted back to its original state. For example promiscuous mode is turned off
   * */
  void onRepoFinishingSequence();

  /**
   * The launcher successfully executed the attack and exited without any error
   * */
  void onRepoSuccess();

  /**
   * The launcher failed to execute the attack or it successfully executed the attack but an error
   * occurred later on which requires the launcher to be restarted
   * @param targetBssid the target access point that the launcher were unable to penetrate
   * */
  void onRepoFailure(String targetBssid);

  /**
   * The main task when using Deauther is stopped, this task continually injects deauthentication
   * frame
   * @param targetBssid the target access point that is receiving the deauthentication frame
   * */
  void onRepoMainTaskInDeautherStopped(String targetBssid);
}
