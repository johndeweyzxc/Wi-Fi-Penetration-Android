package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface SessionDeauthEvent {
  void onLauncherDeauthScannedAccessPoint(String scannedAccessPoint);
  void onLauncherDeauthFinishScanning();
  void onLauncherDeauthLaunchingSequence();
  void onLauncherDeauthStarted();
  void onLauncherDeauthInjectedDeauth(int numberOfDeauths);
  void onLauncherDeauthStopped(String targetAccessPoint);
}
