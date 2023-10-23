package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface LauncherEvent extends LauncherPmkidEvent, LauncherMicEvent,
        LauncherReconEvent, LauncherDeauthEvent {
  void onLauncherStarted();
  void onLauncherArmamentActivation();
  void onLauncherArmamentDeactivation();
}
