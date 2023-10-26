package com.johndeweydev.awps.repository;

import com.johndeweydev.awps.models.LauncherOutputModel;

public interface RepositorySerialDataEvent {

  void onRepositoryOutputRaw(LauncherOutputModel launcherOutputModel);
  void onRepositoryOutputFormatted(LauncherOutputModel launcherOutputModel);
  void onRepositoryOutputError(String error);
  void onRepositoryInputError(String input);
}
