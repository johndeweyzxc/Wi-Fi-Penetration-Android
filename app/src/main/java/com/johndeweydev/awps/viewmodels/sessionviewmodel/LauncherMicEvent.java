package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface LauncherMicEvent {
  void onLauncherMicScannedAccessPoint(String scannedAccessPoint);
  void onLauncherMicFinishScanning();
  void onLauncherMicLaunchingSequence();
  void onLauncherMicDeauthStarted(String targetAccessPoint);
  void onLauncherMicInjectedDeauth(int numberOfDeauths);
  void onLauncherMicMessage1(String eapolMessage1AuthData);
  void onLauncherMicMessage2(String eapolMessage2AuthData);
  void onLauncherMicFinishingSequence();
  void onLauncherMicSuccess();
  void onLauncherMicFailed(String targetAccessPoint);
}
