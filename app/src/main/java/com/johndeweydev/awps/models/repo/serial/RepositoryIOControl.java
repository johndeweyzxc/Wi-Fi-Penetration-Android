package com.johndeweydev.awps.models.repo.serial;

import com.johndeweydev.awps.models.data.DeviceConnectionParamData;
import com.johndeweydev.awps.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.api.launcher.LauncherConnectionStages;

public class RepositoryIOControl {

  public String connectToDevice(DeviceConnectionParamData deviceConnectionParamData) {
    LauncherConnectionStages status = LauncherSingleton.getInstance().getLauncher()
            .connectToDevice(deviceConnectionParamData);

    return switch (status) {
      case ALREADY_CONNECTED -> "Already connected";
      case DEVICE_NOT_FOUND -> "Device not found";
      case DRIVER_NOT_FOUND -> "Driver not found";
      case PORT_NOT_FOUND -> "Port not found";
      case NO_USB_PERMISSION -> "No usb permission";
      case SUCCESSFULLY_CONNECTED -> "Successfully connected";
      case UNSUPPORTED_PORT_PARAMETERS -> "Unsupported port parameters";
      case FAILED_OPENING_DEVICE -> "Failed to open the device";
      default -> "None";
    };
  }
  public void disconnectFromDevice() {
    LauncherSingleton.getInstance().getLauncher().disconnectFromDevice();
  }
  public void startEventDrivenReadFromDevice() {
    LauncherSingleton.getInstance().getLauncher().startEventDrivenReadFromDevice();
  }
  public void stopEventDrivenReadFromDevice() {
    LauncherSingleton.getInstance().getLauncher().stopEventDrivenReadFromDevice();
  }
  public void writeDataToDevice(String data) {
    LauncherSingleton.getInstance().getLauncher().writeDataToDevice(data);
  }
}
