package com.johndeweydev.awps.launcher;

public enum LauncherStages {
  // Initialization stage
  ALREADY_CONNECTED,

  // Driver and device stage
  DEVICE_NOT_FOUND,
  DRIVER_NOT_FOUND,
  PORT_NOT_FOUND,
  DRIVER_SET,

  // Permission stage
  NO_USB_PERMISSION,

  // Connection stage
  SUCCESSFULLY_CONNECTED,
  UNSUPPORTED_PORT_PARAMETERS,
  FAILED_OPENING_DEVICE
}
