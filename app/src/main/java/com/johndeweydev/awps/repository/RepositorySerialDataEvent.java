package com.johndeweydev.awps.repository;

import com.johndeweydev.awps.data.LauncherOutputData;

/**
 * Interface for events occurred in the terminal repository and session repository
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface RepositorySerialDataEvent {

  /**
   * Received raw launcher serial data
   * @param launcherOutputData contains the time in string and the launcher serial data in string
   * */
  void onRepositoryOutputRaw(LauncherOutputData launcherOutputData);

  /**
   * Received launcher serial data that is formatted
   * @param launcherOutputData contains the time in string and the launcher serial data in string
   * */
  void onRepositoryOutputFormatted(LauncherOutputData launcherOutputData);

  /**
   * An error occurred on the launcher
   * @param error the error message
   * */
  void onRepositoryOutputError(String error);

  /**
   * An input to the launcher serial causes an error
   * @param input the input in string that causes the error
   * */
  void onRepositoryInputError(String input);
}
