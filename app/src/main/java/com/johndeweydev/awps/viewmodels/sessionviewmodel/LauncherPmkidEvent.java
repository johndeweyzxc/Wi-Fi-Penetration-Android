package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface LauncherPmkidEvent {
  void onLauncherPmkidScannedAccessPoint(String scannedAccessPoint);
  void onLauncherPmkidFinishScanning();
  void onLauncherPmkidLaunchingSequence();
  void onLauncherPmkidSniffStarted(String targetAccessPoint);
  void onLauncherPmkidFound(String pmkid);
  void onLauncherPmkidFinishingSequence();
  void onLauncherPmkidSuccess(String targetAccessPoint);
  void onLauncherPmkidFailed(String targetAccessPoint);
}
