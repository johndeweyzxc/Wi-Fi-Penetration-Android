package com.johndeweydev.awps.viewmodels.sessionviewmodel.attackphase;

/**
 * This callback is also known as the cleanup phase. In this phase the attack on the target has
 * finished or the running attack is stopped by the user. The result of the attack may or may not
 * be successful. This stage is also the right time to clean the view model of the data because
 * after this phase, it will go back to initialization phase
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface PostExecutionPhase {
  /**
   * The launcher goes to the finishing state where functions invoke in the launching sequence
   * is reverted back to its original state. For example promiscuous mode is turned off
   * */
  void onLauncherFinishingSequence();

  /**
   * The launcher successfully executed the attack and exited without any error
   * */
  void onLauncherSuccess();

  /**
   * The launcher failed to execute the attack or it successfully executed the attack but an error
   * occurred later on which requires the launcher to be restarted
   * @param targetBssid the target access point that the launcher were unable to penetrate
   * */
  void onLauncherFailure(String targetBssid);

  /**
   * The main task when using Deauther is stopped, this task continually injects deauthentication
   * frame
   * @param targetBssid the target access point that is receiving the deauthentication frame
   * */
  void onLauncherMainTaskInDeautherStopped(String targetBssid);
}
