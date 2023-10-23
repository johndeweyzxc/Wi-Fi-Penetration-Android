package com.johndeweydev.awps.viewmodels;

public interface LauncherDeauthEvent {
  void onLauncherDeauthScannedAccessPoint(String scannedAccessPoint);
  void onLauncherDeauthFinishScanning();
  void onLauncherDeauthLaunchingSequence();
  void onLauncherDeauthStarted();
  void onLauncherDeauthInjectedDeauth(int numberOfDeauths);
  void onLauncherDeauthStopped(String targetAccessPoint);
}
