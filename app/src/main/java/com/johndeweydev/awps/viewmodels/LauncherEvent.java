package com.johndeweydev.awps.viewmodels;

public interface LauncherEvent extends LauncherPmkidEvent, LauncherMicEvent,
        LauncherReconEvent, LauncherDeauthEvent {
  void onLauncherStarted();
  void onLauncherArmamentActivation();
  void onLauncherArmamentDeactivation();
}
