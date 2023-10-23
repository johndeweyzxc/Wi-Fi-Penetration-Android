package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface SessionReconEvent {
  void onLauncherReconScannedAccessPoint(String scannedAccessPoint);
  void onLauncherReconFinishScanning();
}
