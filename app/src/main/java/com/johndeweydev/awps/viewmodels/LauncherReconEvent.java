package com.johndeweydev.awps.viewmodels;

public interface LauncherReconEvent {
  void onLauncherReconScannedAccessPoint(String scannedAccessPoint);
  void onLauncherReconFinishScanning();
}
