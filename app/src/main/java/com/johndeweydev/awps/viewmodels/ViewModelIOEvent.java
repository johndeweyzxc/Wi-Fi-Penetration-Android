package com.johndeweydev.awps.viewmodels;

import com.johndeweydev.awps.models.data.LauncherOutputData;

/**
 * Callbacks when a serial data is received from the launcher, the launcher is a usb serial device
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface ViewModelIOEvent {

  /**
   * Received raw launcher serial data
   * @param launcherOutputData contains the time in string and the launcher serial data in string
   * */
  default void onLauncherOutputRaw(LauncherOutputData launcherOutputData) {}

  /**
   * Received launcher serial data that is formatted
   * @param launcherOutputData contains the time in string and the launcher serial data in string
   * */
  void onLauncherOutputFormatted(LauncherOutputData launcherOutputData);

  /**
   * An error occurred on the launcher
   * @param error the error message
   * */
  void onLauncherOutputError(String error);

  /**
   * An input to the launcher serial causes an error
   * @param input the input in string that causes the error
   * */
  void onLauncherInputError(String input);
}
