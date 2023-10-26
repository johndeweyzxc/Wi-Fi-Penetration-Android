package com.johndeweydev.awps.launcher;

public interface LauncherEvent {
  void onLauncherOutput(String data);
  void onLauncherOutputError(String error);
  void onLauncherInputError(String input);
}
