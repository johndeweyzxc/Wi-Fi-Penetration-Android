package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface LauncherReconEvent {
  void onLauncherReconScannedAccessPoint(String scannedAccessPoint);
  void onLauncherReconFinishScanning();
}
