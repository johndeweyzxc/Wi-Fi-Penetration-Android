package com.johndeweydev.awps.repository;

import com.johndeweydev.awps.data.LauncherOutputData;

public interface RepositorySerialDataEvent {

  void onRepositoryOutputRaw(LauncherOutputData launcherOutputData);
  void onRepositoryOutputFormatted(LauncherOutputData launcherOutputData);
  void onRepositoryOutputError(String error);
  void onRepositoryInputError(String input);
}
