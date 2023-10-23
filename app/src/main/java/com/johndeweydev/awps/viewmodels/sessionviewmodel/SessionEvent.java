package com.johndeweydev.awps.viewmodels.sessionviewmodel;

public interface SessionEvent extends SessionPmkidEvent, SessionMicEvent,
        SessionReconEvent, SessionDeauthEvent {
  void onLauncherStarted();
  void onLauncherArmamentActivation();
  void onLauncherArmamentDeactivation();
}
