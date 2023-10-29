package com.johndeweydev.awps.launcher;

/**
 * Interface for any events occurred in the usb serial
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface LauncherEvent {

  /**
   * A serial output is received
   * @param data the data in string outputted by the serial
   * */
  void onLauncherOutput(String data);

  /**
   * An error occurred in the serial
   * @param error the error message
   * */
  void onLauncherOutputError(String error);

  /**
   * A user input causes serial error
   * @param input the string input that causes the error
   * */
  void onLauncherInputError(String input);
}
