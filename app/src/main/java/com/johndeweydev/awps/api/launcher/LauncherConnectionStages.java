package com.johndeweydev.awps.api.launcher;

public enum LauncherConnectionStages {
  /**
   * INITIALIZATION STAGE
   * */
  ALREADY_CONNECTED,

  /**
   * DRIVER AND DEVICE DISCOVERY STAGE
   * */
  DEVICE_NOT_FOUND,
  DRIVER_NOT_FOUND,
  PORT_NOT_FOUND,
  DRIVER_SET,

  /**
   * PERMISSION STAGE
   * */
  NO_USB_PERMISSION,


  /**
   * OPENING STAGE
   * */
  SUCCESSFULLY_CONNECTED,
  UNSUPPORTED_PORT_PARAMETERS,
  FAILED_OPENING_DEVICE
}
