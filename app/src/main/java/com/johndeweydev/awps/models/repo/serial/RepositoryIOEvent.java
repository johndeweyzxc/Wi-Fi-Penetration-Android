package com.johndeweydev.awps.models.repo.serial;

import com.johndeweydev.awps.models.data.LauncherOutputData;

/**
 * Callbacks when a serial data is received from the launcher, the launcher is a usb serial device
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface RepositoryIOEvent {

  /**
   * Received raw launcher serial data
   * @param launcherOutputData contains the time in string and the launcher serial data in string
   * */
  default void onRepoOutputRaw(LauncherOutputData launcherOutputData) {}

  /**
   * Received launcher serial data that is formatted
   * @param launcherOutputData contains the time in string and the launcher serial data in string
   * */
  default void onRepoOutputFormatted(LauncherOutputData launcherOutputData) {}

  /**
   * An error occurred on the launcher
   * @param error the error message
   * */
  void onRepoOutputError(String error);

  /**
   * An input to the launcher serial causes an error
   * @param input the input in string that causes the error
   * */
  void onRepoInputError(String input);
}
