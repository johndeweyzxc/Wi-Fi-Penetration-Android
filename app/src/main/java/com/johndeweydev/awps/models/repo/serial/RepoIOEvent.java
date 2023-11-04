package com.johndeweydev.awps.models.repo.serial;

/**
 * Callbacks for input and output from the usb serial device as well as callbacks when an error
 * occurred
 *
 * @author John Dewey (johndewey02003@gmail.com)
 *
 * */
public interface RepoIOEvent {

  /**
   * A serial output is received
   * @param data the data in string outputted by the serial
   * */
  void onUsbSerialOutput(String data);

  /**
   * An error occurred in the serial
   * @param error the error message
   * */
  void onUsbSerialOutputError(String error);

  /**
   * A user input causes serial error
   * @param input the string input that causes the error
   * */
  void onUsbSerialInputError(String input);
}
